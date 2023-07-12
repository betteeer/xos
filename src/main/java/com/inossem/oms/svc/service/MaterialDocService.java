package com.inossem.oms.svc.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inossem.oms.api.bk.api.BookKeepingService;
import com.inossem.oms.base.common.constant.ModuleConstant;
import com.inossem.oms.base.svc.domain.*;
import com.inossem.oms.base.svc.domain.VO.SkuListReqVO;
import com.inossem.oms.base.svc.mapper.BkCoaRelMapper;
import com.inossem.oms.base.svc.mapper.MaterialDocMapper;
import com.inossem.oms.base.svc.mapper.MovementTypeMapper;
import com.inossem.oms.base.svc.mapper.StockBalanceMapper;
import com.inossem.oms.base.svc.vo.*;
import com.inossem.oms.base.utils.NumberWorker;
import com.inossem.oms.base.utils.UserInfoUtils;
import com.inossem.oms.mdm.service.CompanyService;
import com.inossem.oms.mdm.service.SkuService;
import com.inossem.oms.mdm.service.WarehouseService;
import com.inossem.sco.common.core.exception.ServiceException;
import com.inossem.sco.common.core.utils.DateUtils;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.*;

@Service
@Slf4j
public class MaterialDocService {

    @Resource
    private MaterialDocMapper materialDocMapper;

    @Resource
    private MovementTypeMapper movementTypeMapper;

    @Resource
    private StockBalanceMapper stockBalanceMapper;

    @Resource
    private NumberWorker numberWorker;

    @Resource
    private BkCoaRelMapper bkCoaRelMapper;

    @Resource
    private BookKeepingService bookKeepingService;

    @Resource
    private SkuService skuService;

    @Resource
    private WarehouseService warehouseService;

    @Resource
    private CompanyService companyService;

    private static final String[] sceneB = {"201", "601", "551", "701", "702", "651"};  //场景B的处理

    private static final String[] sceneC = {"201", "551", "701", "702"};  //领用  盘盈  盘亏  报废

    public List<QueryMaterialDocResVo> list(QueryMaterialDocListVo queryMaterialDocListVo) {
        log.info("开始查询物料凭证,传入参数:[{}]", queryMaterialDocListVo);

        if (StringUtils.isNotBlank(queryMaterialDocListVo.getSearchText())) {
            // 根据Name查询Sku列表
            SkuListReqVO skuListReqVO = new SkuListReqVO();
            skuListReqVO.setSkuName(queryMaterialDocListVo.getSearchText());
            skuListReqVO.setCompanyCode(queryMaterialDocListVo.getCompanyCode());
            List<SkuMaster> skuMasters = skuService.getFeignList(skuListReqVO);
            log.info("查询skuMastersByName结果:[{}]", skuMasters);
            queryMaterialDocListVo.setSkuMasters(skuMasters);
        }
        List<QueryMaterialDocResVo> materialDocs = materialDocMapper.selectListByQueryParam(queryMaterialDocListVo);
        for (int i = 0; i < materialDocs.size(); i++) {
            SkuMaster skuMaster = skuService.getSku(materialDocs.get(i).getSkuNumber(), null, materialDocs.get(i).getCompanyCode());
            log.info("查询skuMaster结果:[{}]", skuMaster);
            if (!StringUtils.isNull(skuMaster)) {
                materialDocs.get(i).setSkuName(skuMaster.getSkuName());
            }
            Warehouse warehouse = warehouseService.getWarehouse(materialDocs.get(i).getCompanyCode(), materialDocs.get(i).getWarehouseCode());
            log.info("查询warehouse结果:[{}]", warehouse);
            if (!StringUtils.isNull(warehouse)) {
                materialDocs.get(i).setWarehouseName(warehouse.getName());
            }
        }
        log.info("结束查询物料凭证,查询结果:[{}]", materialDocs);
        return materialDocs;
    }

    /**
     * 创建物料凭证
     *
     * @param createMaterialDocVo
     * @return
     */
    @Transactional
    public List<MaterialDoc> add(CreateMaterialDocVo createMaterialDocVo) throws ServiceException {
        List<MaterialDoc> materialDocList = new ArrayList<>();
        //获取用户信息
        Long userId = Long.valueOf(UserInfoUtils.getSysUserId());
        Date createTime = new Date();
        //根据movementType查询类型信息
        LambdaQueryWrapper<MovementType> movementTypeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        movementTypeLambdaQueryWrapper.eq(MovementType::getMovementType, createMaterialDocVo.getMovementType()).last("limit 1");
        MovementType movementType = movementTypeMapper.selectOne(movementTypeLambdaQueryWrapper);
        if (StringUtils.isNull(movementType)) {
            throw new ServiceException("移动类型不存在");
        }
        //判断当前是否冻结库存
        log.info("库存冻结状态，移动类型本身冻结状态:[{}],传入的冻结状态:[{}]", movementType.getIsBlockAllowed(), createMaterialDocVo.getStockStatus());
        if (ModuleConstant.IS_BOLCK_ALLOWED.ALLOWED == movementType.getIsBlockAllowed() && ModuleConstant.STOCK_STATUS.STOCK.equals(createMaterialDocVo.getStockStatus())) {
            movementType.setIsBlockAllowed(ModuleConstant.IS_BOLCK_ALLOWED.ALLOWED);
        } else {
            movementType.setIsBlockAllowed(ModuleConstant.IS_BOLCK_ALLOWED.NO_ALLOWED);
        }
        //生成DocNumber
        Long docNumber = numberWorker.generateId(createMaterialDocVo.getCompanyCode(), ModuleConstant.ORDER_NUMBER_TYPE.MATERIAL_DOC);
        //根据查出的移动类型movementType进行库存处理
        //先查询库存中是否已初始化此sku
        for (int i = 0; i < createMaterialDocVo.getCreateMaterialDocSkuVoList().size(); i++) {
            // 当前操作的sku的数量 -- 前端或相关调用该接口传入
            BigDecimal skuQty = createMaterialDocVo.getCreateMaterialDocSkuVoList().get(i).getSkuQty();

            //增量总价 -- 当前操作的sku的totalAmount  -- increTotalAmount -- 前端或相关调用该接口传入
            BigDecimal increTotalAmount = createMaterialDocVo.getCreateMaterialDocSkuVoList().get(i).getTotalAmount();

            if (increTotalAmount == null && createMaterialDocVo.getCreateMaterialDocSkuVoList().get(i).getItemAmount() != null) {
                increTotalAmount = createMaterialDocVo.getCreateMaterialDocSkuVoList().get(i).getItemAmount().multiply(createMaterialDocVo.getCreateMaterialDocSkuVoList().get(i).getSkuQty()).setScale(2, BigDecimal.ROUND_HALF_UP);
            }

            log.info(">>>>>>增量总价 -- 当前操作的sku的totalAmount  -- increTotalAmount -- 前端或相关调用该接口传入,increTotalAmount:{}", increTotalAmount);

            //查询当前sku在当前前端传入的指定wareHouse中的信息
            LambdaQueryWrapper<StockBalance> stockBalanceLambdaQueryWrapper = new LambdaQueryWrapper<>();
            stockBalanceLambdaQueryWrapper.eq(StockBalance::getSkuNumber, createMaterialDocVo.getCreateMaterialDocSkuVoList().get(i).getSkuNumber())
                    .eq(StockBalance::getCompanyCode, createMaterialDocVo.getCompanyCode())
                    .eq(StockBalance::getWarehouseCode, createMaterialDocVo.getWarehouseCode()).last("limit 1");
            //当前sku在当前前端传入的指定wareHouse中的信息 -- stockBalance
            StockBalance stockBalance = stockBalanceMapper.selectOne(stockBalanceLambdaQueryWrapper);

            //抛开wareHouse限制，查询该companyCode下所有当前sku的库存（store-balance）的总价totalAmount、移动平均价average_price、总数量信息total_qty
            //当前公司下所有产品的总价
            QueryStockBySkuVo queryStockBySkuVo = new QueryStockBySkuVo();
            queryStockBySkuVo.setCompanyCode(createMaterialDocVo.getCompanyCode());
            queryStockBySkuVo.setSkuNumber(createMaterialDocVo.getCreateMaterialDocSkuVoList().get(i).getSkuNumber());
            QueryStockBalanceResVo queryStockBalanceResVo = stockBalanceMapper.selectStockBySkuAndCompany(queryStockBySkuVo);

            //重新赋值
            BigDecimal originTotalAmount = BigDecimal.ZERO;
            BigDecimal originTotalQty = BigDecimal.ZERO;
            if (!StringUtils.isNull(queryStockBalanceResVo)) {
                originTotalAmount = queryStockBalanceResVo.getTotalAmount();
                originTotalQty = queryStockBalanceResVo.getTotalQty();
            }

            //如果是场景B，拿库存的移动平均并且不是盘盈盘亏，根据当前移动平均价计算总价
            if (increTotalAmount == null && Arrays.asList(sceneB).contains(createMaterialDocVo.getMovementType())) {
                if (StringUtils.isNull(queryStockBalanceResVo)) {
                    throw new ServiceException("当前场景下,必须先初始化库存");
                }
            }
            if (StringUtils.isNull(stockBalance)) {
                //如果库存不存在，判断是否是初始化库存的移动类型，是需要初始化库存，否则报错 --不报错
                //                if(!ModuleConstant.MOVEMENT_TYPE.Initial_Stock.equals(createMaterialDocVo.getMovementType())){
                //                    throw new ServiceException("请先初始化库存");
                //                }

                //如果当前sku在当前库存warehouse中不存在
                // 初始化
                stockBalance = new StockBalance();
                BeanUtils.copyProperties(createMaterialDocVo, stockBalance);
                BeanUtils.copyProperties(createMaterialDocVo.getCreateMaterialDocSkuVoList().get(i), stockBalance);
                stockBalance.setCreateBy(String.valueOf(userId));
                stockBalance.setGmtCreate(createTime);
                stockBalance.setModifiedBy(String.valueOf(userId));
                stockBalance.setGmtModified(createTime);
                stockBalance.setSkuNumber(createMaterialDocVo.getCreateMaterialDocSkuVoList().get(i).getSkuNumber());
                stockBalance.setTotalOnhandQty(BigDecimal.ZERO);
                stockBalance.setTotalBlockQty(BigDecimal.ZERO);
                stockBalance.setAveragePrice(BigDecimal.ZERO);
                stockBalance.setTotalQty(BigDecimal.ZERO);
                stockBalance.setTotalTransferQty(BigDecimal.ZERO);
                stockBalance.setTotalAmount(BigDecimal.ZERO);
                /**
                 * movementType : 移动类型
                 * stockBalance : 当前sku在指定的warehouse中的库存信息
                 * originTotalAmount : 当前sku在当前companyCode下查询出来的总价信息(不加wareHouse限制)
                 * originTotalQty : 当前sku在当前companyCode下查询出来的总数量信息(不加wareHouse限制)
                 * skuQty : 当前操作的sku的数量 -- 前端或相关调用该接口传入
                 * increTotalAmount :  当前操作的sku的总价格 -- 前端或相关调用该接口传入
                 * skuNumber : 当前操作的skuNumber
                 * companyCode : 当前操作的skuNumber 的公司信息
                 * warehouseCode : 当前操作的sku的仓库编码
                 * skuWareHourseIsExist : 当前操作的sku 在当前库存下是否存在  false-不存在 , true-存在
                 //                 * skuWareHourseIsExist : 当前操作的sku 在当前库存下是否存在  false-不存在 , true-存在
                 */
                String skuNumber = createMaterialDocVo.getCreateMaterialDocSkuVoList().get(i).getSkuNumber();
                String companyCode = createMaterialDocVo.getCompanyCode();
                String warehouseCode = createMaterialDocVo.getWarehouseCode();
//                Boolean skuWareHourseIsExist = false;
                stockBalance = calculateAveragePrice(movementType, stockBalance,
                        originTotalAmount, originTotalQty,
                        skuQty, increTotalAmount, skuNumber, companyCode, warehouseCode);

                stockBalanceMapper.insert(stockBalance);
            } else {
                //已存在，修改
                stockBalance.setBasicUom(createMaterialDocVo.getCreateMaterialDocSkuVoList().get(i).getBasicUom());
                stockBalance.setCurrencyCode(createMaterialDocVo.getCreateMaterialDocSkuVoList().get(i).getCurrencyCode());
                /**
                 * movementType : 移动类型
                 * stockBalance : 当前sku在指定的warehouse中的库存信息
                 * originTotalAmount : 当前sku在当前companyCode下查询出来的总价信息(不加wareHouse限制)
                 * originTotalQty : 当前sku在当前companyCode下查询出来的总数量信息(不加wareHouse限制)
                 * skuQty : 当前操作的sku的数量 -- 前端或相关调用该接口传入
                 * increTotalAmount :  当前操作的sku的总价格 -- 前端或相关调用该接口传入
                 */
                String skuNumber = createMaterialDocVo.getCreateMaterialDocSkuVoList().get(i).getSkuNumber();
                String companyCode = createMaterialDocVo.getCompanyCode();
                String warehouseCode = createMaterialDocVo.getWarehouseCode();
//                Boolean skuWareHourseIsExist = true;
                stockBalance = calculateAveragePrice(movementType, stockBalance,
                        originTotalAmount, originTotalQty,
                        skuQty, increTotalAmount, skuNumber, companyCode, warehouseCode);
                stockBalanceMapper.updateById(stockBalance);
            }

            //创建物料凭证
            MaterialDoc materialDoc = new MaterialDoc();
            //如果最后totalAmount为空，说明没有发生库存的变化
            if (increTotalAmount == null) {
                if (Arrays.asList(sceneB).contains(createMaterialDocVo.getMovementType())) {
                    materialDoc.setReferenceType(ModuleConstant.REFERENCE_TYPE.INAJ);
                    increTotalAmount = skuQty.multiply(stockBalance.getAveragePriceNumber()).setScale(2, BigDecimal.ROUND_HALF_UP);
                    createMaterialDocVo.getCreateMaterialDocSkuVoList().get(i).setTotalAmount(increTotalAmount);
                } else {
                    increTotalAmount = BigDecimal.ZERO;
                }
            }
            BeanUtils.copyProperties(createMaterialDocVo, materialDoc);
            BeanUtils.copyProperties(createMaterialDocVo.getCreateMaterialDocSkuVoList().get(i), materialDoc);
            materialDoc.setCreateBy(String.valueOf(userId));
            materialDoc.setGmtCreate(createTime);
            materialDoc.setModifiedBy(String.valueOf(userId));
            materialDoc.setGmtModified(createTime);
            materialDoc.setDocNumber(String.valueOf(docNumber));
            materialDoc.setDocItem(String.valueOf(i + 1));
            materialDoc.setIsReversed(ModuleConstant.IS_REVERSED.NORMAL);
            materialDoc.setTotalAmount(increTotalAmount);
            materialDocMapper.insert(materialDoc);

            materialDocList.add(materialDoc);
        }
        try {
            if (!createMaterialDocVo.getMovementType().equals("343") && !createMaterialDocVo.getMovementType().equals("344")) {
                log.info(">>>>>库存操作,远程调用BK开始,参数为:createMaterialDocVo:{},docNumber:{}", createMaterialDocVo, docNumber);
                String s = remoteBkGL(createMaterialDocVo, String.valueOf(docNumber));
                LambdaQueryWrapper<MaterialDoc> materialDocLambdaQueryWrapper = new LambdaQueryWrapper<MaterialDoc>()
                        .eq(MaterialDoc::getCompanyCode, createMaterialDocVo.getCompanyCode())
                        .eq(MaterialDoc::getDocNumber, String.valueOf(docNumber));
                MaterialDoc materialDoc = new MaterialDoc();
                materialDoc.setAccountingDoc(s);
                materialDocMapper.update(materialDoc, materialDocLambdaQueryWrapper);
            }
        } catch (Exception e) {
            log.error("远程调用BKGL失败，失败原因:[{}]", e.getMessage());
            throw new RuntimeException("远程调用BKGL失败,失败原因:" + e.getMessage());
        }
        return materialDocList;
    }


    /**
     * 库存数量计算方法
     *
     * @param movementType      : 移动类型
     * @param stockBalance      : 当前sku在指定的warehouse中的库存信息
     * @param originTotalAmount : 当前sku在当前companyCode下查询出来的总价信息(不加wareHouse限制)
     * @param originTotalQty    : 当前sku在当前companyCode下查询出来的总数量信息(不加wareHouse限制)
     * @param skuQty            : 当前操作的sku的数量 -- 前端或相关调用该接口传入
     * @param increTotalAmount  :  当前操作的sku的总价格 -- 前端或相关调用该接口传入
     * @param skuNumber         :  当前操作的sku
     * @param companyCode       :  当前操作的sku的公司信息
     * @param wareHourse        :  当前操作的sku的仓库信息
     * @return
     */
    private StockBalance calculateAveragePrice(MovementType movementType, StockBalance stockBalance, BigDecimal originTotalAmount,
                                               BigDecimal originTotalQty, BigDecimal skuQty, BigDecimal increTotalAmount,
                                               String skuNumber, String companyCode, String wareHourse) {
        log.info(">>>>库存计算---移动类型:movementType:{}", movementType);
        log.info(">>>>库存计算---当前sku在指定的warehouse中的库存信息:stockBalance:{}", stockBalance.toString());
        log.info(">>>>库存计算---当前sku在当前companyCode下查询出来的总价信息(不加wareHouse限制):originTotalAmount:{}", originTotalAmount);
        log.info(">>>>库存计算---当前sku在当前companyCode下查询出来的总数量信息(不加wareHouse限制):originTotalQty:{}", originTotalQty);
        log.info(">>>>库存计算---当前操作的sku的数量 -- 前端或相关调用该接口传入:skuQty:{}", skuQty);
        log.info(">>>>库存计算---当前操作的sku的总价格 -- 前端或相关调用该接口传入:increTotalAmount:{}", increTotalAmount);
        log.info(">>>>库存计算---当前操作的sku:skuNumber:{}", skuNumber);
        log.info(">>>>库存计算---当前操作的sku的公司信息:companyCode:{}", companyCode);
        log.info(">>>>库存计算---当前操作的sku的仓库信息:wareHourse:{}", wareHourse);
//        log.info(">>>>库存计算:skuWareHourseIsExist:{}", skuWareHourseIsExist);
        //记录是否为盘盈
        Boolean p = false;
        //记录是单库还是多库
        Boolean k = false;
        //查询当前sku在当前公司下的库存信息  加上此次sku的warehouse限制  用于判断是多库还是单库
        LambdaQueryWrapper<StockBalance> stockBalanceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        stockBalanceLambdaQueryWrapper.eq(StockBalance::getSkuNumber, skuNumber)
                .eq(StockBalance::getCompanyCode, companyCode)
                .ne(StockBalance::getWarehouseCode, wareHourse)
                .eq(StockBalance::getIsDeleted, 0);
        List<StockBalance> stockBalances = stockBalanceMapper.selectList(stockBalanceLambdaQueryWrapper);
        if (stockBalances.size() > 0) {
            k = true; //多库
            log.info(">>>>>>库存计算,查询到当前sku:{}是多库sku,查询到的多库信息:{}", skuNumber, stockBalances);
        }
        //保留一份最原始的库存总价格(抛开warehouse查询的)
        BigDecimal oldOriginTotalAmount = BigDecimal.ZERO;
        //是否有库存变更
        if (ModuleConstant.INVENTORY_CHANGE.CHANGED == movementType.getInventoryChange()) { //有库存变更
            //判断当前进出类型为 : IN 进
            if (ModuleConstant.IN_OUT.IN == movementType.getInOut()) {
                log.info(">>>>>>>IN");
                oldOriginTotalAmount = oldOriginTotalAmount.add(originTotalAmount);
                log.info(">>>>IN---当前sku查询到的库存总价格(未加传入的这次的总价格),oldOriginTotalAmount:{}", oldOriginTotalAmount);

                //盘盈情况下increTotalAmount不会有值
                if (null == increTotalAmount) {
                    log.info(">>>>>IN---increTotalAmount为null,为盘盈情况");
                    BigDecimal divide = originTotalAmount.divide(originTotalQty, 64, BigDecimal.ROUND_HALF_UP); //盘盈情况下要计算出移动平均价
                    increTotalAmount = skuQty.multiply(divide).setScale(64, BigDecimal.ROUND_HALF_UP);
                    log.info(">>>>IN --- 盘盈情况下,移动平均价格为:{},increTotalAmount为:{}", divide, increTotalAmount);
                    p = true;
                }

                // 库存总价格 = 库存当前总价格 + 传入的当前sku的价格
                originTotalAmount = oldOriginTotalAmount.add(increTotalAmount);
                log.info(">>>>IN --- 当前sku库存总价格(加传入的这次的总价格),originTotalAmount:{}", originTotalAmount);
                // 库存总数量 = 库存当前总数量 + 传入的当前sku的数量
                log.info(">>>>IN --- 当前sku库存总数量(未加传入的这次的总数量),originTotalQty:{}", originTotalQty);
                originTotalQty = originTotalQty.add(skuQty);
                log.info(">>>>IN --- 当前sku库存总数量(加传入的这次的总数量),originTotalQty:{}", originTotalQty);

                //重新调整移动平均价格average_price和总价格totalAmount计算
                //移动平均价(带有尾差值) = 上面两行代码的 库存总价格 ÷ 库存总数量
                BigDecimal averagePrice = originTotalAmount.divide(originTotalQty, 64, BigDecimal.ROUND_HALF_UP);
                log.info(">>>>>IN --- 移动平均价(带有尾差值) = 库存总价格 ÷ 库存总数量 : {}", averagePrice);
                stockBalance.setAveragePriceNumber(averagePrice);


                //入库需要计算移动平均价
                if (BigDecimal.ZERO.compareTo(originTotalAmount) == 0
                        || BigDecimal.ZERO.compareTo(originTotalQty) >= 0) {
                    stockBalance.setAveragePrice(BigDecimal.ZERO);
                } else {
                    stockBalance.setAveragePrice(originTotalAmount.divide(originTotalQty, 4, BigDecimal.ROUND_HALF_UP));
                }

                stockBalance.setTotalQty(stockBalance.getTotalQty().add(skuQty));
                //stockBalance.setTotalAmount(stockBalance.getAveragePrice().multiply(stockBalance.getTotalQty()));
                //重新计算总价格TotalAmount
                if (p) {
                    //盘盈
                    stockBalance.setTotalAmount(stockBalance.getTotalAmount().add(originTotalAmount.subtract(oldOriginTotalAmount)).setScale(2, BigDecimal.ROUND_HALF_UP));
                    log.info("IN --- 盘盈 --- 重新计算总价格TotalAmount , 对于当前操作的这条sku不再使用移动平均价计算,totalAmount =  originTotalAmount - oldOriginTotalAmount:{}", stockBalance.getTotalAmount());
                } else {
                    if (k) {
                        // 多库
                        BigDecimal all = BigDecimal.ZERO;
                        for (StockBalance stockBalanceList : stockBalances) {
                            stockBalanceList.setAveragePrice(stockBalance.getAveragePrice());
                            //totalAmount重新计算   =  totalQty x带有尾差的移动平均价
                            stockBalanceList.setTotalAmount(stockBalanceList.getTotalQty().multiply(averagePrice).setScale(2, BigDecimal.ROUND_HALF_UP));
                            all = all.add(stockBalanceList.getTotalQty().multiply(averagePrice)).setScale(2, BigDecimal.ROUND_HALF_UP);
                            stockBalanceMapper.updateById(stockBalanceList);
                        }
//                        //当前sku在当前wareHouse中是否存在
//                        if (skuWareHourseIsExist) {
//                            //存在
//                            stockBalance.setTotalAmount(originTotalAmount.subtract(all).subtract(averagePrice.multiply(skuQty)).setScale(2, BigDecimal.ROUND_HALF_UP));
//                            log.info("IN ---多库--存在- 重新计算总价格TotalAmount , 对于当前操作的这条sku不再使用移动平均价计算,totalAmount =  originTotalAmount - oldOriginTotalAmount:{}", stockBalance.getTotalAmount());
//                        } else {
                        //不存在
                        stockBalance.setTotalAmount(originTotalAmount.subtract(all).setScale(2, BigDecimal.ROUND_HALF_UP));
                        log.info("IN ---多库--- 重新计算总价格TotalAmount , 对于当前操作的这条sku不再使用移动平均价计算,totalAmount =  originTotalAmount - oldOriginTotalAmount:{}", stockBalance.getTotalAmount());
//                        log.info("IN ---多库--不存在- 重新计算总价格TotalAmount , 对于当前操作的这条sku不再使用移动平均价计算,totalAmount =  originTotalAmount - oldOriginTotalAmount:{}", stockBalance.getTotalAmount());
//                        }
                    } else {
                        //单库
                        stockBalance.setTotalAmount(originTotalAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
                        log.info("IN ---单库--- 重新计算总价格TotalAmount , 对于当前操作的这条sku不再使用移动平均价计算,totalAmount =  originTotalAmount:{}", stockBalance.getTotalAmount());
                    }
                }
                if (ModuleConstant.IS_BOLCK_ALLOWED.NO_ALLOWED == movementType.getIsBlockAllowed()) {
                    stockBalance.setTotalOnhandQty(stockBalance.getTotalOnhandQty().add(skuQty));
                } else if (ModuleConstant.IS_BOLCK_ALLOWED.ALLOWED == movementType.getIsBlockAllowed()) {
                    stockBalance.setTotalBlockQty(stockBalance.getTotalBlockQty().add(skuQty));
                }
            } else if (ModuleConstant.IN_OUT.OUT == movementType.getInOut()) {
                log.info(">>>>OUT");
                if (BigDecimal.ZERO.compareTo(originTotalQty) >= 0) {
                    throw new RuntimeException("库存不足");
                }
                log.info(">>>>OUT --- 当前sku查询到的库存总价格,originTotalAmount:{}", originTotalAmount);
                log.info(">>>>OUT --- 当前sku查询到的库存总数量,originTotalQty:{}", originTotalQty);
                BigDecimal subtract = stockBalance.getTotalQty().subtract(skuQty);
                log.info(">>>>OUT --- 当前sku在当前wareHouse下的数量减去本次需要出的数量,得到的剩余库存总数量,subtract:{}", subtract);
                //重新调整移动平均价格average_price和总价格totalAmount计算
                //移动平均价(带有尾差值) =  查出来的 库存总价格 ÷ 库存总数量
                BigDecimal averagePrice = originTotalAmount.divide(originTotalQty, 64, BigDecimal.ROUND_HALF_UP);
                log.info(">>>>>OUT --- 移动平均价(带有尾差值) = 库存总价格 ÷ 库存总数量 : {}", averagePrice);
                stockBalance.setAveragePriceNumber(averagePrice);

                //计算移动平均价
                if (BigDecimal.ZERO.compareTo(subtract) >= 0) {
                    log.info(">>>>>OUT -- 计算得出当前sku在当前wareHouse下的数量减去本次需要出的数量,得到的剩余库存总数量小于等于0,则将移动平均价置位0");
                    stockBalance.setAveragePrice(averagePrice.setScale(4, BigDecimal.ROUND_HALF_UP));
                } else {
                    stockBalance.setAveragePrice(originTotalAmount.divide(originTotalQty, 4, BigDecimal.ROUND_HALF_UP));
                }

                stockBalance.setTotalQty(subtract);

                if (BigDecimal.ZERO.compareTo(subtract) >= 0) {
                    log.info(">>>>OUT --- 本次出货后该sku数量变为0,所以将totalAmount也置位0");
                    stockBalance.setTotalAmount(BigDecimal.ZERO);
                } else {
                    //重新计算 totalAmount = 当前sku在当前仓库中的总价格 - (带有尾差的移动平均价 * 本次要出的数量)
                    stockBalance.setTotalAmount(stockBalance.getTotalAmount().subtract(averagePrice.multiply(skuQty)).setScale(2, BigDecimal.ROUND_HALF_UP));
                    log.info(">>>>OUT --- 重新计算 totalAmount = 当前sku在当前仓库中的总价格 - (带有尾差的移动平均价 * 本次要出的数量) :{}", stockBalance.getTotalAmount());
                }
                if (ModuleConstant.IS_BOLCK_ALLOWED.NO_ALLOWED == movementType.getIsBlockAllowed()) {
                    stockBalance.setTotalOnhandQty(stockBalance.getTotalOnhandQty().subtract(skuQty));
                } else if (ModuleConstant.IS_BOLCK_ALLOWED.ALLOWED == movementType.getIsBlockAllowed()) {
                    stockBalance.setTotalBlockQty(stockBalance.getTotalBlockQty().subtract(skuQty));
                }
            }
        } else if (ModuleConstant.INVENTORY_CHANGE.NOT_CHANGED == movementType.getInventoryChange()) {//没有库存变更，说明是冻结/解冻类型
            if (ModuleConstant.IS_BOLCK_ALLOWED.NO_ALLOWED == movementType.getIsBlockAllowed()) {  //解冻类型
                stockBalance.setTotalOnhandQty(stockBalance.getTotalOnhandQty().add(skuQty));
                stockBalance.setTotalBlockQty(stockBalance.getTotalBlockQty().subtract(skuQty));
            } else if (ModuleConstant.IS_BOLCK_ALLOWED.ALLOWED == movementType.getIsBlockAllowed()) {  //冻结类型
                stockBalance.setTotalBlockQty(stockBalance.getTotalBlockQty().add(skuQty));
                stockBalance.setTotalOnhandQty(stockBalance.getTotalOnhandQty().subtract(skuQty));
            }
        } else {
            log.error("库存变更类型错误，当前类型值:[{}]", movementType.getInventoryChange());
            throw new ServiceException("库存变更类型错误");
        }

        if (BigDecimal.ZERO.compareTo(stockBalance.getTotalQty()) > 0
                || BigDecimal.ZERO.compareTo(stockBalance.getTotalBlockQty()) > 0
                || BigDecimal.ZERO.compareTo(stockBalance.getTotalOnhandQty()) > 0) {
            log.error("计算后的库存量不足,SkuNumber:[{}],计算后的库存总数:[{}]-减/加库存总数[{}],计算后的可用库存总数:[{}]-减/加可用库存总数[{}],计算后的冻结库存总数:[{}]-减/加库存库存总数[{}]", stockBalance.getSkuNumber(),
                    stockBalance.getTotalQty(), skuQty,
                    stockBalance.getTotalOnhandQty(), skuQty,
                    stockBalance.getTotalBlockQty(), skuQty);

            throw new ServiceException("库存不足");
        }
        return stockBalance;
    }

    @Transactional
    public QueryUnReversedResVo getUnReversedByDocNumber(String docNumber, String companyCode) {
        LambdaQueryWrapper<MaterialDoc> materialDocLambdaQueryWrapper = new LambdaQueryWrapper<>();
        materialDocLambdaQueryWrapper.eq(MaterialDoc::getDocNumber, docNumber)
                .eq(MaterialDoc::getCompanyCode, companyCode)
                .eq(MaterialDoc::getIsDeleted, ModuleConstant.IS_DELETED.NO_DELETE)
                .eq(MaterialDoc::getIsReversed, ModuleConstant.IS_REVERSED.NORMAL);
        List<MaterialDoc> materialDocList = materialDocMapper.selectList(materialDocLambdaQueryWrapper);

        LambdaQueryWrapper<MaterialDoc> materialDocAllLambdaQueryWrapper = new LambdaQueryWrapper<>();
        materialDocAllLambdaQueryWrapper.eq(MaterialDoc::getDocNumber, docNumber);
        materialDocAllLambdaQueryWrapper.eq(MaterialDoc::getCompanyCode, companyCode);
        List<MaterialDoc> materialDocAll = materialDocMapper.selectList(materialDocAllLambdaQueryWrapper);

        QueryUnReversedResVo queryUnReversedResVo = new QueryUnReversedResVo();
        if (CollectionUtils.isEmpty(materialDocAll)) {
            return queryUnReversedResVo;
        }
        Warehouse warehouse = warehouseService.getWarehouse(materialDocAll.get(0).getCompanyCode(), materialDocAll.get(0).getWarehouseCode());
        log.info("远程调用查询warehouse结果:[{}]", warehouse);
        if (!StringUtils.isNull(warehouse)) {
            queryUnReversedResVo.setWarehouseName(warehouse.getName());
        }
        queryUnReversedResVo.setDocNumber(docNumber);
        queryUnReversedResVo.setMovementType(materialDocAll.get(0).getMovementType());
        queryUnReversedResVo.setWarehouse(materialDocAll.get(0).getWarehouseCode());
        queryUnReversedResVo.setStockStatus(materialDocAll.get(0).getStockStatus());
        queryUnReversedResVo.setPostDate(materialDocAll.get(0).getPostingDate());
        if (!CollectionUtils.isEmpty(materialDocList)) {
            List<QueryUnReversedSubResVo> queryUnReversedSubResVoList = new ArrayList<>();
            for (MaterialDoc materialDoc : materialDocList) {
                QueryUnReversedSubResVo queryUnReversedSubResVo = new QueryUnReversedSubResVo();
                BeanUtils.copyProperties(materialDoc, queryUnReversedSubResVo);
                //查询skuName
                SkuMaster skuMaster = skuService.getSku(queryUnReversedSubResVo.getSkuNumber(), null, companyCode);
                log.info("查询skuMaster结果:[{}]", skuMaster);
                if (!StringUtils.isNull(skuMaster)) {
                    queryUnReversedSubResVo.setSkuName(skuMaster.getSkuName());
                }
                //查询对应库存
                LambdaQueryWrapper<StockBalance> stockBalanceLambdaQueryWrapper = new LambdaQueryWrapper<>();
                stockBalanceLambdaQueryWrapper.eq(StockBalance::getSkuNumber, queryUnReversedSubResVo.getSkuNumber())
                        .eq(StockBalance::getCompanyCode, materialDoc.getCompanyCode())
                        .eq(StockBalance::getWarehouseCode, queryUnReversedSubResVo.getWarehouseCode()).last("limit 1");
                StockBalance stockBalance = stockBalanceMapper.selectOne(stockBalanceLambdaQueryWrapper);
                BeanUtils.copyProperties(stockBalance, queryUnReversedSubResVo);
                queryUnReversedSubResVo.setId(materialDoc.getId());
                queryUnReversedSubResVoList.add(queryUnReversedSubResVo);
            }
            queryUnReversedResVo.setQueryUnReversedSubResVoList(queryUnReversedSubResVoList);
        }

        return queryUnReversedResVo;
    }

    /**
     * reversed操作
     *
     * @param reversedMaterialDocVO
     * @return
     */
    @Transactional
    public List<MaterialDoc> reverseMaterialDoc(ReversedMaterialDocVO reversedMaterialDocVO) throws ServiceException {
        List<MaterialDoc> materialDocList = new ArrayList<>();
        //获取用户信息
        Long userId = 1l;//UserInfoUtils.getSysUserId();
        Date createTime = new Date();
        Long docNumber = numberWorker.generateId(reversedMaterialDocVO.getCompanyCode(), ModuleConstant.ORDER_NUMBER_TYPE.MATERIAL_DOC);
        int i = 0;
        for (ReversedMaterialDocItemVo reversedMaterialDocItemVo : reversedMaterialDocVO.getReversedMaterialDocItemVos()) {
            i++;
            //遍历物料凭证ID
            //查询物料凭证信息
            MaterialDoc materialDoc = materialDocMapper.selectById(reversedMaterialDocItemVo.getMaterialDocId());
            materialDoc.setIsReversed(ModuleConstant.IS_REVERSED.REVERSED);
            materialDoc.setModifiedBy(String.valueOf(userId));
            materialDoc.setGmtModified(createTime);
            if (ModuleConstant.DB_RES_STATUS.FAIL_STATUS == materialDocMapper.updateById(materialDoc)) {
                log.error("reverse 操作更新物料凭证 IS_REVERSED 错误,物料凭证ID:[{}]", materialDoc.getId());
                throw new ServiceException("物料凭证表修改失败");
            }
            //查询移动类型的反向操作类型
            LambdaQueryWrapper<MovementType> movementTypeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            movementTypeLambdaQueryWrapper.eq(MovementType::getReverseMovType, materialDoc.getMovementType())
                    .eq(MovementType::getIsDeleted, ModuleConstant.IS_DELETED.NO_DELETE).last("limit 1");
            MovementType reverseMovementType = movementTypeMapper.selectOne(movementTypeLambdaQueryWrapper);

            if (ModuleConstant.STOCK_STATUS.STOCK.equals(materialDoc.getStockStatus())) {
                reverseMovementType.setIsBlockAllowed(ModuleConstant.IS_BOLCK_ALLOWED.ALLOWED);
            } else {
                reverseMovementType.setIsBlockAllowed(ModuleConstant.IS_BOLCK_ALLOWED.NO_ALLOWED);
            }

            //查询skuNumber对应的库存
            LambdaQueryWrapper<StockBalance> stockBalanceLambdaQueryWrapper = new LambdaQueryWrapper<>();
            stockBalanceLambdaQueryWrapper.eq(StockBalance::getSkuNumber, materialDoc.getSkuNumber())
                    .eq(StockBalance::getCompanyCode, materialDoc.getCompanyCode())
                    .eq(StockBalance::getWarehouseCode, materialDoc.getWarehouseCode())
                    .last("limit 1");
            StockBalance stockBalance = stockBalanceMapper.selectOne(stockBalanceLambdaQueryWrapper);


            //当前公司下该sku产品的总价 (不加wareHouse限制)
            QueryStockBySkuVo queryStockBySkuVo = new QueryStockBySkuVo();
            queryStockBySkuVo.setCompanyCode(materialDoc.getCompanyCode());
            queryStockBySkuVo.setSkuNumber(materialDoc.getSkuNumber());
            QueryStockBalanceResVo queryStockBalanceResVo = stockBalanceMapper.selectStockBySkuAndCompany(queryStockBySkuVo);
            //计算总价=原总价+增量总价
            BigDecimal originTotalAmount = BigDecimal.ZERO;
            BigDecimal originTotalQty = BigDecimal.ZERO;
            if (!StringUtils.isNull(queryStockBalanceResVo)) {
                originTotalAmount = queryStockBalanceResVo.getTotalAmount();
                originTotalQty = queryStockBalanceResVo.getTotalQty();
            }

            //如果是场景B，拿库存的移动平均并且不是盘盈盘亏，根据当前移动平均价计算总价
            if (materialDoc.getTotalAmount() == null && Arrays.asList(sceneB).contains(reverseMovementType.getMovementType())) {
                if (StringUtils.isNull(queryStockBalanceResVo)) {
                    throw new ServiceException("当前场景下,必须先初始化库存");
                }
            }
            //修改库存信息
            stockBalance = reverseCalculateAveragePrice(reverseMovementType, stockBalance,
                    originTotalAmount, originTotalQty,
                    materialDoc.getSkuQty(), materialDoc.getTotalAmount(),
                    materialDoc.getSkuNumber(), materialDoc.getCompanyCode(), materialDoc.getWarehouseCode());
            if (ModuleConstant.DB_RES_STATUS.FAIL_STATUS == stockBalanceMapper.updateById(stockBalance)) {
                log.error("reverse 操作修改 stock_balance表失败,物料凭证ID:[{}]", materialDoc.getId());
                throw new ServiceException("修改库存表失败");
            }
            stockBalanceMapper.updateById(stockBalance);

            //创建
            //生成物料凭证
            //创建物料凭证
            MaterialDoc materialDocNew = new MaterialDoc();
            //拷贝该物料凭证之前的数据
            BeanUtils.copyProperties(materialDoc, materialDocNew);
            materialDocNew.setCreateBy(String.valueOf(userId));
            materialDocNew.setGmtCreate(createTime);
            materialDocNew.setModifiedBy(String.valueOf(userId));
            materialDocNew.setGmtModified(createTime);
            materialDocNew.setDocNumber(String.valueOf(docNumber));
            materialDocNew.setDocItem(String.valueOf(i));
            materialDocNew.setIsReversed(ModuleConstant.IS_REVERSED.NORMAL);
            materialDocNew.setMovementType(reverseMovementType.getMovementType());
            materialDocNew.setInOut(reverseMovementType.getInOut());
            materialDocNew.setPostingDate(reversedMaterialDocVO.getReverseDate());
            materialDocNew.setNote(reversedMaterialDocVO.getNote());
            materialDocNew.setReferenceType(ModuleConstant.REFERENCE_TYPE.INAJ);  //反向操作类型
            materialDocNew.setReferenceNumber(materialDoc.getDocNumber());
            materialDocNew.setReferenceItem(materialDoc.getDocItem());
            materialDocNew.setId(null);


            materialDocList.add(materialDocNew);
            if (ModuleConstant.DB_RES_STATUS.FAIL_STATUS == materialDocMapper.insert(materialDocNew)) {
                log.error("reverse 操作新增 material_doc 表失败,物料凭证Number:[{}]", materialDocNew.getDocNumber());
                throw new ServiceException("修改库存表失败");
            }
        }

        try {
            if (!materialDocList.get(0).getMovementType().equals("343") && !materialDocList.get(0).getMovementType().equals("344")) {
                log.info(">>>>reverse 调用remoteBkGLs:{}", materialDocList);
                String s = remoteBkGLs(String.valueOf(docNumber), materialDocList);
                LambdaQueryWrapper<MaterialDoc> materialDocLambdaQueryWrapper = new LambdaQueryWrapper<MaterialDoc>()
                        .eq(MaterialDoc::getCompanyCode, reversedMaterialDocVO.getCompanyCode())
                        .eq(MaterialDoc::getDocNumber, String.valueOf(docNumber));
                MaterialDoc materialDoc = new MaterialDoc();
                materialDoc.setAccountingDoc(s);
                materialDocMapper.update(materialDoc, materialDocLambdaQueryWrapper);
            }
        } catch (Exception e) {
            log.error("远程调用BKGL失败，失败原因:[{}]", e.getMessage());
            throw new ServiceException("远程调用BKGL失败,失败原因:" + e.getMessage());
        }
        return materialDocList;
    }

    /**
     * reverse库存数量计算方法
     *
     * @param movementType      : 移动类型
     * @param stockBalance      : 当前sku在指定的warehouse中的库存信息
     * @param originTotalAmount : 当前sku在当前companyCode下查询出来的总价信息(不加wareHouse限制)
     * @param originTotalQty    : 当前sku在当前companyCode下查询出来的总数量信息(不加wareHouse限制)
     * @param skuQty            : 当前操作的sku的数量 -- 前端或相关调用该接口传入
     * @param increAmount       : 当前操作的sku的总价格 -- 前端或相关调用该接口传入
     * @param skuNumber         : 当前操作的sku
     * @param companyCode       : 当前操作的sku的公司信息
     * @param wareHouse         : 当前操作的sku的仓库信息
     * @return
     */
    private StockBalance reverseCalculateAveragePrice(MovementType movementType, StockBalance stockBalance, BigDecimal originTotalAmount,
                                                      BigDecimal originTotalQty, BigDecimal skuQty, BigDecimal increAmount,
                                                      String skuNumber, String companyCode, String wareHouse) {
        log.info(">>>>Reverse 库存计算---移动类型,movementType:{}", movementType);
        log.info(">>>>Reverse 库存计算---当前sku在指定的warehouse中的库存信息,stockBalance:{}", stockBalance.toString());
        log.info(">>>>Reverse 库存计算---当前sku在当前companyCode下查询出来的总价信息(不加wareHouse限制),originTotalAmount:{}", originTotalAmount);
        log.info(">>>>Reverse 库存计算---当前sku在当前companyCode下查询出来的总数量信息(不加wareHouse限制),originTotalQty:{}", originTotalQty);
        log.info(">>>>Reverse 库存计算---当前操作的sku的数量 -- 前端或相关调用该接口传入,skuQty:{}", skuQty);
        log.info(">>>>Reverse 库存计算---当前操作的sku的总价格 -- 前端或相关调用该接口传入,increAmount:{}", increAmount);
        log.info(">>>>Reverse 库存计算---当前操作的sku,skuNumber:{}", skuNumber);
        log.info(">>>>Reverse 库存计算---当前操作的sku的公司信息,companyCode:{}", companyCode);
        log.info(">>>>Reverse 库存计算---当前操作的sku的仓库信息,wareHouse:{}", wareHouse);
        //记录是单库还是多库
        Boolean k = false;
        //查询当前sku在当前公司下的库存信息  用于判断是多库还是单库 及 更新这些库存的移动平均价
        LambdaQueryWrapper<StockBalance> stockBalanceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        stockBalanceLambdaQueryWrapper.eq(StockBalance::getSkuNumber, skuNumber)
                .eq(StockBalance::getCompanyCode, companyCode)
                .ne(StockBalance::getWarehouseCode, wareHouse)
                .eq(StockBalance::getIsDeleted, 0);
        List<StockBalance> stockBalances = stockBalanceMapper.selectList(stockBalanceLambdaQueryWrapper);
        if (stockBalances.size() > 0) {
            k = true; //多库
        }
        //是否有库存变更
        if (ModuleConstant.INVENTORY_CHANGE.CHANGED == movementType.getInventoryChange()) { //有库存变更
            if (ModuleConstant.IN_OUT.IN == movementType.getInOut()) {
                log.info(">>>>Reverse --- IN");
                originTotalAmount = originTotalAmount.add(increAmount);
                originTotalQty = originTotalQty.add(skuQty);
                log.info(">>>>Reverse --IN-- 用当前sku的总价格(当前公司所有总和)加上物料凭证上的原始价格计算结果,originTotalAmount:{}", originTotalAmount);
                BigDecimal qtySubtract = originTotalQty;
                log.info(">>>>Reverse --IN-- 用当前sku的总数量(当前公司所有总和)加上物料凭证上的原始数量计算结果,qtySubtract:{}", qtySubtract);
                //移动平均价
                BigDecimal divide = originTotalAmount.divide(qtySubtract, 64, BigDecimal.ROUND_HALF_UP);
                log.info(">>>>>Reverse --IN-- 移动平均价(带有尾差的)计算结果, divide:{}", divide);

                //计算移动平均价
                if (BigDecimal.ZERO.compareTo(originTotalAmount) == 0
                        || BigDecimal.ZERO.compareTo(originTotalQty) >= 0) {
                    stockBalance.setAveragePrice(BigDecimal.ZERO);
                } else {
                    stockBalance.setAveragePrice(originTotalAmount.divide(qtySubtract, 4, BigDecimal.ROUND_HALF_UP));
                }

                stockBalance.setTotalQty(stockBalance.getTotalQty().add(skuQty));

                if (k) {
                    BigDecimal all = BigDecimal.ZERO;
                    for (StockBalance stockBalanceList : stockBalances) {
                        stockBalanceList.setAveragePrice(divide.setScale(4, BigDecimal.ROUND_HALF_UP));
                        //totalAmount重新计算   =  totalQty x带有尾差的移动平均价
                        stockBalanceList.setTotalAmount(stockBalanceList.getTotalQty().multiply(divide).setScale(2, BigDecimal.ROUND_HALF_UP));
                        all = all.add(stockBalanceList.getTotalQty().multiply(divide)).setScale(2, BigDecimal.ROUND_HALF_UP);
                        stockBalanceMapper.updateById(stockBalanceList);
                    }
                    stockBalance.setTotalAmount(originTotalAmount.subtract(all).setScale(2, BigDecimal.ROUND_HALF_UP));
                    log.info(">>>>>Reverse --IN--多库-- ,该条sku的totalAmount等于:{}", stockBalance.getTotalAmount());
                } else {
                    stockBalance.setTotalAmount(originTotalAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
                    log.info(">>>>>Reverse --IN--单库-- ,该条sku的totalAmount等于:{}", stockBalance.getTotalAmount());
                }

                if (ModuleConstant.IS_BOLCK_ALLOWED.NO_ALLOWED == movementType.getIsBlockAllowed()) {
                    stockBalance.setTotalOnhandQty(stockBalance.getTotalOnhandQty().add(skuQty));
                } else if (ModuleConstant.IS_BOLCK_ALLOWED.ALLOWED == movementType.getIsBlockAllowed()) {
                    stockBalance.setTotalBlockQty(stockBalance.getTotalBlockQty().add(skuQty));
                }
            } else if (ModuleConstant.IN_OUT.OUT == movementType.getInOut()) {
                log.info(">>>>Reverse --- OUT");
                BigDecimal amountSubtract = originTotalAmount.subtract(increAmount);
                log.info(">>>>Reverse --OUT-- 用当前sku的总价格(当前公司所有总和)减去物料凭证上的原始价格计算结果,amountSubtract:{}", amountSubtract);
                BigDecimal qtySubtract = originTotalQty.subtract(skuQty);
                log.info(">>>>Reverse --OUT-- 用当前sku的总数量(当前公司所有总和)减去物料凭证上的原始数量计算结果,qtySubtract:{}", qtySubtract);

                BigDecimal subtract = stockBalance.getTotalQty().subtract(skuQty);
                log.info(">>>>Reverse --OUT-- 用当前sku在当前wareHouse下reverse,reverse后的数量为:{}", subtract);

                //如果reverse后该sku在公司下所有的数量为0或小于0  不再计算移动平均价
                BigDecimal divide = BigDecimal.ZERO;
                if (BigDecimal.ZERO.compareTo(qtySubtract) < 0) {
                    //移动平均价
                    log.info(">>>>Reverse --OUT-- 带有尾差的移动平均价计算");
                    divide = divide.add(amountSubtract.divide(qtySubtract, 64, BigDecimal.ROUND_HALF_UP));
                    log.info(">>>>>Reverse --OUT-- 移动平均价(带有尾差的)计算结果, divide:{}", divide);
                }

                //计算移动平均价
                if (BigDecimal.ZERO.compareTo(subtract) >= 0) {
                    if (BigDecimal.ZERO.compareTo(divide) == 0) {
                        stockBalance.setAveragePrice(stockBalance.getAveragePrice());
                    } else {
                        stockBalance.setAveragePrice(amountSubtract.divide(qtySubtract, 4, BigDecimal.ROUND_HALF_UP));
                    }
                } else {
                    stockBalance.setAveragePrice(amountSubtract.divide(qtySubtract, 4, BigDecimal.ROUND_HALF_UP));
                }

                stockBalance.setTotalQty(subtract);

                if (k) {
                    BigDecimal all = BigDecimal.ZERO;
                    for (StockBalance stockBalanceList : stockBalances) {
                        if (BigDecimal.ZERO.compareTo(divide) == 0) {
                            stockBalanceList.setAveragePrice(stockBalance.getAveragePrice());
                        } else {
                            stockBalanceList.setAveragePrice(divide.setScale(4, BigDecimal.ROUND_HALF_UP));
                        }
                        //totalAmount重新计算   =  totalQty x带有尾差的移动平均价
                        stockBalanceList.setTotalAmount(stockBalanceList.getTotalQty().multiply(divide).setScale(2, BigDecimal.ROUND_HALF_UP));
                        all = all.add(stockBalanceList.getTotalQty().multiply(divide)).setScale(2, BigDecimal.ROUND_HALF_UP);
                        stockBalanceMapper.updateById(stockBalanceList);
                    }
                    stockBalance.setTotalAmount(amountSubtract.subtract(all).setScale(2, BigDecimal.ROUND_HALF_UP));
                    log.info(">>>>>Reverse --OUT--多库-- 当sku  reverse后数量大于0 , 那么该条sku的totalAmount等于:{}", stockBalance.getTotalAmount());
                } else {
                    stockBalance.setTotalAmount(originTotalAmount.subtract(increAmount).setScale(2, BigDecimal.ROUND_HALF_UP));
                    log.info(">>>>>Reverse --OUT--单库-- 当sku  reverse后数量大于0 , 那么该条sku的totalAmount等于:{}", stockBalance.getTotalAmount());
                }

                if (BigDecimal.ZERO.compareTo(subtract) >= 0) {
                    log.info(">>>>>Reverse --OUT-- 当sku  reverse后数量剩余为0 , 那么该条sku的totalAmount也设置为0");
                    stockBalance.setTotalAmount(BigDecimal.ZERO);
                }

                if (ModuleConstant.IS_BOLCK_ALLOWED.NO_ALLOWED == movementType.getIsBlockAllowed()) {
                    stockBalance.setTotalOnhandQty(stockBalance.getTotalOnhandQty().subtract(skuQty));
                } else if (ModuleConstant.IS_BOLCK_ALLOWED.ALLOWED == movementType.getIsBlockAllowed()) {
                    stockBalance.setTotalBlockQty(stockBalance.getTotalBlockQty().subtract(skuQty));
                }
            }

        } else if (ModuleConstant.INVENTORY_CHANGE.NOT_CHANGED == movementType.getInventoryChange()) {//没有库存变更，说明是冻结/解冻类型
            if (ModuleConstant.IS_BOLCK_ALLOWED.NO_ALLOWED == movementType.getIsBlockAllowed()) {  //解冻类型
                stockBalance.setTotalOnhandQty(stockBalance.getTotalOnhandQty().add(skuQty));
                stockBalance.setTotalBlockQty(stockBalance.getTotalBlockQty().subtract(skuQty));
            } else if (ModuleConstant.IS_BOLCK_ALLOWED.ALLOWED == movementType.getIsBlockAllowed()) {  //冻结类型
                stockBalance.setTotalBlockQty(stockBalance.getTotalBlockQty().add(skuQty));
                stockBalance.setTotalOnhandQty(stockBalance.getTotalOnhandQty().subtract(skuQty));
            }
        } else {
            log.error("库存变更类型错误，当前类型值:[{}]", movementType.getInventoryChange());
            throw new ServiceException("库存变更类型错误");
        }

        if (BigDecimal.ZERO.compareTo(stockBalance.getTotalQty()) > 0
                || BigDecimal.ZERO.compareTo(stockBalance.getTotalBlockQty()) > 0
                || BigDecimal.ZERO.compareTo(stockBalance.getTotalOnhandQty()) > 0) {
            log.error("计算后的库存量不足,SkuNumber:[{}],计算后的库存总数:[{}]-减/加库存总数[{}],计算后的可用库存总数:[{}]-减/加可用库存总数[{}],计算后的冻结库存总数:[{}]-减/加库存库存总数[{}]", stockBalance.getSkuNumber(),
                    stockBalance.getTotalQty(), skuQty,
                    stockBalance.getTotalOnhandQty(), skuQty,
                    stockBalance.getTotalBlockQty(), skuQty);

            throw new ServiceException("库存不足");
        }
        return stockBalance;
    }


    /**
     * 库存变动给Bk发送gl
     *
     * @param createMaterialDocVo
     * @return
     * @throws IOException
     */
    public String remoteBkGL(CreateMaterialDocVo createMaterialDocVo, String docNumber) throws IOException {
//        RemoteBkGl remoteBkGl = new RemoteBkGl();
                RemoteBkGlV2 remoteBkGl = new RemoteBkGlV2();
//        LambdaQueryWrapper<BkCoaRel> bkCoaRelLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        bkCoaRelLambdaQueryWrapper.eq(BkCoaRel::getCode, createMaterialDocVo.getMovementType())
//                .eq(BkCoaRel::getCompanyCode, createMaterialDocVo.getCompanyCode())
//                .last("limit 1");
//        BkCoaRel bkCoaRel = bkCoaRelMapper.selectOne(bkCoaRelLambdaQueryWrapper);
                Company company = companyService.getCompany(createMaterialDocVo.getCompanyCode());
                ArrayList<BkCoaRel> bkCoaRels = bookKeepingService.getBkCoaRels(company);
                Optional<BkCoaRel> bkCoaRelOptional = bkCoaRels.stream().filter(b -> b.getCode().equals(createMaterialDocVo.getMovementType()) && b.getCompanyCode().equals(createMaterialDocVo.getCompanyCode())).findFirst();

                if (bkCoaRelOptional.isPresent()) {
                    BkCoaRel bkCoaRel = bkCoaRelOptional.get();
                    remoteBkGl.setCompanyId(company.getOrgidEx());
                    remoteBkGl.setCompanyCode(company.getCompanyCodeEx());
                    remoteBkGl.setHeaderText(docNumber);
//            remoteBkGl.setCurrency(convertCurrency(createMaterialDocVo.getCreateMaterialDocSkuVoList().get(0).getCurrencyCode()));
                    remoteBkGl.setCurrency(createMaterialDocVo.getCreateMaterialDocSkuVoList().get(0).getCurrencyCode());
                    remoteBkGl.setCreateDate(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, new Date()));
                    remoteBkGl.setPostingDate(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, createMaterialDocVo.getPostingDate()));
                    remoteBkGl.setExchangeRate(1);
                    BigDecimal totalDebit = BigDecimal.ZERO;
                    BigDecimal totalCredit = BigDecimal.ZERO;
                    //初始化Item
//            List<RemoteBkGlSubList> remoteBkGlSubLists = new ArrayList<>();
                    List<RemoteBkGlSubListV2> remoteBkGlSubLists = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(createMaterialDocVo.getCreateMaterialDocSkuVoList())) {
                        int i = 0;
                        for (CreateMaterialDocSkuVo createMaterialDocSkuVo : createMaterialDocVo.getCreateMaterialDocSkuVoList()) {
                            String skuName = createMaterialDocSkuVo.getSkuNumber();
                            SkuMaster res = skuService.getSku(createMaterialDocSkuVo.getSkuNumber(), null, createMaterialDocVo.getCompanyCode());

                            if (res != null) {
                                log.info("查询skuMaster成功结果:[{}]", res);
                                skuName = res.getSkuName();
                            } else {
                                log.error("查询skuMaster失败，失败原因:[{}]");
                            }

                            //增量总价
                            BigDecimal increTotalAmount = createMaterialDocSkuVo.getTotalAmount();

                            if (increTotalAmount == null && createMaterialDocSkuVo.getItemAmount() != null) {
                                increTotalAmount = createMaterialDocSkuVo.getItemAmount().multiply(createMaterialDocSkuVo.getSkuQty()).setScale(2, BigDecimal.ROUND_HALF_UP);
                            }
                            i++;
                            RemoteBkGlSubListV2 debitBkGlSubList = new RemoteBkGlSubListV2();
                            debitBkGlSubList.setItemNo(String.valueOf(i));
                            debitBkGlSubList.setDescription(skuName);
                            debitBkGlSubList.setGlAccount(getCode(bkCoaRel, res, "dr"));
//                            debitBkGlSubList.setGlAccount(bkCoaRel.getDebitCoaCode());
                            debitBkGlSubList.setNegPosting(false);
                            debitBkGlSubList.setAmountTc(increTotalAmount);
                            debitBkGlSubList.setAmountLc(BigDecimal.ZERO);
                            debitBkGlSubList.setDrCr("dr");

                            totalDebit = totalDebit.add(increTotalAmount);

                            remoteBkGlSubLists.add(debitBkGlSubList);

                            i++;
                            RemoteBkGlSubListV2 creditBkGlSubList = new RemoteBkGlSubListV2();
                            creditBkGlSubList.setItemNo(String.valueOf(i));
                            creditBkGlSubList.setDescription(skuName);
                            creditBkGlSubList.setGlAccount(getCode(bkCoaRel, res, "cr"));
//                            creditBkGlSubList.setGlAccount(bkCoaRel.getCoaCode());
                            creditBkGlSubList.setNegPosting(false);
                            creditBkGlSubList.setAmountTc(increTotalAmount);
                            creditBkGlSubList.setAmountLc(BigDecimal.ZERO);
                            creditBkGlSubList.setDrCr("cr");

                            totalCredit = totalCredit.add(increTotalAmount);

                            remoteBkGlSubLists.add(creditBkGlSubList);

                        }

                    }
                    remoteBkGl.setTotalDebit(totalDebit);
                    remoteBkGl.setTotalCredit(totalCredit);
                    remoteBkGl.setLineItems(remoteBkGlSubLists);
                } else {
                    throw new RuntimeException("oms查询BookKeeping信息出错");
                }
                try {
                    String besnString = JSONObject.toJSONString(remoteBkGl);
                    JSONObject requestBody = JSONObject.parseObject(besnString);
                    log.info(">>>>>bookKeepingService.postBkGL,requestBody:{}", requestBody);
            return bookKeepingService.postBkGL(requestBody);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }
    public String getCode(BkCoaRel bkCoaRel, SkuMaster skuMaster, String drCr) {
        if (bkCoaRel.getCoaJson() == null || bkCoaRel.getCoaJson().size() == 0) {
            return drCr == "dr" ? bkCoaRel.getDebitCoaCode() : bkCoaRel.getCoaCode();
        } else {
            String skuGroupCode = skuMaster.getSkuGroupCode();
            for (BkCoaRel.CoaItem coaItem : bkCoaRel.getCoaJson()) {
                if (coaItem.getSkuGroup().equals(skuGroupCode)) {
                    return drCr == "dr" ? coaItem.getDebitCoaCode() : coaItem.getCoaCode();
                }
            }
            return drCr == "dr" ? bkCoaRel.getDebitCoaCode() : bkCoaRel.getCoaCode();
        }
    }
    /**
     * 库存变动给Bk发送gl
     *
     * @param materialDocList
     * @return
     * @throws IOException
     */
    public String remoteBkGLs(String docNumber, List<MaterialDoc> materialDocList) throws IOException {
//        RemoteBkGl remoteBkGl = new RemoteBkGl();
        RemoteBkGlV2 remoteBkGl = new RemoteBkGlV2();
//        LambdaQueryWrapper<BkCoaRel> bkCoaRelLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        bkCoaRelLambdaQueryWrapper.eq(BkCoaRel::getCode, materialDocList.get(0).getMovementType())
//                .eq(BkCoaRel::getCompanyCode, materialDocList.get(0).getCompanyCode())
//                .last("limit 1");
//        BkCoaRel bkCoaRel = bkCoaRelMapper.selectOne(bkCoaRelLambdaQueryWrapper);
        Company company = companyService.getCompany(materialDocList.get(0).getCompanyCode());
        ArrayList<BkCoaRel> bkCoaRels = bookKeepingService.getBkCoaRels(company);
        BkCoaRel bkCoaRel = bkCoaRels.stream().filter(b -> b.getCode().equals(materialDocList.get(0).getMovementType()) && b.getCompanyCode().equals(materialDocList.get(0).getCompanyCode())).findFirst().orElse(null);
        if (!StringUtils.isNull(bkCoaRel)) {
            remoteBkGl.setCompanyId(company.getOrgidEx());
            remoteBkGl.setCompanyCode(company.getCompanyCodeEx());
            remoteBkGl.setHeaderText(docNumber);
//            remoteBkGl.setCurrency(convertCurrency(materialDocList.get(0).getCurrencyCode()));
            remoteBkGl.setCurrency(materialDocList.get(0).getCurrencyCode());
            remoteBkGl.setCreateDate(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, new Date()));
            remoteBkGl.setPostingDate(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, materialDocList.get(0).getPostingDate()));
            remoteBkGl.setExchangeRate(1);
            BigDecimal totalDebit = BigDecimal.ZERO;
            BigDecimal totalCredit = BigDecimal.ZERO;
            //初始化Item
//            List<RemoteBkGlSubList> remoteBkGlSubLists = new ArrayList<>();
            List<RemoteBkGlSubListV2> remoteBkGlSubLists = new ArrayList<>();
            if (!CollectionUtils.isEmpty(materialDocList)) {
                int i = 0;
                for (MaterialDoc materialDoc : materialDocList) {
                    String skuName = materialDoc.getSkuNumber();
                    SkuMaster res = skuService.getSku(materialDoc.getSkuNumber(), null, materialDoc.getCompanyCode());

                    if (res != null) {
                        log.info("查询skuMaster成功结果:[{}]", res);
                        skuName = res.getSkuName();
                    } else {
                        log.error("远程调用查询skuMaster失败，失败原因:[{}]");
                    }

                    //增量总价
                    BigDecimal increTotalAmount = materialDoc.getTotalAmount();

                    i++;
                    RemoteBkGlSubListV2 debitBkGlSubList = new RemoteBkGlSubListV2();
                    debitBkGlSubList.setItemNo(String.valueOf(i));
                    debitBkGlSubList.setDescription(skuName);
                    debitBkGlSubList.setGlAccount(getCode(bkCoaRel, res, "dr"));
                    debitBkGlSubList.setNegPosting(false);
                    debitBkGlSubList.setAmountTc(increTotalAmount);
                    debitBkGlSubList.setAmountLc(BigDecimal.ZERO);
                    debitBkGlSubList.setDrCr("dr");

                    totalDebit = totalDebit.add(increTotalAmount);
                    remoteBkGlSubLists.add(debitBkGlSubList);

                    i++;
                    RemoteBkGlSubListV2 creditBkGlSubList = new RemoteBkGlSubListV2();
                    creditBkGlSubList.setItemNo(String.valueOf(i));
                    creditBkGlSubList.setDescription(skuName);
                    creditBkGlSubList.setGlAccount(getCode(bkCoaRel, res, "cr"));
                    creditBkGlSubList.setNegPosting(false);
                    creditBkGlSubList.setAmountTc(increTotalAmount);
                    creditBkGlSubList.setAmountLc(BigDecimal.ZERO);
                    creditBkGlSubList.setDrCr("cr");
                    totalCredit = totalCredit.add(increTotalAmount);
                    remoteBkGlSubLists.add(creditBkGlSubList);
                }

            }
            remoteBkGl.setTotalDebit(totalDebit);
            remoteBkGl.setTotalCredit(totalCredit);
            remoteBkGl.setLineItems(remoteBkGlSubLists);
        } else {
            throw new RuntimeException("oms查询BookKeeping信息出错");
        }
        try {
            String besnString = JSONObject.toJSONString(remoteBkGl);
            JSONObject requestBody = JSONObject.parseObject(besnString);
            return bookKeepingService.postBkGL(requestBody);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    public String convertCurrency(String currency) {
        String currencyType = "0";
        switch (currency) {
            case "CNY":
                currencyType = "1";
                break;
            case "CAD":
                currencyType = "2";
                break;
            case "USD":
                currencyType = "3";
                break;
            default:
                break;
        }
        return currencyType;
    }

    public static void main(String[] args) {
        BigDecimal qty = new BigDecimal(2);

        BigDecimal a = new BigDecimal(128.2500);
        BigDecimal b = new BigDecimal(11);
        System.out.println(a.divide(b, 64, BigDecimal.ROUND_HALF_UP));
        BigDecimal divide = qty.multiply(a.divide(b, 10, BigDecimal.ROUND_HALF_UP));
        System.out.println(divide);
        System.out.println(a.divide(b, 4, BigDecimal.ROUND_HALF_UP));


        BigDecimal c = new BigDecimal(2.244567);
        BigDecimal d = new BigDecimal(1.580221);
        System.out.println(c.subtract(d));
        System.out.println(c.subtract(d).setScale(2, BigDecimal.ROUND_HALF_UP));
    }


}
