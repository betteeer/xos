package com.inossem.oms.svc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inossem.oms.base.common.constant.ModuleConstant;
import com.inossem.oms.base.svc.domain.SkuMaster;
import com.inossem.oms.base.svc.domain.StockBalance;
import com.inossem.oms.base.svc.domain.VO.SkuListReqVO;
import com.inossem.oms.base.svc.domain.Warehouse;
import com.inossem.oms.base.svc.mapper.StockBalanceMapper;
import com.inossem.oms.base.svc.vo.*;
import com.inossem.oms.mdm.service.SkuService;
import com.inossem.oms.mdm.service.WarehouseService;
import com.inossem.sco.common.core.exception.ServiceException;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StockBalanceService {

    @Resource
    private StockBalanceMapper stockBalanceMapper;

    @Resource
    private MaterialDocService materialDocService;

    @Resource
    private SkuService skuService;

    @Resource
    private WarehouseService warehouseService;

    public List<QueryStockBalanceResVo> list(QueryStockListVo queryStockListVo) {
        log.info("开始查询库存列表,传入参数:[{}]", queryStockListVo);
        // 远程调用根据Name查询Sku列表
        if (StringUtils.isNotBlank(queryStockListVo.getSearchText())) {
            SkuListReqVO skuListReqVO = new SkuListReqVO();
            skuListReqVO.setSkuName(queryStockListVo.getSearchText());
            skuListReqVO.setCompanyCode(queryStockListVo.getCompanyCode());
            List<SkuMaster> skuMasters = skuService.getFeignList(skuListReqVO);
//            List<SkuMaster> skuMasters = remoteMdmService.listSkuName(queryStockListVo.getSearchText(),queryStockListVo.getCompanyCode()).getData();
            queryStockListVo.setSkuMasters(skuMasters);
        }
        List<QueryStockBalanceResVo> stockBalances = stockBalanceMapper.selectListByQueryParam(queryStockListVo);
        for (int i = 0; i < stockBalances.size(); i++) {
//            SkuMaster skuMaster = remoteMdmService.getSkuByNumber(stockBalances.get(i).getSkuNumber(),stockBalances.get(i).getCompanyCode()).getData();
            SkuMaster skuMaster = skuService.getSku(stockBalances.get(i).getSkuNumber(), null, stockBalances.get(i).getCompanyCode());
            if (!StringUtils.isNull(skuMaster)) {
                stockBalances.get(i).setSkuMaster(skuMaster);
                stockBalances.get(i).setSkuName(skuMaster.getSkuName());
            }
//            Warehouse warehouse = remoteMdmService.getWarehourseByCode(stockBalances.get(i).getCompanyCode(), stockBalances.get(i).getWarehouseCode()).getData();
            Warehouse warehouse = warehouseService.getWarehouse(stockBalances.get(i).getCompanyCode(),stockBalances.get(i).getWarehouseCode());
            log.info("查询warehouse结果:[{}]", warehouse);
            if (!StringUtils.isNull(warehouse)) {
                stockBalances.get(i).setWarehouseName(warehouse.getName());
            }
        }
        log.info("结束查询库存列表,查询结果:[{}]", stockBalances);
        return stockBalances;
    }

    public CheckStockBalanceResVo checkStock(CheckStockBalanceParamVo checkStockBalanceParamVo) {
        CheckStockBalanceResVo checkStockBalanceResVo = new CheckStockBalanceResVo();
        boolean isAdequate = true;
        List<CheckStockBalanceResSubVo> checkStockBalanceResSubVos = new ArrayList<>();
        List<String> skuNumbers = checkStockBalanceParamVo.getCheckStockBalanceSubVos().stream().map(CheckStockBalanceParamSubVo::getSkuNumber).collect(Collectors.toList());
        LambdaQueryWrapper<StockBalance> wrapper = new LambdaQueryWrapper<StockBalance>()
                .eq(StockBalance::getWarehouseCode, checkStockBalanceParamVo.getWarehouseCode())
                .eq(StockBalance::getCompanyCode, checkStockBalanceParamVo.getCompanyCode())
                .in(StockBalance::getSkuNumber, skuNumbers);
        List<StockBalance> stockBalances = stockBalanceMapper.selectList(wrapper);
        for (CheckStockBalanceParamSubVo checkStockBalanceParamSubVo : checkStockBalanceParamVo.getCheckStockBalanceSubVos()) {
            CheckStockBalanceResSubVo checkStockBalanceResSubVo = new CheckStockBalanceResSubVo();
            checkStockBalanceResSubVo.setSkuNumber(checkStockBalanceParamSubVo.getSkuNumber());
            checkStockBalanceResSubVo.setUseQty(checkStockBalanceParamSubVo.getUseQty());
            StockBalance stockBalance = stockBalances.stream().filter(v -> v.getSkuNumber().equals(checkStockBalanceParamSubVo.getSkuNumber())).findFirst().orElse(null);
            if (StringUtils.isNull(stockBalance)) {
                checkStockBalanceResSubVo.setAdequate(false);
                isAdequate = false;
            } else {
                checkStockBalanceResSubVo.setAveragePrice(stockBalance.getAveragePrice());
                checkStockBalanceResSubVo.setAvailableQty(stockBalance.getTotalOnhandQty());

                // 等于0，说明库存相等，也是可以扣的吧
                if (stockBalance.getTotalOnhandQty().compareTo(checkStockBalanceParamSubVo.getUseQty()) < 0) {
//                if (stockBalance.getTotalOnhandQty().compareTo(checkStockBalanceParamSubVo.getUseQty()) <= 0) {
                    checkStockBalanceResSubVo.setAdequate(false);
                    isAdequate = false;
                } else {
                    checkStockBalanceResSubVo.setAdequate(true);
                }
            }
            checkStockBalanceResSubVos.add(checkStockBalanceResSubVo);
        }

//        for (CheckStockBalanceParamSubVo checkStockBalanceParamSubVo : checkStockBalanceParamVo.getCheckStockBalanceSubVos()) {
//            CheckStockBalanceResSubVo checkStockBalanceResSubVo = new CheckStockBalanceResSubVo();
//            LambdaQueryWrapper<StockBalance> stockBalanceLambdaQueryWrapper = new LambdaQueryWrapper<>();
//            stockBalanceLambdaQueryWrapper.eq(StockBalance::getSkuNumber, checkStockBalanceParamSubVo.getSkuNumber())
//                    .eq(StockBalance::getWarehouseCode, checkStockBalanceParamVo.getWarehouseCode())
//                    .eq(StockBalance::getCompanyCode, checkStockBalanceParamVo.getCompanyCode())
//                    .last("limit 1");
//            StockBalance stockBalance = stockBalanceMapper.selectOne(stockBalanceLambdaQueryWrapper);
//            checkStockBalanceResSubVo.setSkuNumber(checkStockBalanceParamSubVo.getSkuNumber());
//            checkStockBalanceResSubVo.setUseQty(checkStockBalanceParamSubVo.getUseQty());
//            if (StringUtils.isNull(stockBalance)) {
//                checkStockBalanceResSubVo.setAdequate(false);
//                isAdequate = false;
//            } else {
//                checkStockBalanceResSubVo.setAveragePrice(stockBalance.getAveragePrice());
//                checkStockBalanceResSubVo.setAvailableQty(stockBalance.getTotalOnhandQty());
//
//                // 等于0，说明库存相等，也是可以扣的吧
//                if (stockBalance.getTotalOnhandQty().compareTo(checkStockBalanceParamSubVo.getUseQty()) < 0) {
////                if (stockBalance.getTotalOnhandQty().compareTo(checkStockBalanceParamSubVo.getUseQty()) <= 0) {
//                    checkStockBalanceResSubVo.setAdequate(false);
//                    isAdequate = false;
//                } else {
//                    checkStockBalanceResSubVo.setAdequate(true);
//                }
//            }
//
//            checkStockBalanceResSubVos.add(checkStockBalanceResSubVo);
//        }
        checkStockBalanceResVo.setCheckStockBalanceSubVos(checkStockBalanceResSubVos);
        checkStockBalanceResVo.setAdequate(isAdequate);
        return checkStockBalanceResVo;
    }

    /**
     * 导入库存
     *
     * @param importStockBalanceVoList
     * @return
     */
    @Transactional
    public String importExcel(List<ImportStockBalanceVo> importStockBalanceVoList, String companyCode) throws ServiceException {
        //所有字段均不允许为空
        for (int i = 0; i < importStockBalanceVoList.size(); i++) {
            if (StringUtils.isNull(importStockBalanceVoList.get(i).getPostingDate())) {
                throw new ServiceException("Line " + i + " postingDate is a required field");
            }

            if (StringUtils.isBlank(importStockBalanceVoList.get(i).getStockStatus())) {
                throw new ServiceException("Line " + i + " stockStatus is a required field");
            }

            if (StringUtils.isBlank(importStockBalanceVoList.get(i).getSkuNumber())) {
                throw new ServiceException("Line " + i + " skuNumber is a required field");
            }

            if (StringUtils.isBlank(importStockBalanceVoList.get(i).getBasicUom())) {
                throw new ServiceException("Line " + i + " basicUom is a required field");
            }

            if (StringUtils.isBlank(importStockBalanceVoList.get(i).getCurrencyCode())) {
                throw new ServiceException("Line " + i + " currencyCode is a required field");
            }

            if (StringUtils.isBlank(importStockBalanceVoList.get(i).getWarehouseCode())) {
                throw new ServiceException("Line " + i + " warehouseCode is a required field");
            }

            if (StringUtils.isNull(importStockBalanceVoList.get(i).getSkuQty())) {
                throw new ServiceException("Line " + i + " skuQty is a required field");
            }

            if (StringUtils.isNull(importStockBalanceVoList.get(i).getTotalAmount())) {
                throw new ServiceException("Line " + i + " totalAmount is a required field");
            }
        }
        for (ImportStockBalanceVo importStockBalanceVo : importStockBalanceVoList) {
            if (importStockBalanceVo == null) {
                continue;
            }
            log.info("开始导入库存数据:[{}]", importStockBalanceVo);
            if (StringUtils.isEmpty(importStockBalanceVo.getStockStatus()) ||
                    StringUtils.isEmpty(importStockBalanceVo.getSkuNumber()) ||
                    StringUtils.isEmpty(importStockBalanceVo.getWarehouseCode()) ||
                    StringUtils.isNull(importStockBalanceVo.getPostingDate()) ||
                    importStockBalanceVo.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0 ||
                    importStockBalanceVo.getSkuQty().compareTo(BigDecimal.ZERO) <= 0) {
                log.error("库存数据导入,数据异常,数据值:[{}]", importStockBalanceVo);
                continue;
            }
            CreateMaterialDocVo createMaterialDocVo = new CreateMaterialDocVo();
            createMaterialDocVo.setMovementType(ModuleConstant.MOVEMENT_TYPE.Initial_Stock);
            createMaterialDocVo.setCompanyCode(companyCode);
            createMaterialDocVo.setPostingDate(importStockBalanceVo.getPostingDate());
            createMaterialDocVo.setStockStatus(importStockBalanceVo.getStockStatus());
            createMaterialDocVo.setWarehouseCode(importStockBalanceVo.getWarehouseCode());
            List<CreateMaterialDocSkuVo> createMaterialDocSkuVoList = new ArrayList<>();
            CreateMaterialDocSkuVo createMaterialDocSkuVo = new CreateMaterialDocSkuVo();
            createMaterialDocSkuVo.setSkuNumber(importStockBalanceVo.getSkuNumber());
            createMaterialDocSkuVo.setSkuQty(importStockBalanceVo.getSkuQty());
            createMaterialDocSkuVo.setTotalAmount(importStockBalanceVo.getTotalAmount());
            createMaterialDocSkuVo.setBasicUom(importStockBalanceVo.getBasicUom());
            createMaterialDocSkuVo.setCurrencyCode(importStockBalanceVo.getCurrencyCode());
            createMaterialDocSkuVoList.add(createMaterialDocSkuVo);
            createMaterialDocVo.setCreateMaterialDocSkuVoList(createMaterialDocSkuVoList);
            materialDocService.add(createMaterialDocVo);
        }
        return "成功";
    }

    public boolean checkStockWithoutWH(String skuNumber, BigDecimal useQty,String companyCode) {
        BigDecimal totalQty = stockBalanceMapper.selectSkuTotalQty(skuNumber,companyCode);
        if (totalQty == null) {
            return false;
        }
        return totalQty.compareTo(useQty) >= 0;
    }

    public QueryStockBalanceResVo getBySku(QueryStockBySkuVo queryStockListVo) {
        return stockBalanceMapper.selectStockBySkuAndCompany(queryStockListVo);
    }

    public List<QueryStockBalanceResVo> satetyList(QueryStockListVo queryStockListVo) {
        log.info(">>>>库存报警接口查询,入参:{}", queryStockListVo);
        List<QueryStockBalanceResVo> list = new ArrayList<>();
        // 远程调用根据Name查询Sku列表
        if (StringUtils.isNotBlank(queryStockListVo.getSearchText())) {
            SkuListReqVO skuListReqVO = new SkuListReqVO();
            skuListReqVO.setSkuName(queryStockListVo.getSearchText());
            skuListReqVO.setCompanyCode(queryStockListVo.getCompanyCode());
            List<SkuMaster> skuMasters = skuService.getFeignList(skuListReqVO);
            log.info("远程调用查询skuMastersByName结果:[{}]", skuMasters);
            queryStockListVo.setSkuMasters(skuMasters);
        }
        List<QueryStockBalanceResVo> stockBalances = stockBalanceMapper.selectListByQueryParam(queryStockListVo);
        for (int i = 0; i < stockBalances.size(); i++) {
            SkuMaster skuMaster = skuService.getSku(stockBalances.get(i).getSkuNumber(), null, stockBalances.get(i).getCompanyCode());
            log.info("远程调用查询Sku结果:[{}]", skuMaster);
            if (!StringUtils.isNull(skuMaster)) {
                stockBalances.get(i).setSkuMaster(skuMaster);
                stockBalances.get(i).setSkuName(skuMaster.getSkuName());
                if (null != skuMaster.getSkuSatetyStock()) {
                    log.info(">>>>>库存报警查询,查询到该sku:[{}]的安全库存值为:[{}]", stockBalances.get(i).getSkuNumber(), skuMaster.getSkuSatetyStock());
                    if (skuMaster.getSkuSatetyStock().compareTo(stockBalances.get(i).getTotalQty()) > 0) {
                        list.add(stockBalances.get(i));
                    }
                }
            }
            Warehouse warehouse = warehouseService.getWarehouse(stockBalances.get(i).getCompanyCode(), stockBalances.get(i).getWarehouseCode());
            log.info("远程调用查询warehouse结果:[{}]", warehouse);
            if (!StringUtils.isNull(warehouse)) {
                stockBalances.get(i).setWarehouseName(warehouse.getName());
            }
            log.info(">>>>>库存报警查询,查询到该sku:[{}]的库存数量值为:[{}]", stockBalances.get(i).getSkuNumber(), stockBalances.get(i).getTotalQty());


        }
        log.info(">>>>库存报警结束查询,查询结果:[{}]", list);
        return list;
    }


}
