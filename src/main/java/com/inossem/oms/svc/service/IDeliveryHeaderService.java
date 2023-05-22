package com.inossem.oms.svc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.inossem.oms.base.common.constant.ModuleConstant;
import com.inossem.oms.base.svc.domain.*;
import com.inossem.oms.base.svc.domain.VO.*;
import com.inossem.oms.base.svc.mapper.*;
import com.inossem.oms.base.svc.vo.*;
import com.inossem.oms.common.service.ITaxTableService;
import com.inossem.oms.mdm.service.AddressService;
import com.inossem.oms.mdm.service.BpService;
import com.inossem.oms.mdm.service.SkuService;
import com.inossem.sco.common.core.domain.R;
import com.inossem.sco.common.core.exception.ServiceException;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;


/**
 * Delivery Service接口
 *
 * @author shigf
 * @date 2022-10-17
 */
@Service
@Slf4j
public class IDeliveryHeaderService {

    public static final String TYPE_ADDRESS_SODN = "SODN";
    public static final String SUBTYPE_ADDRESS_SODN_SHIP = "shipto";

    @Resource
    private DeliveryHeaderMapper deliveryHeaderMapper;

    @Resource
    private DeliveryItemMapper deliveryItemMapper;

    @Resource
    private SoHeaderMapper soHeaderMapper;

    @Resource
    private PoHeaderMapper poHeaderMapper;

    @Resource
    private SoItemMapper soItemMapper;

    @Resource
    private MaterialDocService materialDocService;

    @Resource
    private StockBalanceService stockBalanceService;

    @Resource
    private PoItemMapper poItemMapper;

    @Resource
    private MaterialDocMapper materialDocMapper;

    @Resource
    private SoBillHeaderMapper soBillHeaderMapper;

    @Resource
    private BpService bpService;

    @Resource
    private AddressService addressService;

    @Resource
    private SkuService skuService;

    @Resource
    private ITaxTableService taxTableService;
    /**
     * by soNumber get shipped info card
     *
     * @param soNumber
     * @return
     */
    public List<DeliveryShippedResp> shipped(String soNumber, String companyCode) {
        List<DeliveryShippedResp> deliveryShippedResps = deliveryItemMapper.
                selectShippedHeader(soNumber, companyCode);
        if (deliveryShippedResps.size() > 0) {
            deliveryShippedResps.forEach(x -> {
                List<SoItem> soItems = getSoItems(companyCode, soNumber);
                //根据deliveryNumber查询shippingAddress
                SoShippingAddressVO soShippingAddressVO = new SoShippingAddressVO();
                soShippingAddressVO.setDeliveryKey(x.getDeliveryNumber());
                soShippingAddressVO.setCompanyCode(companyCode);
                log.info("查询发运卡片信息,deliveryNumber:{}", x.getDeliveryNumber());
                Address shippingAddress = addressService.getShippingAddress(soShippingAddressVO);
                if (shippingAddress != null) {
                    x.setShippingAddress(shippingAddress.getAddress1());
                }
                soItems.stream().forEach(s -> {
                    if (1 == s.getIsKitting()) {
                        for (int i = 0; i < s.getKittingItems().size(); i++) {
                            SkuKitting k = s.getKittingItems().get(i);
                            log.info("获取到的kitting组合中的sku为:{},数量为:{}", k.getComponentSku(), k.getComponentQty());
                            LambdaQueryWrapper<DeliveryItem> deliveryItemQuery = new LambdaQueryWrapper<DeliveryItem>()
                                    .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getDeliveryNumber, x.getDeliveryNumber()))
                                    .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getSkuNumber, k.getComponentSku()))
                                    .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getKittingSku, s.getSkuNumber()))
                                    .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getReferenceDocItem, s.getSoItem()))
                                    .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getReferenceDoc, soNumber))
                                    .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getCompanyCode, companyCode));
                            DeliveryItem d = deliveryItemMapper.selectOne(deliveryItemQuery);
                            log.info("获取到的发运表中的sku为:{},可发运数量为:{}", d.getSkuNumber(), d.getDeliveryQty());
                            log.info("获取到的发运表中的sku为:{},已经发运数量为:{}", d.getSkuNumber(), d.getDeliveredQty());
                            k.setId(d.getId());
                            k.setSalesQty(d.getDeliveryQty());
                            k.setShippedQty(d.getDeliveredQty());
                            if (i == s.getKittingItems().size() - 1) {
                                s.setSalesQty(d.getDeliveredQty().divide(k.getComponentQty(), 2, BigDecimal.ROUND_HALF_UP));
                                s.setBasicQty(d.getKittingDeliveryQty());
                            }
                        }
                    } else {
                        LambdaQueryWrapper<DeliveryItem> deliveryItemQuery = new LambdaQueryWrapper<DeliveryItem>()
                                .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getDeliveryNumber, x.getDeliveryNumber()))
                                .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getSkuNumber, s.getSkuNumber()))
                                .isNull(DeliveryItem::getKittingSku)
                                .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getReferenceDoc, soNumber))
                                .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getReferenceDocItem, s.getSoItem()))
                                .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getCompanyCode, companyCode));
                        DeliveryItem d = deliveryItemMapper.selectOne(deliveryItemQuery);
                        s.setId(d.getId());
                        s.setBasicQty(d.getDeliveryQty());
                        s.setShippedQTY(d.getDeliveredQty());
                    }
                });
                x.setSoItemList(soItems);
            });
        }
        return deliveryShippedResps;
    }

    /**
     * get delivery list
     *
     * @param deliveryedListQueryVo
     * @return
     */
    public List<DeliveryedListResp> selectDeliveryList(DeliveryedListQuery deliveryedListQueryVo) {
        if (StringUtils.isNotBlank(deliveryedListQueryVo.getShippingStaus())) {
            if (ModuleConstant.DELIVERY_SHIPPED_STATUS.UNFULFILED.equals(deliveryedListQueryVo.getShippingStaus())) {
                deliveryedListQueryVo.setCompleteDelivery(0);
            } else {
                deliveryedListQueryVo.setCompleteDelivery(1);
            }
        }
        return deliveryHeaderMapper.selectDeliveryList(deliveryedListQueryVo);
    }

    /**
     * by soNumber & company get so order header info
     *
     * @param soNumber
     * @param companyCode
     * @return
     */
    public OrderHeaderResp getOrderHeader(String soNumber, String companyCode) {
        //查询so_header中的信息
        OrderHeaderResp orderHeader = null;
        List<SoItem> list = getSoItems(companyCode, soNumber);

        //查询shippingAddress
        SoShippingAddressVO soShippingAddressVO = new SoShippingAddressVO();
        soShippingAddressVO.setCompanyCode(companyCode);

        //根据soNumber查询Delivery中是否有发运信息
        List<DeliveryItem> deliveryItemsInfo = deliveryItemMapper.getDeliveyItemsInfo(soNumber, companyCode);
        if (deliveryItemsInfo.size() > 0) {
            // 有发运记录
            // 查询发运是否存在 --> 没有发运时间的单子，表示没有发运
            // 20230115 去掉delivery_type查询条件，用以兼容inventory so 和 service so
            List<String> s = deliveryItemMapper.getShippedDateIsNull(soNumber, companyCode, null);
            if (s.size() == 0) {
                //有发运记录  但是不存在发运时间为空的情况
                soShippingAddressVO.setDeliveryKey(deliveryItemsInfo.get(0).getDeliveryNumber());
                orderHeader = soHeaderMapper.getOrderHeader(soNumber, companyCode);
                //查询so_items中的信息
                list.stream().forEach(x -> {
                    if (1 == x.getIsKitting()) {
                        for (int i1 = 0; i1 < x.getKittingItems().size(); i1++) {
                            SkuKitting kitItems = x.getKittingItems().get(i1);
                            BigDecimal shippedQTY = deliveryItemMapper.getShippedQTY(kitItems.getComponentSku(),
                                    soNumber, companyCode, x.getSkuNumber(), x.getSoItem());
                            kitItems.setSalesQty(x.getSalesQty().multiply(kitItems.getComponentQty()).subtract(shippedQTY));
                            if (i1 == x.getKittingItems().size() - 1) {
                                BigDecimal divide = shippedQTY.divide(kitItems.getComponentQty(), 2, BigDecimal.ROUND_HALF_UP);
                                BigDecimal subtract = x.getSalesQty().subtract(divide);
                                x.setSalesQty(subtract);
                            }
                        }
                    } else {
                        //根据skuNumber和soNumber和companyCode 查询deliveryItem 得到sku已经发运的数量
                        BigDecimal shippedQTY = deliveryItemMapper.getShippedQTY(x.getSkuNumber(), soNumber, companyCode, null, x.getSoItem());
                        x.setBasicQty(x.getBasicQty().subtract(shippedQTY));
                    }
                });
                orderHeader.setSoItemList(list);
            } else {
                //有发运记录  并且存在有发运时间为空的情况
                orderHeader = soHeaderMapper.getOrderHeaders(soNumber, companyCode);
                soShippingAddressVO.setDeliveryKey(orderHeader.getDeliveryNumber());
                list.stream().forEach(x -> {
                    if (1 == x.getIsKitting()) {
                        for (int i1 = 0; i1 < x.getKittingItems().size(); i1++) {
                            SkuKitting kitItems = x.getKittingItems().get(i1);

                            // 去掉delivery_type 条件，用以兼容inventory so an service so
                            Long itemId = deliveryItemMapper.getShippedDateIsNullItemId(soNumber, companyCode, kitItems.getComponentSku(),
                                    null, x.getSkuNumber(), x.getSoItem());
                            log.info("获取到的组合sku:{}的deliveryItem中的id为:{}", kitItems.getComponentSku(), itemId);
                            kitItems.setId(itemId);
                            BigDecimal shippedQTY = deliveryItemMapper.getShippedQTY(kitItems.getComponentSku(), soNumber, companyCode,
                                    x.getSkuNumber(), null);
                            kitItems.setSalesQty(x.getSalesQty().multiply(kitItems.getComponentQty()).subtract(shippedQTY));
                            if (i1 == x.getKittingItems().size() - 1) {
                                BigDecimal divide = shippedQTY.divide(kitItems.getComponentQty(), 2, BigDecimal.ROUND_HALF_UP);
                                BigDecimal subtract = x.getSalesQty().subtract(divide);
                                x.setSalesQty(subtract);
                            }
                        }
                    } else {
                        //根据skuNumber和soNumber和companyCode 查询deliveryItem 得到sku已经发运的数量
                        BigDecimal shippedQTY = deliveryItemMapper.getShippedQTY(x.getSkuNumber(), soNumber, companyCode, null, x.getSoItem());
                        x.setBasicQty(x.getBasicQty().subtract(shippedQTY));

                        // 去掉delivery_type，用于兼容 inventory so and service so
                        Long itemId = deliveryItemMapper.getShippedDateIsNullItemId(soNumber, companyCode, x.getSkuNumber(),
                                null, null, x.getSoItem());

                        log.info("获取到的正常sku:{}的deliveryItem中的id为:{}", x.getSkuNumber(), itemId);
                        x.setId(itemId);
                    }
                });
                orderHeader.setSoItemList(list);
            }
        } else {
            //没有发运记录
            soShippingAddressVO.setSoKey(soNumber);
            orderHeader = soHeaderMapper.getOrderHeader(soNumber, companyCode);
            //查询so_items中的信息

            list.forEach(x -> {
                if (1 == x.getIsKitting()) {
                    x.getKittingItems().forEach(kitItems -> {
                        kitItems.setSalesQty(x.getSalesQty().multiply(kitItems.getComponentQty()));
                    });
                }
            });
            orderHeader.setSoItemList(list);
        }
        //根据bpCustomer查询bpName
        BusinessPartner bp = bpService.getBpNameByBpNumber(companyCode, orderHeader.getBusinessPartner());
        if (null != bp) {
            orderHeader.setBusinessName(bp.getBpName());
        }
        Address shippingAddress = addressService.getShippingAddress(soShippingAddressVO);
        if (null != shippingAddress) {
            orderHeader.setShippingAddress(shippingAddress.getAddress1());
        }
        return orderHeader;
    }


    private List<SoItem> getSoItems(String companyCode, String soNumber) {
        // 查soItem
        LambdaQueryWrapper<SoItem> itemWrapper = new LambdaQueryWrapper<SoItem>().
                and(com -> com.eq(SoItem::getCompanyCode, companyCode)).
                and(com -> com.eq(SoItem::getSoNumber, soNumber)).
                and(com -> com.eq(SoItem::getIsDeleted, 0))
                .orderByAsc(SoItem::getSoItem);
        List<SoItem> items = soItemMapper.selectList(itemWrapper);
        log.info("查到的 {} 的明细条数为：{}", soNumber, items.size());
        items.stream().forEach(si -> {
            //如果是个kitting  需要去查kitting中的子sku
            if (1 == si.getIsKitting()) {
                SkuMaster skuByNumberAndVersion = skuService.getSku(si.getSkuNumber(), si.getSkuVersion(), si.getCompanyCode());
                log.info("调用KittingSKU服务查询SKU {} 信息的状态为：{}", si.getSoNumber(), skuByNumberAndVersion);
                if (null == skuByNumberAndVersion) {
                    throw new RuntimeException("kittingSKU服务调用失败，获取SKU信息未成功");
                }
                if (!StringUtils.isEmpty(skuByNumberAndVersion.getSkuName())) {
                    si.setSkuName(skuByNumberAndVersion.getSkuName());
                }
                if (!StringUtils.isEmpty(skuByNumberAndVersion.getSkuDescription())) {
                    si.setSkuDescription(skuByNumberAndVersion.getSkuDescription());
                }
                si.setKittingItems(skuByNumberAndVersion.getKittingItems());
            } else {
                SkuMaster skuRes = skuService.getSku(si.getSkuNumber(), null, si.getCompanyCode());
                log.info("调用SKU服务查询SKU {} 信息的状态为：{}", si.getSoNumber(), skuRes);
                if (skuRes == null) {
                    throw new RuntimeException("SKU服务调用失败，获取SKU信息未成功");
                }
                if (!StringUtils.isEmpty(skuRes.getSkuName())) {
                    si.setSkuName(skuRes.getSkuName());
                }
            }
        });
        return items;
    }


    /**
     * create so delivery
     *
     * @param deliveryInfoVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public DeliveryHeader createDelivery(DeliveryInfoVo deliveryInfoVo) {
        // 查询当前so发运了几次
        List<DeliveryItem> shippedNumbers = this.getShippedNumber(deliveryInfoVo);
        log.info("创建so delivery,参数{}", deliveryInfoVo);

        // 是否是空发运
        boolean isBlankDelivery = false;
        // 是否是service so
        boolean isServiceDelivery = ModuleConstant.DELIVERY_TYPE.SERVICE_SO.equals(deliveryInfoVo.getDeliveryType());

        // 如果发运类型为 SEDN,表示service类型的发运，不扣库存，不检验库存

        // 如果发货时间为空  说明该订单的item没有填写发货数量   也就是表示这单不算发运 也就不可以扣减库存
        if ("".equals(deliveryInfoVo.getPostingDate()) || null == deliveryInfoVo.getPostingDate()) {
            isBlankDelivery = true;
        }

        //用于存储该deliveryItem中每一条的wareHouse,判断wareHouse是否一致
        Set<String> wareHouseSet = new HashSet<>();

        Boolean isALikeSku = false;

        //存放相同sku
        Map<String, List<BigDecimal>> aLikeSku = new HashMap<>();

        for (int i = 0; i < deliveryInfoVo.getDeliveryItemList().size(); i++) {
            DeliveryItem x = deliveryInfoVo.getDeliveryItemList().get(i);
            //如果发货时间不为空,则说明用户填写了发运数量  需要验证这些sku的发运数量  验证分为两块  ①订单销售数量校验  ②库存校验
            if (!isBlankDelivery) {
                //如果已经发运了8次  那本次就为第9次  就要验证用户输入数量要等于可发运数量
                if (shippedNumbers.size() >= 998) {
                    //订单销售数量校验
                    if (x.getDeliveredQty().compareTo(x.getDeliveryQty()) == -1) {
                        // 用户输入数量 小于 订单剩余可发运数量
                        throw new ServiceException("本次发运为系统默认最后一次发运(9次),您填写的发运数量不符合要求,请将sku全部发运");
                    }
                }

                //订单销售数量校验
                if (x.getDeliveredQty().compareTo(x.getDeliveryQty()) == 1) {
                    // 用户输入数量 大于 订单剩余可发运数量
                    throw new ServiceException("Creation not allowed: sku[" + x.getSkuNumber() + "] greater than the order sales quantity");
                }

                //验证sku库存是否充足
                CheckStockBalanceParamVo checkStockBalanceParamVo = new CheckStockBalanceParamVo();
                checkStockBalanceParamVo.setWarehouseCode(x.getWarehouseCode());
                checkStockBalanceParamVo.setCompanyCode(x.getCompanyCode());

                List<CheckStockBalanceParamSubVo> list = new ArrayList<>();

                // 数量大于0去校验库存
                if (x.getDeliveredQty().compareTo(BigDecimal.ZERO) > 0) {
                    if (aLikeSku.isEmpty()) {
                        List<BigDecimal> lists = new ArrayList<>();
                        lists.add(x.getDeliveredQty());
                        aLikeSku.put(x.getSkuNumber(), lists);
                    } else {
                        if (aLikeSku.containsKey(x.getSkuNumber())) {
                            aLikeSku.get(x.getSkuNumber()).add(x.getDeliveryQty());
                            isALikeSku = true;
                        } else {
                            List<BigDecimal> lists = new ArrayList<>();
                            lists.add(x.getDeliveredQty());
                            aLikeSku.put(x.getSkuNumber(), lists);
                        }
                    }
                    CheckStockBalanceParamSubVo checkStockBalanceParamSubVo = new CheckStockBalanceParamSubVo();
                    checkStockBalanceParamSubVo.setSkuNumber(x.getSkuNumber());
                    checkStockBalanceParamSubVo.setUseQty(x.getDeliveredQty());
                    list.add(checkStockBalanceParamSubVo);

                    checkStockBalanceParamVo.setCheckStockBalanceSubVos(list);

                    CheckStockBalanceResVo checkStockBalanceResVo = stockBalanceService.checkStock(checkStockBalanceParamVo);

                    // service so 不校验库存
                    if (!isServiceDelivery && !checkStockBalanceResVo.getCheckStockBalanceSubVos().get(0).isAdequate()) {
                        throw new ServiceException("Creation not allowed: sku[" + x.getSkuNumber() + "] greater than inventory quantity");
                    }
                    log.info(">>>> so create delivery ,skuNumber:{}，获取到的移动平均价为：{}", x.getSkuNumber(), checkStockBalanceResVo.getCheckStockBalanceSubVos().get(0).getAveragePrice());
                    x.setAvagUnitPrice(checkStockBalanceResVo.getCheckStockBalanceSubVos().get(0).getAveragePrice());
                }
            }
            if (isBlankDelivery) {
                wareHouseSet.add(x.getWarehouseCode());
            }
        }

        if (isBlankDelivery) {
            //验证wareHouse是否一致
            if (wareHouseSet.size() > 1) {
                throw new ServiceException("Creation not allowed: Warehouse codes must be consistent");
            }
        }

        //存在相同sku的情况   针对相同的sku 判断库存是否充足
        if (isALikeSku) {
            for (String s : aLikeSku.keySet()) {
                List<BigDecimal> bigDecimals = aLikeSku.get(s);
                if (bigDecimals.size() > 1) {
                    CheckStockBalanceParamVo checkStockBalanceParamVo = new CheckStockBalanceParamVo();
                    checkStockBalanceParamVo.setWarehouseCode(deliveryInfoVo.getWarehouseCode());
                    checkStockBalanceParamVo.setCompanyCode(deliveryInfoVo.getCompanyCode());
                    List<CheckStockBalanceParamSubVo> list = new ArrayList<>();
                    BigDecimal b = BigDecimal.ZERO;
                    for (int i = 0; i < bigDecimals.size(); i++) {
                        log.info(">>>so delivery相同sku ,skuNumber:{},第：{}个数量为：{}", s, i + 1, bigDecimals.get(i));
                        BigDecimal bigDecimal = bigDecimals.get(i);
                        b = b.add(bigDecimal);
                        log.info(">>>so delivery相同sku ,skuNumber:{},第：{}个总数量为：{}", s, i + 1, b);
                    }
                    CheckStockBalanceParamSubVo checkStockBalanceParamSubVo = new CheckStockBalanceParamSubVo();
                    checkStockBalanceParamSubVo.setSkuNumber(s);
                    checkStockBalanceParamSubVo.setUseQty(b);
                    list.add(checkStockBalanceParamSubVo);
                    checkStockBalanceParamVo.setCheckStockBalanceSubVos(list);
                    CheckStockBalanceResVo checkStockBalanceResVo = stockBalanceService.checkStock(checkStockBalanceParamVo);
                    //service so 不校验库存
                    if (!isServiceDelivery && !checkStockBalanceResVo.getCheckStockBalanceSubVos().get(0).isAdequate()) {
                        throw new ServiceException("Creation not allowed: sku[" + s + "] greater than inventory quantity");
                    }
                }
            }
        }

        DeliveryHeader deliveryHeader = this.deliveryHeaderInfoPacking(isBlankDelivery, deliveryInfoVo);
        try {
            deliveryHeaderMapper.insert(deliveryHeader);
            //封装Delivery_item所需参数
            List<DeliveryItem> deliveryItem = new ArrayList<>();
            //生成物料凭证所需参数
            List<CreateMaterialDocSkuVo> cmdSkuVoList = new ArrayList<>();

            List<DeliveryItem> deliveryListParams = deliveryInfoVo.getDeliveryItemList();

            for (int i = 0; i < deliveryListParams.size(); i++) {
                DeliveryItem x = deliveryListParams.get(i);
                DeliveryItem dItem = new DeliveryItem();
                dItem.setCompanyCode(deliveryHeader.getCompanyCode());
                dItem.setDeliveryNumber(deliveryHeader.getDeliveryNumber());
                dItem.setDeliveryItem(x.getDeliveryItem());
                dItem.setReferenceDoc(x.getReferenceDoc());
                dItem.setReferenceDocItem(x.getReferenceDocItem());
                dItem.setSkuNumber(x.getSkuNumber());
                SkuMaster sku = skuService.getSku(x.getSkuNumber(),null,deliveryHeader.getCompanyCode());
                if (sku == null) {
                    throw new RuntimeException("skuNumber:[" + x.getSkuNumber() + "]查询sku信息失败");
                }
                if (StringUtils.isNotBlank((x.getKittingSku()))) {
                    dItem.setKittingSku(x.getKittingSku());
                    dItem.setKittingDeliveryQty(x.getKittingDeliveryQty());
                }
                if (StringUtils.isNotBlank(x.getBpSkuNumber())) {
                    dItem.setBpSkuNumber(x.getBpSkuNumber());
                }
                if (StringUtils.isNotBlank(x.getWarehouseCode())) {
                    dItem.setWarehouseCode(x.getWarehouseCode());
                }
                dItem.setCompleteDelivery(0);
                dItem.setDeliveryQty(x.getDeliveryQty());
                dItem.setDeliveredQty(x.getDeliveredQty());
                dItem.setBasicUom(x.getBasicUom());
                dItem.setIsDeleted(0);
                deliveryItem.add(dItem);
                if (BigDecimal.ZERO.equals(x.getDeliveredQty())) {
                    continue;
                } else {
                    if (!isBlankDelivery) {
                        CreateMaterialDocSkuVo cmdSkuVo = new CreateMaterialDocSkuVo();
                        cmdSkuVo.setSkuNumber(x.getSkuNumber());
                        cmdSkuVo.setSkuQty(x.getDeliveredQty());
                        cmdSkuVo.setBasicUom(x.getBasicUom());

                        //根据skuNumber 及 soNumber查询 soItem  获取单价 及货币编号
                        //根据以下条件查询so_item中当前sku的sales qty
                        QueryWrapper<SoItem> sqw = new QueryWrapper<>();
                        sqw.eq("so_number", x.getReferenceDoc());
                        if (StringUtils.isNotBlank(x.getKittingSku())) {
                            sqw.eq("sku_number", x.getKittingSku());
                        } else {
                            sqw.eq("sku_number", x.getSkuNumber());
                        }
                        sqw.eq("so_item", x.getReferenceDocItem());
                        sqw.eq("company_code", x.getCompanyCode());
                        sqw.eq("is_deleted", 0);
                        SoItem soItem = soItemMapper.selectOne(sqw);
                        cmdSkuVo.setItemAmount(x.getAvagUnitPrice());
                        cmdSkuVo.setCurrencyCode(soItem.getCurrencyCode());
                        cmdSkuVo.setReferenceNumber(deliveryHeader.getDeliveryNumber());
                        cmdSkuVo.setReferenceItem(x.getDeliveryItem());
                        cmdSkuVoList.add(cmdSkuVo);
                    }
                }
            }
            deliveryItemMapper.insertBatch(deliveryItem);



            if (cmdSkuVoList.size() > 0) {
                // 非空发运不扣减库存，且不更新发运状态
                if (!isBlankDelivery) {
                    // 只有inventory so扣减库存，service so不扣减库存
                    if (ModuleConstant.DELIVERY_TYPE.INVENTORY_SO.equals(deliveryInfoVo.getDeliveryType())) {
                        CreateMaterialDocVo cmdVo = new CreateMaterialDocVo();
                        cmdVo.setCompanyCode(deliveryInfoVo.getCompanyCode());
                        cmdVo.setPostingDate(deliveryInfoVo.getPostingDate());
                        cmdVo.setMovementType(ModuleConstant.MOVEMENT_TYPE.SO_delivery);
                        cmdVo.setWarehouseCode(deliveryInfoVo.getWarehouseCode());
                        cmdVo.setStockStatus("A");
                        cmdVo.setReferenceType("DN");
                        cmdVo.setCreateMaterialDocSkuVoList(cmdSkuVoList);
                        materialDocService.add(cmdVo);
                    }
                }

                //当创建完物料凭证后 通过delivery_number更新delivery_header 及 delivery_item中的complete_delivery 为 1-已完成交付
                QueryWrapper<DeliveryHeader> queryHeaderWrapper = new QueryWrapper<>();
                queryHeaderWrapper.eq("delivery_number", deliveryHeader.getDeliveryNumber())
                        .eq("company_code", deliveryHeader.getCompanyCode())
                        .eq("is_deleted", 0);
                DeliveryHeader dh = new DeliveryHeader();
                dh.setCompleteDelivery(1);
                deliveryHeaderMapper.update(dh, queryHeaderWrapper);

                QueryWrapper<DeliveryItem> queryItemWrapper = new QueryWrapper<>();
                queryItemWrapper.eq("delivery_number", deliveryHeader.getDeliveryNumber())
                        .eq("company_code", deliveryHeader.getCompanyCode())
                        .eq("is_deleted", 0);
                DeliveryItem dhi = new DeliveryItem();
                dhi.setCompleteDelivery(1);
                deliveryItemMapper.update(dhi, queryItemWrapper);

                //修改完Delivery相关状态后,需要修改soHeader中的订单状态
                //从unfullfilled 变更为 partially fullfilled 或 fully fullfilled
                //查询so_item 得到所有的sku 及 sku的可销售数量
                //判断这些sku是否都已经发运完成  还是 部分发运
                List<SoItem> soItems = getSoItems(deliveryInfoVo.getCompanyCode(), deliveryInfoVo.getSoNumber());
                boolean c = false;
                for (int i = 0; i < soItems.size(); i++) {
                    SoItem x = soItems.get(i);
                    if (1 == x.getIsKitting()) {
                        for (int i1 = 0; i1 < x.getKittingItems().size(); i1++) {
                            SkuKitting skuKitting = x.getKittingItems().get(i1);
                            BigDecimal sum = x.getSalesQty().multiply(skuKitting.getComponentQty());
                            //根据查询到的sku 去 delivery表中查询发运数量
                            QueryWrapper<DeliveryItem> dqw = new QueryWrapper<>();
                            dqw.select("sum(delivered_qty) as delivered_qty");
                            dqw.eq("company_code", deliveryInfoVo.getCompanyCode());
                            dqw.eq("reference_doc", deliveryInfoVo.getSoNumber());
                            dqw.eq("sku_number", skuKitting.getComponentSku());
                            dqw.eq("reference_doc_item", x.getSoItem());
                            dqw.eq("is_deleted", 0);
                            DeliveryItem deliveryItem1 = deliveryItemMapper.selectOne(dqw);
                            BigDecimal bc = BigDecimal.ZERO;
                            if (deliveryItem1 != null) {
                                bc = bc.add(deliveryItem1.getDeliveredQty());
                            }
                            if (sum.compareTo(bc) == 0) {
                                c = true;
                            } else {
                                c = false;
                                break;
                            }
                        }
                    } else {
                        //根据查询到的sku 去 delivery表中查询发运数量
                        QueryWrapper<DeliveryItem> dqw = new QueryWrapper<>();
                        dqw.select("sum(delivered_qty) as delivered_qty");
                        dqw.eq("company_code", deliveryInfoVo.getCompanyCode());
                        dqw.eq("reference_doc", deliveryInfoVo.getSoNumber());
                        dqw.eq("sku_number", x.getSkuNumber());
                        dqw.eq("reference_doc_item", x.getSoItem());
                        dqw.eq("is_deleted", 0);
                        DeliveryItem deliveryItem1 = deliveryItemMapper.selectOne(dqw);
                        BigDecimal bc = BigDecimal.ZERO;
                        if (deliveryItem1 != null) {
                            bc = bc.add(deliveryItem1.getDeliveredQty());
                        }
                        if (x.getBasicQty().compareTo(bc) == 0) {
                            c = true;
                        } else {
                            c = false;
                            break;
                        }
                    }
                }
                SoHeader soHeader = new SoHeader();
                if (!c) {
                    soHeader.setDeliveryStatus(ModuleConstant.SOPO_DELIVERY_STATUS.PARTIALLY_FULLFILLED);
                } else {
                    soHeader.setDeliveryStatus(ModuleConstant.SOPO_DELIVERY_STATUS.FULLFILLED);
                }
                QueryWrapper<SoHeader> shqw = new QueryWrapper<>();
                shqw.eq("so_number", deliveryInfoVo.getSoNumber());
                shqw.eq("company_code", deliveryInfoVo.getCompanyCode());
                shqw.eq("is_deleted", 0);
                soHeaderMapper.update(soHeader, shqw);
            }

            AddressSaveVo shipAddress = deliveryInfoVo.getShippingAddress();
            shipAddress.setCompanyCode(deliveryInfoVo.getCompanyCode());
            shipAddress.setType(TYPE_ADDRESS_SODN);
            shipAddress.setSubType(SUBTYPE_ADDRESS_SODN_SHIP);
            shipAddress.setKey(deliveryHeader.getDeliveryNumber());
            addressService.save(shipAddress);
        } catch (Exception e) {
            log.error("create delivery failed", e);
            throw new RuntimeException(e);
        }
        return deliveryHeader;
    }

    /**
     * Packaging Delivery Header Params
     *
     * @param deliveryInfoVo
     * @return
     */
    private DeliveryHeader deliveryHeaderInfoPacking(Boolean b, DeliveryInfoVo deliveryInfoVo) {
        DeliveryHeader dh = new DeliveryHeader();
        //参数封装
        //生成Delivery_Number
        String soNumber = deliveryInfoVo.getSoNumber();
        QueryWrapper<DeliveryItem> deliveryItemQueryWrapper = new QueryWrapper<>();
        deliveryItemQueryWrapper.eq("reference_doc", soNumber);
        deliveryItemQueryWrapper.eq("company_code", deliveryInfoVo.getCompanyCode());
        deliveryItemQueryWrapper.orderByDesc("id");
        deliveryItemQueryWrapper.last("limit 1");
        DeliveryItem deliveryItem = deliveryItemMapper.selectOne(deliveryItemQueryWrapper);
        String deliveryNumber = null;
        if (deliveryItem != null) {
            deliveryNumber = BigDecimal.ONE.add(new BigDecimal(deliveryItem.getDeliveryNumber())).toString();
        } else {
            deliveryNumber = "9" + soNumber + "001";
        }
        dh.setCompanyCode(deliveryInfoVo.getCompanyCode());
        dh.setDeliveryNumber(deliveryNumber);
        dh.setDeliveryType(deliveryInfoVo.getDeliveryType());
        dh.setCompleteDelivery(0);
        dh.setDeliveryDate(deliveryInfoVo.getDeliveryDate());
        if (!b) {
            dh.setPostingDate(deliveryInfoVo.getPostingDate());
        }
        dh.setBpCustomer(deliveryInfoVo.getBpCustomer());
        dh.setBpVendor(deliveryInfoVo.getBpVendor());
        if (StringUtils.isNotBlank(deliveryInfoVo.getWarehouseCode())) {
            dh.setWarehouseCode(deliveryInfoVo.getWarehouseCode());
        }
        dh.setCarrierCode(deliveryInfoVo.getCarrierCode());
        dh.setTrackingNumber(deliveryInfoVo.getTrackingNumber());
        dh.setDeliveryNotes(deliveryInfoVo.getDeliveryNotes());
        Date date = new Date();
        dh.setGmtCreate(date);
        dh.setGmtModified(date);
        // ###todo###
        dh.setCreateBy(String.valueOf(1));
        // ###todo###
        dh.setModifiedBy(String.valueOf(1));
        dh.setIsDeleted(0);
        return dh;
    }


    /**
     * modify so delivery
     *
     * @param deliveryInfoVo
     * @return
     */
    public DeliveryHeader modifyDelivery(DeliveryInfoVo deliveryInfoVo) {
        // 修改发运信息，必须带着posting date
        if ("".equals(deliveryInfoVo.getPostingDate()) || null == deliveryInfoVo.getPostingDate()) {
            throw new IllegalArgumentException("posting date is null");
        }

        // 查发运次数
        List<DeliveryItem> shippedNumbers = this.getShippedNumber(deliveryInfoVo);

        // 是否是service so
        boolean isServiceDelivery = ModuleConstant.DELIVERY_TYPE.SERVICE_SO.equals(deliveryInfoVo.getDeliveryType());
        // 如果发运类型为 SEDN,表示service类型的发运，不扣库存，不检验库存

        //生成物料凭证所需参数
        List<CreateMaterialDocSkuVo> cmdSkuVoList = new ArrayList<>();
        //用于存储该deliveryItem中每一条的wareHouse,判断wareHouse是否一致
        Set<String> wareHouseSet = new HashSet<>();

        Boolean isALikeSku = false;

        //存放相同sku
        Map<String, List<BigDecimal>> aLikeSku = new HashMap<>();

        for (int i = 0; i < deliveryInfoVo.getDeliveryItemList().size(); i++) {
            DeliveryItem deliveryItem = deliveryInfoVo.getDeliveryItemList().get(i);
            //验证sku的发运数量  验证分为两块  ①订单销售数量校验  ②库存校验
            //订单销售数量校验
            //如果本次空单为第9次  也就是最后一次  就要验证用户输入数量要等于可发运数量
            if (shippedNumbers.size() >= 999) {
                //订单销售数量校验
                if (deliveryItem.getDeliveredQty().compareTo(deliveryItem.getDeliveryQty()) == -1) {
                    // 用户输入数量 小于 订单剩余可发运数量
                    throw new ServiceException("本次发运为系统默认最后一次发运(9次),您填写的发运数量不符合要求,请将sku全部发运");
                }
            }

            if (deliveryItem.getDeliveredQty().compareTo(deliveryItem.getDeliveryQty()) == 1) {
                // 用户输入数量 大于 订单剩余可发运数量
                throw new ServiceException("Creation not allowed: sku[" + deliveryItem.getSkuNumber() + "] greater than the order sales quantity");
            }

            //验证sku库存是否充足
            CheckStockBalanceParamVo checkStockBalanceParamVo = new CheckStockBalanceParamVo();
            checkStockBalanceParamVo.setWarehouseCode(deliveryItem.getWarehouseCode());
            checkStockBalanceParamVo.setCompanyCode(deliveryItem.getCompanyCode());
            List<CheckStockBalanceParamSubVo> list = new ArrayList<>();

            if (aLikeSku.isEmpty()) {
                List<BigDecimal> lists = new ArrayList<>();
                lists.add(deliveryItem.getDeliveredQty());
                aLikeSku.put(deliveryItem.getSkuNumber(), lists);
            } else {
                if (aLikeSku.containsKey(deliveryItem.getSkuNumber())) {
                    aLikeSku.get(deliveryItem.getSkuNumber()).add(deliveryItem.getDeliveryQty());
                    isALikeSku = true;
                } else {
                    List<BigDecimal> lists = new ArrayList<>();
                    lists.add(deliveryItem.getDeliveredQty());
                    aLikeSku.put(deliveryItem.getSkuNumber(), lists);
                }
            }

            CheckStockBalanceParamSubVo checkStockBalanceParamSubVo = new CheckStockBalanceParamSubVo();
            checkStockBalanceParamSubVo.setSkuNumber(deliveryItem.getSkuNumber());
            checkStockBalanceParamSubVo.setUseQty(deliveryItem.getDeliveredQty());
            list.add(checkStockBalanceParamSubVo);
            checkStockBalanceParamVo.setCheckStockBalanceSubVos(list);
            CheckStockBalanceResVo checkStockBalanceResVo = stockBalanceService.checkStock(checkStockBalanceParamVo);
            //如果校验失败 则报错提示
            if (!isServiceDelivery && !checkStockBalanceResVo.getCheckStockBalanceSubVos().get(0).isAdequate()) {
                throw new ServiceException("Creation not allowed: sku[" + deliveryItem.getSkuNumber() + "] greater than inventory quantity");
            }

            log.info(">>>> so create delivery ,skuNumber:{}，获取到的移动平均价为：{}", deliveryItem.getSkuNumber(), checkStockBalanceResVo.getCheckStockBalanceSubVos().get(0).getAveragePrice());
            deliveryItem.setAvagUnitPrice(checkStockBalanceResVo.getCheckStockBalanceSubVos().get(0).getAveragePrice());

            wareHouseSet.add(deliveryItem.getWarehouseCode());
        }
        //验证wareHouse是否一致
        if (wareHouseSet.size() > 1) {
            throw new ServiceException("Creation not allowed: Warehouse codes must be consistent");
        }

        //存在相同sku的情况   针对相同的sku 判断库存是否充足
        if (isALikeSku) {
            for (String s : aLikeSku.keySet()) {
                List<BigDecimal> bigDecimals = aLikeSku.get(s);
                if (bigDecimals.size() > 1) {
                    CheckStockBalanceParamVo checkStockBalanceParamVo = new CheckStockBalanceParamVo();
                    checkStockBalanceParamVo.setWarehouseCode(deliveryInfoVo.getWarehouseCode());
                    checkStockBalanceParamVo.setCompanyCode(deliveryInfoVo.getCompanyCode());
                    List<CheckStockBalanceParamSubVo> list = new ArrayList<>();
                    BigDecimal b = BigDecimal.ZERO;
                    for (int i = 0; i < bigDecimals.size(); i++) {
                        log.info(">>>so delivery相同sku ,skuNumber:{},第：{}个数量为：{}", s, i + 1, bigDecimals.get(i));
                        BigDecimal bigDecimal = bigDecimals.get(i);
                        b = b.add(bigDecimal);
                        log.info(">>>so delivery相同sku ,skuNumber:{},第：{}个总数量为：{}", s, i + 1, b);
                    }
                    CheckStockBalanceParamSubVo checkStockBalanceParamSubVo = new CheckStockBalanceParamSubVo();
                    checkStockBalanceParamSubVo.setSkuNumber(s);
                    checkStockBalanceParamSubVo.setUseQty(b);
                    list.add(checkStockBalanceParamSubVo);
                    checkStockBalanceParamVo.setCheckStockBalanceSubVos(list);
                    CheckStockBalanceResVo checkStockBalanceResVo = stockBalanceService.checkStock(checkStockBalanceParamVo);
                    //service so 不校验库存
                    if (!isServiceDelivery && !checkStockBalanceResVo.getCheckStockBalanceSubVos().get(0).isAdequate()) {
                        throw new ServiceException("Creation not allowed: sku[" + s + "] greater than inventory quantity");
                    }
                }
            }
        }

        DeliveryHeader dhr = new DeliveryHeader();
        dhr.setId(deliveryInfoVo.getId());
        dhr.setCompanyCode(deliveryInfoVo.getCompanyCode());
        dhr.setDeliveryNumber(deliveryInfoVo.getDeliveryNumber());
        dhr.setDeliveryType(deliveryInfoVo.getDeliveryType());
        dhr.setDeliveryDate(deliveryInfoVo.getDeliveryDate());
        dhr.setPostingDate(deliveryInfoVo.getPostingDate());
        dhr.setBpCustomer(deliveryInfoVo.getBpCustomer());
        dhr.setBpVendor(deliveryInfoVo.getBpVendor());
        dhr.setWarehouseCode(deliveryInfoVo.getWarehouseCode());
        dhr.setCarrierCode(deliveryInfoVo.getCarrierCode());
        dhr.setTrackingNumber(deliveryInfoVo.getTrackingNumber());
        dhr.setDeliveryNotes(deliveryInfoVo.getDeliveryNotes());
        Date date = new Date();
        dhr.setGmtCreate(date);
        dhr.setGmtModified(date);
        dhr.setCreateBy(String.valueOf(1));
        dhr.setModifiedBy(String.valueOf(1));
        dhr.setIsDeleted(0);
        try {
            deliveryHeaderMapper.updateById(dhr);
            for (int i = 0; i < deliveryInfoVo.getDeliveryItemList().size(); i++) {
                DeliveryItem x = deliveryInfoVo.getDeliveryItemList().get(i);
                DeliveryItem dItem = new DeliveryItem();
                dItem.setId(x.getId());
                dItem.setCompanyCode(deliveryInfoVo.getCompanyCode());
                dItem.setDeliveryNumber(deliveryInfoVo.getDeliveryNumber());
                dItem.setDeliveryItem(x.getDeliveryItem());
                dItem.setReferenceDoc(x.getReferenceDoc());
                dItem.setReferenceDocItem(x.getReferenceDocItem());
                dItem.setSkuNumber(x.getSkuNumber());
                SkuMaster sku = skuService.getSku(x.getSkuNumber(), null, deliveryInfoVo.getCompanyCode());
                if (sku == null) {
                    throw new RuntimeException("skuNumber:[" + x.getSkuNumber() + "]查询sku信息失败");
                }
                if (StringUtils.isNotBlank((x.getKittingSku()))) {
                    dItem.setKittingSku(x.getKittingSku());
                    dItem.setKittingDeliveryQty(x.getKittingDeliveryQty());
                }
                if (StringUtils.isNotBlank(x.getBpSkuNumber())) {
                    dItem.setBpSkuNumber(x.getBpSkuNumber());
                }
                dItem.setWarehouseCode(x.getWarehouseCode());
                dItem.setCompleteDelivery(0);
                dItem.setDeliveryQty(x.getDeliveryQty());
                dItem.setDeliveredQty(x.getDeliveredQty());
                dItem.setBasicUom(x.getBasicUom());
                dItem.setIsDeleted(0);
                deliveryItemMapper.updateById(dItem);
                //根据companyCode  type  subType  deliveryNumber   更新地址信息
                AddressSaveVo shipAddress = deliveryInfoVo.getShippingAddress();
                shipAddress.setCompanyCode(deliveryInfoVo.getCompanyCode());
                shipAddress.setType(TYPE_ADDRESS_SODN);
                shipAddress.setSubType(SUBTYPE_ADDRESS_SODN_SHIP);
                shipAddress.setKey(dhr.getDeliveryNumber());
                addressService.modifyAddress(shipAddress);
                if (BigDecimal.ZERO.equals(x.getDeliveredQty())) {
                    continue;
                } else {
                    CreateMaterialDocSkuVo cmdSkuVo = new CreateMaterialDocSkuVo();
                    cmdSkuVo.setSkuNumber(x.getSkuNumber());
                    cmdSkuVo.setSkuQty(x.getDeliveredQty());
                    cmdSkuVo.setBasicUom(x.getBasicUom());
                    //根据skuNumber 及 soNumber查询 soItem  获取单价 及货币编号
                    //根据以下条件查询so_item中当前sku的sales qty
                    QueryWrapper<SoItem> sqw = new QueryWrapper<>();
                    sqw.eq("so_number", x.getReferenceDoc());
                    if (StringUtils.isNotBlank(x.getKittingSku())) {
                        sqw.eq("sku_number", x.getKittingSku());
                    } else {
                        sqw.eq("sku_number", x.getSkuNumber());
                    }
                    sqw.eq("so_item", x.getReferenceDocItem());
                    sqw.eq("company_code", x.getCompanyCode());
                    sqw.eq("is_deleted", 0);
                    SoItem soItem = soItemMapper.selectOne(sqw);
                    cmdSkuVo.setItemAmount(x.getAvagUnitPrice());
                    cmdSkuVo.setCurrencyCode(soItem.getCurrencyCode());
                    cmdSkuVo.setReferenceNumber(deliveryInfoVo.getDeliveryNumber());
                    cmdSkuVo.setReferenceItem(x.getDeliveryItem());
                    cmdSkuVoList.add(cmdSkuVo);
                }
            }

            if (cmdSkuVoList.size() > 0) {
                // 扣减库存
                if (!isServiceDelivery) {
                    CreateMaterialDocVo cmdVo = new CreateMaterialDocVo();
                    cmdVo.setCompanyCode(deliveryInfoVo.getCompanyCode());
                    cmdVo.setPostingDate(deliveryInfoVo.getPostingDate());
                    cmdVo.setMovementType(ModuleConstant.MOVEMENT_TYPE.SO_delivery);
                    cmdVo.setWarehouseCode(deliveryInfoVo.getWarehouseCode());
                    cmdVo.setStockStatus(ModuleConstant.STOCK_STATUS.NORMAL);
                    cmdVo.setReferenceType("DN");
                    cmdVo.setCreateMaterialDocSkuVoList(cmdSkuVoList);
                    materialDocService.add(cmdVo);
                }
            }

            //当创建完物料编码后 通过delivery_number更新delivery_header 及 delivery_item中的complete_delivery 为 1-已完成交付
            DeliveryHeader dh = new DeliveryHeader();
            dh.setId(deliveryInfoVo.getId());
            dh.setCompleteDelivery(1);
            deliveryHeaderMapper.updateById(dh);
            //修改明细
            deliveryInfoVo.getDeliveryItemList().forEach(x -> {
                DeliveryItem dhi = new DeliveryItem();
                dhi.setId(x.getId());
                dhi.setCompleteDelivery(1);
                deliveryItemMapper.updateById(dhi);
            });
            //修改完Delivery相关状态后,需要修改soHeader中的订单状态
            //从unfullfilled 变更为 partially fullfilled 或 fully fullfilled
            //查询so_item 得到所有的sku 及 sku的可销售数量
            //判断这些sku是否都已经发运完成  还是 部分发运
            List<SoItem> soItems = getSoItems(deliveryInfoVo.getCompanyCode(), deliveryInfoVo.getSoNumber());
            boolean c = false;
            for (int i = 0; i < soItems.size(); i++) {
                SoItem x = soItems.get(i);
                if (1 == x.getIsKitting()) {
                    for (int i1 = 0; i1 < x.getKittingItems().size(); i1++) {
                        SkuKitting skuKitting = x.getKittingItems().get(i1);
                        BigDecimal sum = x.getSalesQty().multiply(skuKitting.getComponentQty());
                        //根据查询到的sku 去 delivery表中查询发运数量
                        QueryWrapper<DeliveryItem> dqw = new QueryWrapper<>();
                        dqw.select("sum(delivered_qty) as delivered_qty");
                        dqw.eq("company_code", deliveryInfoVo.getCompanyCode());
                        dqw.eq("reference_doc", deliveryInfoVo.getSoNumber());
                        dqw.eq("sku_number", skuKitting.getComponentSku());
                        dqw.eq("reference_doc_item", x.getSoItem());
                        dqw.eq("is_deleted", 0);
                        DeliveryItem deliveryItem1 = deliveryItemMapper.selectOne(dqw);
                        BigDecimal bc = BigDecimal.ZERO;
                        if (deliveryItem1 != null) {
                            bc = bc.add(deliveryItem1.getDeliveredQty());
                        }
                        if (sum.compareTo(bc) == 0) {
                            c = true;
                        } else {
                            c = false;
                            break;
                        }
                    }
                } else {
                    //根据查询到的sku 去 delivery表中查询发运数量
                    QueryWrapper<DeliveryItem> dqw = new QueryWrapper<>();
                    dqw.select("sum(delivered_qty) as delivered_qty");
                    dqw.eq("company_code", deliveryInfoVo.getCompanyCode());
                    dqw.eq("reference_doc", deliveryInfoVo.getSoNumber());
                    dqw.eq("sku_number", x.getSkuNumber());
                    dqw.eq("reference_doc_item", x.getSoItem());
                    dqw.eq("is_deleted", 0);
                    DeliveryItem deliveryItem1 = deliveryItemMapper.selectOne(dqw);
                    BigDecimal bc = BigDecimal.ZERO;
                    if (deliveryItem1 != null) {
                        bc = bc.add(deliveryItem1.getDeliveredQty());
                    }
                    if (x.getBasicQty().compareTo(bc) == 0) {
                        c = true;
                    } else {
                        c = false;
                        break;
                    }
                }
            }
            SoHeader soHeader = new SoHeader();
            if (!c) {
                soHeader.setDeliveryStatus(ModuleConstant.SOPO_DELIVERY_STATUS.PARTIALLY_FULLFILLED);
            } else {
                soHeader.setDeliveryStatus(ModuleConstant.SOPO_DELIVERY_STATUS.FULLFILLED);
            }
            QueryWrapper<SoHeader> shqw = new QueryWrapper<>();
            shqw.eq("so_number", deliveryInfoVo.getSoNumber());
            shqw.eq("company_code", deliveryInfoVo.getCompanyCode());
            shqw.eq("is_deleted", 0);
            soHeaderMapper.update(soHeader, shqw);
        } catch (Exception e) {
            log.error("modify delivery failed", e);
            throw new RuntimeException(e);
        }
        return dhr;
    }

    /**
     * Packaging Delivery Header Params
     *
     * @param deliveryInfoVo
     * @return
     */
    @Transactional
    public DeliveryHeader deliveryPoHeaderInfoPacking(Boolean b, DeliveryInfoVo deliveryInfoVo) {
        DeliveryHeader dh = new DeliveryHeader();
        //参数封装
        //生成Delivery_Number
        String poNumber = deliveryInfoVo.getPoNumber();
        String companyCode = deliveryInfoVo.getCompanyCode();
        QueryWrapper<DeliveryItem> deliveryItemQueryWrapper = new QueryWrapper<>();
        deliveryItemQueryWrapper.eq("reference_doc", poNumber);
        deliveryItemQueryWrapper.eq("company_code", companyCode);
        deliveryItemQueryWrapper.orderByDesc("id");
        deliveryItemQueryWrapper.last("limit 1");
        DeliveryItem deliveryItem = deliveryItemMapper.selectOne(deliveryItemQueryWrapper);
        String deliveryNumber = null;
        if (deliveryItem != null) {
            deliveryNumber = BigDecimal.ONE.add(new BigDecimal(deliveryItem.getDeliveryNumber())).toString();
        } else {
            deliveryNumber = "9" + poNumber + "001";
        }
        dh.setCompanyCode(deliveryInfoVo.getCompanyCode());
        dh.setDeliveryNumber(deliveryNumber);
        dh.setDeliveryType(deliveryInfoVo.getDeliveryType());
        dh.setCompleteDelivery(0);
        dh.setDeliveryDate(deliveryInfoVo.getDeliveryDate());
        if (!b) {
            dh.setPostingDate(deliveryInfoVo.getPostingDate());
        }
        dh.setBpCustomer(deliveryInfoVo.getBpCustomer());
        dh.setBpVendor(deliveryInfoVo.getBpVendor());
        dh.setWarehouseCode(deliveryInfoVo.getWarehouseCode());
        dh.setCarrierCode(deliveryInfoVo.getCarrierCode());
        dh.setTrackingNumber(deliveryInfoVo.getTrackingNumber());
        dh.setDeliveryNotes(deliveryInfoVo.getDeliveryNotes());
        Date date = new Date();
        dh.setGmtCreate(date);
        dh.setGmtModified(date);
        dh.setCreateBy(String.valueOf(1));
        dh.setModifiedBy(String.valueOf(1));
        dh.setIsDeleted(0);
        return dh;
    }

    /**
     * 创建po的delivery
     *
     * @param deliveryInfoVo
     * @return
     */
    @Transactional
    public Object createPoDelivery(DeliveryInfoVo deliveryInfoVo) {
        boolean isBlankDelivery = "".equals(deliveryInfoVo.getPostingDate()) || null == deliveryInfoVo.getPostingDate();
        // 如果收货时间为空  说明该订单的item没有填写发货数量   也就是表示这单不算发运 也就可以不用处理库存

        // 是否是service so
        boolean isServiceDelivery = ModuleConstant.DELIVERY_TYPE.SERVICE_PO.equals(deliveryInfoVo.getDeliveryType());

        // 如果发运类型为 SEASN,表示service类型的发运，不扣库存，不检验库存

        //用于存储该deliveryItem中每一条的wareHouse,判断wareHouse是否一致
        Set<String> wareHouseSet = new HashSet<>();
        for (int i = 0; i < deliveryInfoVo.getDeliveryItemList().size(); i++) {
            DeliveryItem x = deliveryInfoVo.getDeliveryItemList().get(i);
            //如果收货时间不为空,则说明用户填写了收货数量  需要验证这些sku的收货数量  验证订单销售数量校验
            if (!isBlankDelivery) {  //非空收
                if (x.getDeliveredQty().compareTo(x.getDeliveryQty()) == 1) {
                    // 用户输入数量 大于 订单剩余可采购
                    throw new ServiceException("Creation not allowed: sku[" + x.getSkuNumber() + "] greater than the order purchase quantity");
                }
            }
            wareHouseSet.add(x.getWarehouseCode());
        }
        //验证wareHouse是否一致
        if (wareHouseSet.size() > 1) {
            throw new ServiceException("Creation not allowed: Warehouse codes must be consistent");
        }
        DeliveryHeader deliveryHeader = deliveryPoHeaderInfoPacking(isBlankDelivery, deliveryInfoVo);
        try {
            deliveryHeaderMapper.insert(deliveryHeader);
            //封装Delivery_item所需参数
            List<DeliveryItem> deliveryItems = new ArrayList<>();
            //生成物料凭证所需参数
            List<CreateMaterialDocSkuVo> cmdSkuVoList = new ArrayList<>();

            List<DeliveryItem> deliveryListParams = deliveryInfoVo.getDeliveryItemList();
            for (int i = 0; i < deliveryListParams.size(); i++) {
                DeliveryItem deliveryItem = deliveryListParams.get(i);
                DeliveryItem dItem = new DeliveryItem();
                dItem.setCompanyCode(deliveryHeader.getCompanyCode());
                dItem.setDeliveryNumber(deliveryHeader.getDeliveryNumber());
                dItem.setDeliveryItem(deliveryItem.getDeliveryItem());
                dItem.setReferenceDoc(deliveryItem.getReferenceDoc());
                dItem.setReferenceDocItem(deliveryItem.getReferenceDocItem());
                dItem.setSkuNumber(deliveryItem.getSkuNumber());
                SkuMaster sku = skuService.getSku(deliveryItem.getSkuNumber(), null, deliveryHeader.getCompanyCode());
                if (sku == null) {
                    throw new RuntimeException("skuNumber:[" + deliveryItem.getSkuNumber() + "]查询sku信息失败");
                }
                if (sku.getKittingItems() != null) {
                    dItem.setKittingSku(sku.getKittingItems().get(0).getKittingSku());
                }
                if (StringUtils.isNotBlank(deliveryItem.getBpSkuNumber())) {
                    dItem.setBpSkuNumber(deliveryItem.getBpSkuNumber());
                }
//                if (sku.getData().getBPDetails().size() > 0) {
//                    dItem.setBpSkuNumber(sku.getData().getBPDetails().get(0).getBpNumber());
//                }
                dItem.setWarehouseCode(deliveryItem.getWarehouseCode());
                dItem.setCompleteDelivery(0);
                dItem.setDeliveryQty(deliveryItem.getDeliveryQty());
                dItem.setDeliveredQty(deliveryItem.getDeliveredQty());
                dItem.setBasicUom(deliveryItem.getBasicUom());
                dItem.setIsDeleted(0);
                deliveryItems.add(dItem);

                if (BigDecimal.ZERO.equals(deliveryItem.getDeliveredQty())) {
                    continue;
                } else {
                    //非空收货
                    if (!isBlankDelivery) {
                        CreateMaterialDocSkuVo cmdSkuVo = new CreateMaterialDocSkuVo();
                        cmdSkuVo.setSkuNumber(deliveryItem.getSkuNumber());
                        cmdSkuVo.setSkuQty(deliveryItem.getDeliveredQty());
                        cmdSkuVo.setBasicUom(deliveryItem.getBasicUom());
                        //根据skuNumber 及 soNumber查询 soItem  获取单价 及货币编号
                        //根据以下条件查询so_item中当前sku的sales qty
                        QueryWrapper<PoItem> sqw = new QueryWrapper<>();
                        sqw.eq("po_number", deliveryItem.getReferenceDoc());
                        sqw.eq("sku_number", deliveryItem.getSkuNumber());
                        sqw.eq("po_item", deliveryItem.getReferenceDocItem());
                        sqw.eq("company_code", deliveryItem.getCompanyCode());
                        sqw.eq("is_deleted", 0);
                        PoItem poItem = poItemMapper.selectOne(sqw);
                        BigDecimal purchaseBasicRate = sku.getPurchaseBasicRate();
                        if (null == purchaseBasicRate || BigDecimal.ZERO.equals(purchaseBasicRate)) {
                            purchaseBasicRate = BigDecimal.ONE;
                        }
                        cmdSkuVo.setItemAmount(poItem.getUnitPrice().divide(purchaseBasicRate, 2));
                        cmdSkuVo.setCurrencyCode(poItem.getCurrencyCode());
                        cmdSkuVo.setReferenceNumber(deliveryHeader.getDeliveryNumber());
                        cmdSkuVo.setReferenceItem(deliveryItem.getDeliveryItem());
                        cmdSkuVoList.add(cmdSkuVo);
                    }
                }

            }
            deliveryItemMapper.insertBatch(deliveryItems);

            AddressSaveVo shipAddress = deliveryInfoVo.getShippingAddress();
            shipAddress.setCompanyCode(deliveryInfoVo.getCompanyCode());
            shipAddress.setType(TYPE_ADDRESS_SODN);
            shipAddress.setSubType(SUBTYPE_ADDRESS_SODN_SHIP);
            shipAddress.setKey(deliveryHeader.getDeliveryNumber());
            addressService.save(shipAddress);
            // 非空发运，增加库存，更新状态
            if (!isBlankDelivery) {

                if (!isServiceDelivery) {
                    // 增加库存
                    if (cmdSkuVoList.size() > 0) {
                        CreateMaterialDocVo cmdVo = new CreateMaterialDocVo();
                        cmdVo.setCompanyCode(deliveryInfoVo.getCompanyCode());
                        cmdVo.setPostingDate(deliveryInfoVo.getPostingDate());
                        cmdVo.setMovementType(ModuleConstant.MOVEMENT_TYPE.PO_Receive);
                        cmdVo.setWarehouseCode(deliveryInfoVo.getWarehouseCode());
                        cmdVo.setStockStatus(ModuleConstant.STOCK_STATUS.NORMAL);
                        cmdVo.setReferenceType(ModuleConstant.REFERENCE_TYPE.ASN);
                        cmdVo.setCreateMaterialDocSkuVoList(cmdSkuVoList);
                        materialDocService.add(cmdVo);
                    }
                }

                //当创建完物料编码后 通过delivery_number更新delivery_header 及 delivery_item中的complete_delivery 为 1-已完成交付
                QueryWrapper<DeliveryHeader> qw = new QueryWrapper<>();
                qw.eq("delivery_number", deliveryHeader.getDeliveryNumber())
                        .eq("company_code",deliveryHeader.getCompanyCode())
                        .eq("is_deleted", 0);
                DeliveryHeader dh = new DeliveryHeader();
                dh.setCompleteDelivery(1);
                deliveryHeaderMapper.update(dh, qw);

                QueryWrapper<DeliveryItem> qw1 = new QueryWrapper<>();
                qw1.eq("delivery_number", deliveryHeader.getDeliveryNumber())
                        .eq("company_code",deliveryHeader.getCompanyCode())
                        .eq("is_deleted", 0);
                DeliveryItem dhi = new DeliveryItem();
                dhi.setCompleteDelivery(1);
                deliveryItemMapper.update(dhi, qw1);

                //修改完Delivery相关状态后,需要修改poHeader中的订单状态
                //从unfullfilled 变更为 partially fullfilled 或 fully fullfilled
                //查询po_item 得到所有的sku 及 sku的可收货数量
                //判断这些sku是否都已经收货完成  还是 部分说活
                QueryWrapper<PoItem> sqw = new QueryWrapper<>();
                sqw.eq("company_code", deliveryInfoVo.getCompanyCode());
                sqw.eq("po_number", deliveryInfoVo.getPoNumber());
                sqw.eq("is_deleted", 0);
                List<PoItem> poItems = poItemMapper.selectList(sqw);
                boolean c = false;
                for (int i = 0; i < poItems.size(); i++) {
                    PoItem x = poItems.get(i);
                    //根据查询到的sku 去 delivery表中查询发运数量
                    QueryWrapper<DeliveryItem> dqw = new QueryWrapper<>();
                    dqw.select("sum(delivered_qty) as delivered_qty");
                    dqw.eq("company_code", deliveryInfoVo.getCompanyCode());
                    dqw.eq("reference_doc", deliveryInfoVo.getPoNumber());
                    dqw.eq("sku_number", x.getSkuNumber());
                    dqw.eq("reference_doc_item", x.getPoItem());
                    dqw.eq("is_deleted", 0);
                    DeliveryItem deliveryItem1 = deliveryItemMapper.selectOne(dqw);
                    BigDecimal bc = BigDecimal.ZERO;
                    if (deliveryItem1 != null) {
                        bc = bc.add(deliveryItem1.getDeliveredQty());
                    }
                    if (x.getBasicQty().compareTo(bc) == 0) {
                        c = true;
                    } else {
                        c = false;
                        break;
                    }
                }
                PoHeader poHeader = new PoHeader();
                if (!c) {
                    poHeader.setDeliveryStatus(ModuleConstant.SOPO_DELIVERY_STATUS.PARTIALLY_FULLFILLED);
                } else {
                    poHeader.setDeliveryStatus(ModuleConstant.SOPO_DELIVERY_STATUS.FULLFILLED);
                }
                QueryWrapper<PoHeader> shqw = new QueryWrapper<>();
                shqw.eq("po_number", deliveryInfoVo.getPoNumber());
                shqw.eq("company_code", deliveryInfoVo.getCompanyCode());
                shqw.eq("is_deleted", 0);
                poHeaderMapper.update(poHeader, shqw);
            }
        } catch (Exception e) {
            log.error("create delivery failed", e);
            throw new RuntimeException(e);
        }
        return deliveryHeader;
    }

    @Transactional
    public Object modifyPoDelivery(DeliveryInfoVo deliveryInfoVo) {

        // 发运时间为空，说明是空发运，直接拒绝
        if ("".equals(deliveryInfoVo.getPostingDate()) || null == deliveryInfoVo.getPostingDate()) {
            throw new IllegalArgumentException("posting date is null");
        }

        boolean isServiceDelivery = ModuleConstant.DELIVERY_TYPE.SERVICE_PO.equals(deliveryInfoVo.getDeliveryType());

        //生成物料凭证所需参数
        List<CreateMaterialDocSkuVo> cmdSkuVoList = new ArrayList<>();
        //用于存储该deliveryItem中每一条的wareHouse,判断wareHouse是否一致
        Set<String> wareHouseSet = new HashSet<>();

        for (int i = 0; i < deliveryInfoVo.getDeliveryItemList().size(); i++) {
            DeliveryItem x = deliveryInfoVo.getDeliveryItemList().get(i);
//            //如果发货时间不为空,则说 需要验证这些sku的说活数明用户填写了发运数量 量 订单收货数量校验
//            //校验用户输入的数量是否大于订单可收货数量
            if (x.getDeliveredQty().compareTo(x.getDeliveryQty()) == 1) {
                // 用户输入数量 大于 订单剩余可采购
                throw new ServiceException("Creation not allowed: sku[" + x.getSkuNumber() + "] greater than the order purchase quantity");
            }
            wareHouseSet.add(x.getWarehouseCode());
        }
        //验证wareHouse是否一致
        if (wareHouseSet.size() > 1) {
            throw new ServiceException("Creation not allowed: Warehouse codes must be consistent");
        }
        DeliveryHeader dhr = new DeliveryHeader();
        dhr.setId(deliveryInfoVo.getId());
        dhr.setCompanyCode(deliveryInfoVo.getCompanyCode());
        dhr.setDeliveryNumber(deliveryInfoVo.getDeliveryNumber());
        dhr.setDeliveryType(deliveryInfoVo.getDeliveryType());
        dhr.setCompleteDelivery(0);
        dhr.setDeliveryDate(deliveryInfoVo.getDeliveryDate());
        dhr.setPostingDate(deliveryInfoVo.getPostingDate());
        dhr.setBpCustomer(deliveryInfoVo.getBpCustomer());
        dhr.setBpVendor(deliveryInfoVo.getBpVendor());
        dhr.setWarehouseCode(deliveryInfoVo.getWarehouseCode());
        dhr.setCarrierCode(deliveryInfoVo.getCarrierCode());
        dhr.setTrackingNumber(deliveryInfoVo.getTrackingNumber());
        dhr.setDeliveryNotes(deliveryInfoVo.getDeliveryNotes());
        Date date = new Date();
        dhr.setGmtCreate(date);
        dhr.setGmtModified(date);
        dhr.setCreateBy("1");
        dhr.setModifiedBy("1");
//        dhr.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
//        dhr.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
        dhr.setIsDeleted(0);
        try {
            deliveryHeaderMapper.updateById(dhr);
            for (int i = 0; i < deliveryInfoVo.getDeliveryItemList().size(); i++) {
                DeliveryItem x = deliveryInfoVo.getDeliveryItemList().get(i);
                DeliveryItem dItem = new DeliveryItem();
                dItem.setId(x.getId());
                dItem.setCompanyCode(deliveryInfoVo.getCompanyCode());
                dItem.setDeliveryNumber(deliveryInfoVo.getDeliveryNumber());
                dItem.setDeliveryItem(x.getDeliveryItem());
                dItem.setReferenceDoc(x.getReferenceDoc());
                dItem.setReferenceDocItem(x.getReferenceDocItem());
                dItem.setSkuNumber(x.getSkuNumber());
                SkuMaster sku = skuService.getSku(x.getSkuNumber(), null, deliveryInfoVo.getCompanyCode());
                if (sku == null) {
                    throw new RuntimeException("skuNumber:[" + x.getSkuNumber() + "]查询sku信息失败");
                }
                if (sku.getKittingItems() != null) {
                    dItem.setKittingSku(sku.getKittingItems().get(0).getKittingSku());
                }
                if (StringUtils.isNotBlank(x.getBpSkuNumber())) {
                    dItem.setBpSkuNumber(x.getBpSkuNumber());
                }
//                if (sku.getData().getBPDetails().size() > 0) {
//                    dItem.setBpSkuNumber(sku.getData().getBPDetails().get(0).getBpNumber());
//                }
                dItem.setWarehouseCode(x.getWarehouseCode());
                dItem.setCompleteDelivery(0);
                dItem.setDeliveryQty(x.getDeliveryQty());
                dItem.setDeliveredQty(x.getDeliveredQty());
                dItem.setBasicUom(x.getBasicUom());
                dItem.setIsDeleted(0);
                deliveryItemMapper.updateById(dItem);
                //根据companyCode  type  subType  deliveryNumber   更新地址信息
                AddressSaveVo shipAddress = deliveryInfoVo.getShippingAddress();
                shipAddress.setCompanyCode(deliveryInfoVo.getCompanyCode());
                shipAddress.setType(TYPE_ADDRESS_SODN);
                shipAddress.setSubType(SUBTYPE_ADDRESS_SODN_SHIP);
                shipAddress.setKey(dhr.getDeliveryNumber());
                addressService.modifyAddress(shipAddress);

                if (BigDecimal.ZERO.equals(x.getDeliveredQty())) {
                    continue;
                } else {
                    CreateMaterialDocSkuVo cmdSkuVo = new CreateMaterialDocSkuVo();
                    cmdSkuVo.setSkuNumber(x.getSkuNumber());
                    cmdSkuVo.setSkuQty(x.getDeliveredQty());
                    cmdSkuVo.setBasicUom(x.getBasicUom());
                    //根据skuNumber 及 poNumber查询 poItem  获取单价 及货币编号
                    //根据以下条件查询po_item中当前sku的purchase qty
                    QueryWrapper<PoItem> sqw = new QueryWrapper<>();
                    sqw.eq("po_number", x.getReferenceDoc());
                    sqw.eq("sku_number", x.getSkuNumber());
                    sqw.eq("po_item", x.getReferenceDocItem());
                    sqw.eq("company_code", x.getCompanyCode());
                    sqw.eq("is_deleted", 0);
                    PoItem poItem = poItemMapper.selectOne(sqw);
                    BigDecimal purchaseBasicRate = sku.getPurchaseBasicRate();
                    if (null == purchaseBasicRate || BigDecimal.ZERO.equals(purchaseBasicRate)) {
                        purchaseBasicRate = BigDecimal.ONE;
                    }
                    cmdSkuVo.setItemAmount(poItem.getUnitPrice().divide(purchaseBasicRate, 2));
                    cmdSkuVo.setCurrencyCode(poItem.getCurrencyCode());
                    cmdSkuVo.setReferenceNumber(deliveryInfoVo.getDeliveryNumber());
                    cmdSkuVo.setReferenceItem(x.getDeliveryItem());
                    cmdSkuVoList.add(cmdSkuVo);
                }
            }

            if (!isServiceDelivery) {
                if (cmdSkuVoList.size() > 0) {
                    CreateMaterialDocVo cmdVo = new CreateMaterialDocVo();
                    cmdVo.setCompanyCode(deliveryInfoVo.getCompanyCode());
                    cmdVo.setPostingDate(deliveryInfoVo.getPostingDate());
                    cmdVo.setMovementType(ModuleConstant.MOVEMENT_TYPE.PO_Receive);
                    cmdVo.setWarehouseCode(deliveryInfoVo.getWarehouseCode());
                    cmdVo.setStockStatus(ModuleConstant.STOCK_STATUS.NORMAL);
                    cmdVo.setReferenceType(ModuleConstant.REFERENCE_TYPE.ASN);
                    cmdVo.setCreateMaterialDocSkuVoList(cmdSkuVoList);
                    materialDocService.add(cmdVo);
                }
            }

            //当创建完物料编码后 通过delivery_number更新delivery_header 及 delivery_item中的complete_delivery 为 1-已完成交付
            QueryWrapper<DeliveryHeader> qw = new QueryWrapper<>();
            qw.eq("delivery_number", deliveryInfoVo.getDeliveryNumber())
                    .eq("company_code",deliveryInfoVo.getCompanyCode())
                    .eq("is_deleted", 0);
            DeliveryHeader dh = new DeliveryHeader();
            dh.setCompleteDelivery(1);
            deliveryHeaderMapper.update(dh, qw);
            QueryWrapper<DeliveryItem> qw1 = new QueryWrapper<>();
            qw1.eq("delivery_number", deliveryInfoVo.getDeliveryNumber())
                    .eq("company_code",deliveryInfoVo.getCompanyCode())
                    .eq("is_deleted", 0);
            DeliveryItem dhi = new DeliveryItem();
            dhi.setCompleteDelivery(1);
            deliveryItemMapper.update(dhi, qw1);
            //修改完Delivery相关状态后,需要修改poHeader中的订单状态
            //从unfullfilled 变更为 partially fullfilled 或 fully fullfilled
            //查询po_item 得到所有的sku 及 sku的可销售数量
            //判断这些sku是否都已经发运完成  还是 部分发运


            QueryWrapper<PoItem> sqw = new QueryWrapper<>();
            sqw.eq("company_code", deliveryInfoVo.getCompanyCode());
            sqw.eq("po_number", deliveryInfoVo.getPoNumber());
            sqw.eq("is_deleted", 0);
            List<PoItem> poItems = poItemMapper.selectList(sqw);
            boolean c = false;
            for (int i = 0; i < poItems.size(); i++) {
                PoItem x = poItems.get(i);
                //根据查询到的sku 去 delivery表中查询发运数量
                QueryWrapper<DeliveryItem> dqw = new QueryWrapper<>();
                dqw.select("sum(delivered_qty) as delivered_qty");
                dqw.eq("company_code", deliveryInfoVo.getCompanyCode());
                dqw.eq("reference_doc", deliveryInfoVo.getPoNumber());
                dqw.eq("sku_number", x.getSkuNumber());
                dqw.eq("reference_doc_item", x.getPoItem());
                dqw.eq("is_deleted", 0);
                DeliveryItem deliveryItem1 = deliveryItemMapper.selectOne(dqw);
                BigDecimal bc = BigDecimal.ZERO;
                if (deliveryItem1 != null) {
                    bc = bc.add(deliveryItem1.getDeliveredQty());
                }
                if (x.getBasicQty().compareTo(bc) == 0) {
                    c = true;
                } else {
                    c = false;
                    break;
                }
            }
            PoHeader poHeader = new PoHeader();
            if (!c) {
                poHeader.setDeliveryStatus(ModuleConstant.SOPO_DELIVERY_STATUS.PARTIALLY_FULLFILLED);
            } else {
                poHeader.setDeliveryStatus(ModuleConstant.SOPO_DELIVERY_STATUS.FULLFILLED);
            }
            QueryWrapper<PoHeader> shqw = new QueryWrapper<>();
            shqw.eq("po_number", deliveryInfoVo.getPoNumber());
            shqw.eq("company_code", deliveryInfoVo.getCompanyCode());
            shqw.eq("is_deleted", 0);
            poHeaderMapper.update(poHeader, shqw);
        } catch (Exception e) {
            log.error("modify delivery failed", e);
            throw new RuntimeException(e);
        }
        return dhr;
    }

    public List<PoDeliveryedListResp> selectPoDeliveryList(PoDeliveryedListQuery poDeliveryedListQuery) {
        if (StringUtils.isNotBlank(poDeliveryedListQuery.getShippingStaus())) {
            if (ModuleConstant.DELIVERY_SHIPPED_STATUS.UNFULFILED.equals(poDeliveryedListQuery.getShippingStaus())) {
                poDeliveryedListQuery.setCompleteDelivery(0);
            } else {
                poDeliveryedListQuery.setCompleteDelivery(1);
            }
        }
        return deliveryHeaderMapper.selectPoDeliveryList(poDeliveryedListQuery);
    }

    /**
     * by soNumber & company get so order header info
     *
     * @param poNumber
     * @param companyCode
     * @return
     */
    public PoOrderHeaderResp getPoOrderHeader(String poNumber, String companyCode) {
        //查询po_header中的信息
        PoOrderHeaderResp orderHeader = null;
        //查询shippingAddres
        PoShippingAddressVO poShippingAddressVO = new PoShippingAddressVO();
        poShippingAddressVO.setCompanyCode(companyCode);
        //根据poNumber查询Delivery中是否有发运信息
        List<DeliveryItem> deliveyItemsInfo = deliveryItemMapper.getDeliveyItemsInfo(poNumber, companyCode);
        if (deliveyItemsInfo.size() > 0) {
            //有发运记录
            //查询发运是否存在-->没有发运时间的单子
            List<String> s = deliveryItemMapper.getShippedDateIsNull(poNumber, companyCode,
                    null);
            if (s.size() <= 0) {
                //有发运记录  但是不存在发运时间为空的情况
                poShippingAddressVO.setDeliveryKey(deliveyItemsInfo.get(0).getDeliveryNumber());
                orderHeader = poHeaderMapper.getOrderHeader(poNumber, companyCode);
                //查询po_items中的信息
                QueryWrapper qw = new QueryWrapper();
                qw.eq("po_number", poNumber);
                qw.eq("company_code", companyCode);
                List<PoItem> list = poItemMapper.selectList(qw);
                List<PoOrderItems> dItemList = new ArrayList<>();
                list.forEach(poItem -> {
                    PoOrderItems dItem = new PoOrderItems();
                    //根据skuNumber获取sku信息
                    SkuMaster sku = skuService.getSku(poItem.getSkuNumber(),null, companyCode);
                    if (sku == null) {
                        throw new RuntimeException("skuNumber:[" + poItem.getSkuNumber() + "]查询sku信息失败");
                    }
                    dItem.setDeliveryItemId(poItem.getId().toString());
                    dItem.setSkuName(sku.getSkuName());
                    dItem.setSkuNumber(poItem.getSkuNumber());
                    if (sku.getKittingItems() != null) {
                        dItem.setKittingSku(sku.getKittingItems().get(0).getKittingSku());
                    }
                    dItem.setWarehouseCode(poItem.getWarehouseCode());
                    dItem.setPoSkuItem(poItem.getPoItem());
                    //根据skuNumber和poNumber和companyCode 查询deliveryItem 得到sku已经发运的数量
                    BigDecimal shippedQTY = deliveryItemMapper.getPoShippedQTY(poItem.getSkuNumber(), poNumber, companyCode, poItem.getPoItem());
                    dItem.setOpenQTY(poItem.getBasicQty().subtract(shippedQTY));
                    dItem.setShippedQTY(BigDecimal.ZERO);
                    dItemList.add(dItem);
                });
                orderHeader.setPoOrderItems(dItemList);
            } else {
                //有发运记录  并且存在有发运时间为空的情况
                orderHeader = poHeaderMapper.getOrderHeaders(poNumber, companyCode);
                poShippingAddressVO.setDeliveryKey(orderHeader.getDeliveryNumber());
                //获取发运Items
                QueryWrapper<DeliveryItem> qw = new QueryWrapper<>();
                qw.in("id", s);
                qw.eq("company_code", companyCode);
                List<DeliveryItem> list = deliveryItemMapper.selectList(qw);
                List<PoOrderItems> dItemList = new ArrayList<>();
                list.forEach(x -> {
                    PoOrderItems dItem = new PoOrderItems();
                    dItem.setDeliveryItemId(x.getId().toString());
                    //根据skuNumber获取sku信息
                    SkuMaster sku = skuService.getSku(x.getSkuNumber(), null, companyCode);
                    if (sku == null) {
                        throw new RuntimeException("skuNumber:[" + x.getSkuNumber() + "]查询sku信息失败");
                    }
                    dItem.setSkuName(sku.getSkuName());
                    dItem.setSkuNumber(x.getSkuNumber());
                    if (sku.getKittingItems() != null) {
                        dItem.setKittingSku(sku.getKittingItems().get(0).getKittingSku());
                    }
                    dItem.setWarehouseCode(x.getWarehouseCode());
                    dItem.setOpenQTY(x.getDeliveryQty());
                    dItem.setShippedQTY(BigDecimal.ZERO);
                    dItem.setPoSkuItem(x.getDeliveryItem());
                    dItemList.add(dItem);
                });
                orderHeader.setPoOrderItems(dItemList);
            }
        } else {
            //没有发运记录
            poShippingAddressVO.setPoKey(poNumber);
            orderHeader = poHeaderMapper.getOrderHeader(poNumber, companyCode);
            //查询po_items中的信息
            QueryWrapper qw = new QueryWrapper();
            qw.eq("po_number", poNumber);
            qw.eq("company_code", companyCode);
            List<PoItem> list = poItemMapper.selectList(qw);
            List<PoOrderItems> dItemList = new ArrayList<>();
            list.forEach(x -> {
                PoOrderItems dItem = new PoOrderItems();
                //根据skuNumber获取sku信息
                SkuMaster sku = skuService.getSku(x.getSkuNumber(), null, companyCode);
                if (sku == null) {
                    throw new RuntimeException("skuNumber:[" + x.getSkuNumber() + "]查询sku信息失败");
                }
                dItem.setSkuName(sku.getSkuName());
                dItem.setSkuNumber(x.getSkuNumber());
                if (sku.getKittingItems() != null) {
                    dItem.setKittingSku(sku.getKittingItems().get(0).getKittingSku());
                }
                dItem.setWarehouseCode(x.getWarehouseCode());
                dItem.setOpenQTY(x.getBasicQty());
                dItem.setShippedQTY(BigDecimal.ZERO);
                dItem.setPoSkuItem(x.getPoItem());
                dItemList.add(dItem);
            });
            orderHeader.setPoOrderItems(dItemList);
        }
        //根据bpCustomer查询bpName
        BusinessPartner bp = bpService.getBpNameByBpNumber(companyCode, orderHeader.getBusinessPartner());
        if (bp != null) {
            orderHeader.setBusinessName(bp.getBpName());
        }
        Address shippingAddress = addressService.getPoShippingAddress(poShippingAddressVO);
        if (shippingAddress != null) {
            orderHeader.setShippingAddress(shippingAddress.getAddress1());
        }
        return orderHeader;
    }

    /**
     * by poNumber get shipped info card
     *
     * @param poNumber
     * @return
     */
    public List<PoDeliveryShippedResp> poShipped(String poNumber, String companyCode) {
        List<PoDeliveryShippedResp> deliveryShippedResps = deliveryItemMapper.selectPoShippedHeader(poNumber, companyCode);
        if (deliveryShippedResps.size() > 0) {
            deliveryShippedResps.forEach(x -> {
                //根据deliveryNumber查询shippingAddress
                SoShippingAddressVO soShippingAddressVO = new SoShippingAddressVO();
                soShippingAddressVO.setDeliveryKey(x.getDeliveryNumber());
                soShippingAddressVO.setCompanyCode(companyCode);
                log.info("查询发运卡片信息,deliveryNumber:{}", x.getDeliveryNumber());
                Address shippingAddress = addressService.getShippingAddress(soShippingAddressVO);
                if (shippingAddress == null) {
                    throw new RuntimeException("deliveryNumber:[" + x.getDeliveryNumber() + "]查询发运卡片信息服务调用失败，获取发运卡片信息未成功");
                }
                if (shippingAddress != null) {
                    x.setShippingAddress(shippingAddress.getAddress1());
                }
                List<DeliveryItem> deliveryItemList = deliveryItemMapper.selectShippedItems(x.getDeliveryNumber(),companyCode);
                List<PoOrderItems> list = new ArrayList<>();
                if (deliveryItemList.size() > 0) {
                    deliveryItemList.forEach(y -> {
                        PoOrderItems orderItems = new PoOrderItems();
                        SkuMaster sku = skuService.getSku(y.getSkuNumber(), null, companyCode);
                        if (sku == null) {
                            throw new RuntimeException("skuNumber:[" + y.getSkuNumber() + "]查询sku信息失败");
                        }
                        orderItems.setSkuName(sku.getSkuName());
                        orderItems.setSkuNumber(y.getSkuNumber());
                        orderItems.setKittingSku(y.getKittingSku());
                        orderItems.setWarehouseCode(y.getWarehouseCode());
                        orderItems.setOpenQTY(y.getDeliveryQty());
                        orderItems.setShippedQTY(y.getDeliveredQty());
                        list.add(orderItems);
                    });
                }
                x.setPoOrderItems(list);
            });
        }
        return deliveryShippedResps;
    }


    /**
     * 查询当前so 发运了几次
     *
     * @param deliveryInfoVo
     * @return
     */
    private List<DeliveryItem> getShippedNumber(DeliveryInfoVo deliveryInfoVo) {
        //根据soNumber查询delivery_item关联delivery  查询发运了几次
        QueryWrapper<DeliveryItem> deliveryItemLambdaQueryWrapper = new QueryWrapper<>();
        deliveryItemLambdaQueryWrapper.select("DISTINCT delivery_number");
        deliveryItemLambdaQueryWrapper.eq("reference_doc", deliveryInfoVo.getSoNumber());
        deliveryItemLambdaQueryWrapper.eq("company_code", deliveryInfoVo.getCompanyCode());
        List<DeliveryItem> deliveryItemList = deliveryItemMapper.selectList(deliveryItemLambdaQueryWrapper);
        if (deliveryItemList.size() >= 999) {
            throw new RuntimeException("当前so已经完全发运,不允许修改");
        }
        return deliveryItemList;
    }


    /**
     * modify so shipped delivery
     *
     * @param deliveryInfoVo
     * @return
     */
    public DeliveryHeader modifyShippedDelivery(DeliveryInfoVo deliveryInfoVo) {
        //根据soNumber查询delivery_item关联delivery  查询发运了几次
        List<DeliveryItem> deliveryItemList = this.getShippedNumber(deliveryInfoVo);
        if (deliveryItemList.size() >= 9) {
            throw new RuntimeException("当前so已经完全发运,不允许修改");
        }

        //根据当前deliveryNumber查询delivery是否已经开票
        DeliveryHeader deliveryHeader = deliveryHeaderMapper.checkDeliveryComplateBill(deliveryInfoVo.getCompanyCode(), deliveryInfoVo.getDeliveryNumber());
        if (deliveryHeader == null) {
            throw new RuntimeException("当前delivery已经完成开票,不允许修改");
        }

        //生成物料凭证所需参数
        List<CreateMaterialDocSkuVo> cmdSkuVoList = new ArrayList<>();
        //用于存储该deliveryItem中每一条的wareHourse,判断wareHouse是否一致
        Set<String> wareHouseSet = new HashSet<>();

        Boolean isALikeSku = false;

        //存放相同sku
        Map<String, List<BigDecimal>> aLikeSku = new HashMap<>();

        for (int i = 0; i < deliveryInfoVo.getDeliveryItemList().size(); i++) {
            DeliveryItem x = deliveryInfoVo.getDeliveryItemList().get(i);
            //验证sku的发运数量  验证分为两块  ①库存校验  ②订单销售数量校验
            //验证sku库存是否充足
            CheckStockBalanceParamVo checkStockBalanceParamVo = new CheckStockBalanceParamVo();
            checkStockBalanceParamVo.setWarehouseCode(x.getWarehouseCode());
            checkStockBalanceParamVo.setCompanyCode(x.getCompanyCode());
            List<CheckStockBalanceParamSubVo> list = new ArrayList<>();
            if (aLikeSku.isEmpty()) {
                List<BigDecimal> lists = new ArrayList<>();
                lists.add(x.getDeliveredQty());
                aLikeSku.put(x.getSkuNumber(), lists);
            } else {
                if (aLikeSku.containsKey(x.getSkuNumber())) {
                    aLikeSku.get(x.getSkuNumber()).add(x.getDeliveryQty());
                    isALikeSku = true;
                } else {
                    List<BigDecimal> lists = new ArrayList<>();
                    lists.add(x.getDeliveredQty());
                    aLikeSku.put(x.getSkuNumber(), lists);
                }
            }
            CheckStockBalanceParamSubVo checkStockBalanceParamSubVo = new CheckStockBalanceParamSubVo();
            checkStockBalanceParamSubVo.setSkuNumber(x.getSkuNumber());
            checkStockBalanceParamSubVo.setUseQty(x.getDeliveredQty());
            list.add(checkStockBalanceParamSubVo);
            checkStockBalanceParamVo.setCheckStockBalanceSubVos(list);
            CheckStockBalanceResVo checkStockBalanceResVo = stockBalanceService.checkStock(checkStockBalanceParamVo);
            //如果校验失败 则报错提示
            if (!checkStockBalanceResVo.getCheckStockBalanceSubVos().get(0).isAdequate()) {
                throw new ServiceException("Modify not allowed: sku[" + x.getSkuNumber() + "] greater than inventory quantity");
            }
            if (StringUtils.isNotBlank(x.getKittingSku())) {
                x.setAvagUnitPrice(checkStockBalanceResVo.getCheckStockBalanceSubVos().get(0).getAveragePrice());
            }

            //开始校验用户输入的数量是否大于订单可销售数量
            //根据以下条件查询so_item中当前sku的sales qty
            List<SoItem> lists = getSoItems(deliveryInfoVo.getCompanyCode(), deliveryInfoVo.getSoNumber());
            lists.stream().forEach(listX -> {
                if (1 == listX.getIsKitting()) {
                    for (int i1 = 0; i1 < listX.getKittingItems().size(); i1++) {
                        SkuKitting kitItems = listX.getKittingItems().get(i1);
                        //已经在delivery_item发运过的数量
                        BigDecimal shippedQTY = deliveryItemMapper.getShippedQTYIsDelivery(kitItems.getComponentSku(), deliveryInfoVo.getSoNumber(), deliveryInfoVo.getCompanyCode(), deliveryInfoVo.getDeliveryNumber());
                        //可用的发运数量
                        BigDecimal subtract = listX.getSalesQty().multiply(kitItems.getComponentQty()).subtract(shippedQTY);
                        if (x.getSkuNumber().equals(kitItems.getComponentSku())) {
                            if (x.getDeliveredQty().compareTo(subtract) > 0) {
                                throw new ServiceException("Modify not allowed: sku[" + x.getSkuNumber() + "] greater than the order sales quantity");
                            }
                        }
                    }
                } else {
                    //根据skuNumber和soNumber和companyCode 查询deliveryItem 得到sku已经发运的数量
                    BigDecimal shippedQTY = deliveryItemMapper.getShippedQTYIsDelivery(x.getSkuNumber(), deliveryInfoVo.getSoNumber(), deliveryInfoVo.getCompanyCode(), deliveryInfoVo.getDeliveryNumber());
                    //可用的发运数量
                    BigDecimal subtract = listX.getBasicQty().subtract(shippedQTY);
                    if (x.getSkuNumber().equals(listX.getSkuNumber())) {
                        if (x.getDeliveredQty().compareTo(subtract) > 0) {
                            throw new ServiceException("Modify not allowed: sku[" + x.getSkuNumber() + "] greater than the order sales quantity");
                        }
                    }
                }
            });
            wareHouseSet.add(x.getWarehouseCode());
        }

        //验证wareHouse是否一致
        if (wareHouseSet.size() > 1) {
            throw new ServiceException("Creation not allowed: Warehouse codes must be consistent");
        }

        //存在相同sku的情况   针对相同的sku 判断库存是否充足
        if (isALikeSku) {
            for (String s : aLikeSku.keySet()) {
                List<BigDecimal> bigDecimals = aLikeSku.get(s);
                if (bigDecimals.size() > 1) {
                    CheckStockBalanceParamVo checkStockBalanceParamVo = new CheckStockBalanceParamVo();
                    checkStockBalanceParamVo.setWarehouseCode(deliveryInfoVo.getWarehouseCode());
                    checkStockBalanceParamVo.setCompanyCode(deliveryInfoVo.getCompanyCode());
                    List<CheckStockBalanceParamSubVo> list = new ArrayList<>();
                    BigDecimal b = BigDecimal.ZERO;
                    for (int i = 0; i < bigDecimals.size(); i++) {
                        log.info(">>>so delivery相同sku ,skuNumber:{},第：{}个数量为：{}", s, i + 1, bigDecimals.get(i));
                        BigDecimal bigDecimal = bigDecimals.get(i);
                        b = b.add(bigDecimal);
                        log.info(">>>so delivery相同sku ,skuNumber:{},第：{}个总数量为：{}", s, i + 1, b);
                    }
                    CheckStockBalanceParamSubVo checkStockBalanceParamSubVo = new CheckStockBalanceParamSubVo();
                    checkStockBalanceParamSubVo.setSkuNumber(s);
                    checkStockBalanceParamSubVo.setUseQty(b);
                    list.add(checkStockBalanceParamSubVo);
                    checkStockBalanceParamVo.setCheckStockBalanceSubVos(list);
                    CheckStockBalanceResVo checkStockBalanceResVo = stockBalanceService.checkStock(checkStockBalanceParamVo);
                    //service so 不校验库存
                    if (!checkStockBalanceResVo.getCheckStockBalanceSubVos().get(0).isAdequate()) {
                        throw new ServiceException("Creation not allowed: sku[" + s + "] greater than inventory quantity");
                    }
                }
            }
        }

        //以上验证全部通过后 , 进行原有物料凭证reverse操作
        ReversedMaterialDocVO reversedMaterialDocVO = new ReversedMaterialDocVO();
        reversedMaterialDocVO.setCompanyCode(deliveryInfoVo.getCompanyCode());
        reversedMaterialDocVO.setReverseDate(new Date());
        //根据delivery和skuNumber 查询物料凭证表   得到物料凭证信息
        List<ReversedMaterialDocItemVo> reversedMaterialDocItemVoList = new ArrayList<>();
        deliveryInfoVo.getDeliveryItemList().forEach(deliveryItem -> {
            ReversedMaterialDocItemVo reversedMaterialDocItemVo = new ReversedMaterialDocItemVo();
            LambdaQueryWrapper<MaterialDoc> materialDocQueryWrapper = new LambdaQueryWrapper<MaterialDoc>()
                    .and(moc -> moc.eq(MaterialDoc::getCompanyCode, deliveryInfoVo.getCompanyCode()))
                    .and(moc -> moc.eq(MaterialDoc::getSkuNumber, deliveryItem.getSkuNumber()))
                    .and(moc -> moc.eq(MaterialDoc::getReferenceNumber, deliveryInfoVo.getDeliveryNumber()))
                    .and(moc -> moc.eq(MaterialDoc::getReferenceItem, deliveryItem.getDeliveryItem()))
                    .and(moc -> moc.eq(MaterialDoc::getMovementType, ModuleConstant.MOVEMENT_TYPE.SO_delivery));
            MaterialDoc materialDoc = materialDocMapper.selectOne(materialDocQueryWrapper);
            reversedMaterialDocVO.setDocNumber(materialDoc.getDocNumber());
            reversedMaterialDocItemVo.setMaterialDocId(materialDoc.getId());
            reversedMaterialDocItemVoList.add(reversedMaterialDocItemVo);
        });
        reversedMaterialDocVO.setReversedMaterialDocItemVos(reversedMaterialDocItemVoList);
        materialDocService.reverseMaterialDoc(reversedMaterialDocVO);

        //开始更新delivery操作
        DeliveryHeader dhr = new DeliveryHeader();
        dhr.setId(deliveryInfoVo.getId());
        dhr.setCompanyCode(deliveryInfoVo.getCompanyCode());
        dhr.setDeliveryNumber(deliveryInfoVo.getDeliveryNumber());
        dhr.setDeliveryType("DN");
        dhr.setDeliveryDate(deliveryInfoVo.getDeliveryDate());
        dhr.setPostingDate(deliveryInfoVo.getPostingDate());
        dhr.setBpCustomer(deliveryInfoVo.getBpCustomer());
        dhr.setBpVendor(deliveryInfoVo.getBpVendor());
        dhr.setWarehouseCode(deliveryInfoVo.getWarehouseCode());
        dhr.setCarrierCode(deliveryInfoVo.getCarrierCode());
        dhr.setTrackingNumber(deliveryInfoVo.getTrackingNumber());
        dhr.setDeliveryNotes(deliveryInfoVo.getDeliveryNotes());
        Date date = new Date();
        dhr.setGmtCreate(date);
        dhr.setGmtModified(date);
        dhr.setCreateBy("1");
        dhr.setModifiedBy("1");
//        dhr.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
//        dhr.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
        dhr.setIsDeleted(0);
        try {
            deliveryHeaderMapper.updateById(dhr);
            for (int i = 0; i < deliveryInfoVo.getDeliveryItemList().size(); i++) {
                DeliveryItem x = deliveryInfoVo.getDeliveryItemList().get(i);
                DeliveryItem dItem = new DeliveryItem();
                dItem.setId(x.getId());
                dItem.setCompanyCode(deliveryInfoVo.getCompanyCode());
                dItem.setDeliveryNumber(deliveryInfoVo.getDeliveryNumber());
                dItem.setDeliveryItem(x.getDeliveryItem());
                dItem.setReferenceDoc(x.getReferenceDoc());
                dItem.setReferenceDocItem(x.getReferenceDocItem());
                dItem.setSkuNumber(x.getSkuNumber());
                SkuMaster sku = skuService.getSku(x.getSkuNumber(), null, deliveryInfoVo.getCompanyCode());
                if (sku == null) {
                    throw new RuntimeException("skuNumber:[" + x.getSkuNumber() + "]查询sku信息失败");
                }
                if (sku.getKittingItems() != null) {
                    dItem.setKittingSku(x.getKittingSku());
                }
                if (sku.getBPDetails().size() > 0) {
                    dItem.setBpSkuNumber(sku.getBPDetails().get(0).getBpNumber());
                }
                dItem.setWarehouseCode(x.getWarehouseCode());
                dItem.setCompleteDelivery(0);
                dItem.setDeliveryQty(x.getDeliveryQty());
                dItem.setDeliveredQty(x.getDeliveredQty());
                dItem.setBasicUom(x.getBasicUom());
                dItem.setIsDeleted(0);
                deliveryItemMapper.updateById(dItem);
                //根据companyCode  type  subType  deliveryNumber   更新地址信息
                AddressSaveVo shipAddress = deliveryInfoVo.getShippingAddress();
                shipAddress.setCompanyCode(deliveryInfoVo.getCompanyCode());
                shipAddress.setType(TYPE_ADDRESS_SODN);
                shipAddress.setSubType(SUBTYPE_ADDRESS_SODN_SHIP);
                shipAddress.setKey(dhr.getDeliveryNumber());
                addressService.modifyAddress(shipAddress);

                CreateMaterialDocSkuVo cmdSkuVo = new CreateMaterialDocSkuVo();
                cmdSkuVo.setSkuNumber(x.getSkuNumber());
                cmdSkuVo.setSkuQty(x.getDeliveredQty());
                cmdSkuVo.setBasicUom(x.getBasicUom());
                //根据skuNumber 及 soNumber查询 soItem  获取单价 及货币编号
                //根据以下条件查询so_item中当前sku的sales qty
                QueryWrapper<SoItem> sqw = new QueryWrapper<>();
                sqw.eq("so_number", x.getReferenceDoc());
                if (StringUtils.isNotBlank(x.getKittingSku())) {
                    sqw.eq("sku_number", x.getKittingSku());
                } else {
                    sqw.eq("sku_number", x.getSkuNumber());
                }
                sqw.eq("so_item", x.getReferenceDocItem());
                sqw.eq("company_code", x.getCompanyCode());
                sqw.eq("is_deleted", 0);
                SoItem soItem = soItemMapper.selectOne(sqw);
                if (StringUtils.isNotBlank(x.getKittingSku())) {
                    cmdSkuVo.setItemAmount(x.getAvagUnitPrice());
                } else {
                    cmdSkuVo.setItemAmount(soItem.getUnitPrice());
                }
                cmdSkuVo.setCurrencyCode(soItem.getCurrencyCode());
                cmdSkuVo.setReferenceNumber(deliveryInfoVo.getDeliveryNumber());
                cmdSkuVo.setReferenceItem(x.getDeliveryItem());
                cmdSkuVoList.add(cmdSkuVo);
            }
            CreateMaterialDocVo cmdVo = new CreateMaterialDocVo();
            cmdVo.setCompanyCode(deliveryInfoVo.getCompanyCode());
            cmdVo.setPostingDate(deliveryInfoVo.getPostingDate());
            cmdVo.setMovementType(ModuleConstant.MOVEMENT_TYPE.SO_delivery);
            cmdVo.setWarehouseCode(deliveryInfoVo.getWarehouseCode());
            cmdVo.setStockStatus(ModuleConstant.STOCK_STATUS.NORMAL);
            cmdVo.setReferenceType("DN");
            cmdVo.setCreateMaterialDocSkuVoList(cmdSkuVoList);
            materialDocService.add(cmdVo);
            //当创建完物料编码后 通过delivery_number更新delivery_header 及 delivery_item中的complete_delivery 为 1-已完成交付
            DeliveryHeader dh = new DeliveryHeader();
            dh.setId(deliveryInfoVo.getId());
            dh.setCompleteDelivery(1);
            deliveryHeaderMapper.updateById(dh);
            //修改明细
            deliveryInfoVo.getDeliveryItemList().forEach(x -> {
                DeliveryItem dhi = new DeliveryItem();
                dhi.setId(x.getId());
                dhi.setCompleteDelivery(1);
                deliveryItemMapper.updateById(dhi);
            });
            //修改完Delivery相关状态后,需要修改soHeader中的订单状态
            //从unfullfilled 变更为 partially fullfilled 或 fully fullfilled
            //查询so_item 得到所有的sku 及 sku的可销售数量
            //判断这些sku是否都已经发运完成  还是 部分发运
            List<SoItem> soItems = getSoItems(deliveryInfoVo.getCompanyCode(), deliveryInfoVo.getSoNumber());
            boolean c = false;
            for (int i = 0; i < soItems.size(); i++) {
                SoItem x = soItems.get(i);
                if (1 == x.getIsKitting()) {
                    for (int i1 = 0; i1 < x.getKittingItems().size(); i1++) {
                        SkuKitting skuKitting = x.getKittingItems().get(i1);
                        BigDecimal sum = x.getSalesQty().multiply(skuKitting.getComponentQty());
                        //根据查询到的sku 去 delivery表中查询发运数量
                        QueryWrapper<DeliveryItem> dqw = new QueryWrapper<>();
                        dqw.select("sum(delivered_qty) as delivered_qty");
                        dqw.eq("company_code", deliveryInfoVo.getCompanyCode());
                        dqw.eq("reference_doc", deliveryInfoVo.getSoNumber());
                        dqw.eq("sku_number", skuKitting.getComponentSku());
                        dqw.eq("reference_doc_item", x.getSoItem());
                        dqw.eq("is_deleted", 0);
                        DeliveryItem deliveryItem1 = deliveryItemMapper.selectOne(dqw);
                        BigDecimal bc = BigDecimal.ZERO;
                        if (deliveryItem1 != null) {
                            bc = bc.add(deliveryItem1.getDeliveredQty());
                        }
                        if (sum.compareTo(bc) == 0) {
                            c = true;
                        } else {
                            c = false;
                            break;
                        }
                    }
                } else {
                    //根据查询到的sku 去 delivery表中查询发运数量
                    QueryWrapper<DeliveryItem> dqw = new QueryWrapper<>();
                    dqw.select("sum(delivered_qty) as delivered_qty");
                    dqw.eq("company_code", deliveryInfoVo.getCompanyCode());
                    dqw.eq("reference_doc", deliveryInfoVo.getSoNumber());
                    dqw.eq("sku_number", x.getSkuNumber());
                    dqw.eq("reference_doc_item", x.getSoItem());
                    dqw.eq("is_deleted", 0);
                    DeliveryItem deliveryItem1 = deliveryItemMapper.selectOne(dqw);
                    BigDecimal bc = BigDecimal.ZERO;
                    if (deliveryItem1 != null) {
                        bc = bc.add(deliveryItem1.getDeliveredQty());
                    }
                    if (x.getBasicQty().compareTo(bc) == 0) {
                        c = true;
                    } else {
                        c = false;
                        break;
                    }
                }
            }
            SoHeader soHeader = new SoHeader();
            if (!c) {
                soHeader.setDeliveryStatus(ModuleConstant.SOPO_DELIVERY_STATUS.PARTIALLY_FULLFILLED);
            } else {
                soHeader.setDeliveryStatus(ModuleConstant.SOPO_DELIVERY_STATUS.FULLFILLED);
            }
            QueryWrapper<SoHeader> shqw = new QueryWrapper<>();
            shqw.eq("so_number", deliveryInfoVo.getSoNumber());
            shqw.eq("company_code", deliveryInfoVo.getCompanyCode());
            shqw.eq("is_deleted", 0);
            soHeaderMapper.update(soHeader, shqw);
        } catch (Exception e) {
            log.error("modify shipped delivery failed", e);
            throw new RuntimeException(e);
        }
        return dhr;
    }

    /**
     * 查询未开票的发运记录
     *
     * @param soNumber
     * @param companyCode
     * @return
     */
    public List<DeliveryShippedResp> selectUnInvoiceList(String soNumber, String companyCode) {
        List<DeliveryShippedResp> deliveryShippedResps = new ArrayList<>();
//        //查询soHeader的orderType
//        LambdaQueryWrapper<SoHeader> soQueryWrapper = new LambdaQueryWrapper<SoHeader>()
//          .and(soHeader -> soHeader.eq(SoHeader::getCompanyCode, companyCode))
//          .and(soHeader -> soHeader.eq(SoHeader::getSoNumber, soNumber));
//        SoHeader soInfo = soHeaderMapper.selectOne(soQueryWrapper);

//        if (ModuleConstant.SOHEADER_ORDER_TYPE.SERVICE_SO.equals(soInfo.getOrderType())) {
//            if (ModuleConstant.SOPO_BILLIING_STATUS.UNINVOICED.equals(soInfo.getBillingStatus())) {
//                //如果是一个service so 要判断service so的delivery status
//                if (!ModuleConstant.SOPO_DELIVERY_STATUS.UNFULFILED.equals(soInfo.getDeliveryStatus())) {
//                    //根据soNumber查询shippingAddress
//                    SoShippingAddressVO soShippingAddressVO = new SoShippingAddressVO();
//                    soShippingAddressVO.setSoKey(soNumber);
//                    soShippingAddressVO.setCompanyCode(companyCode);
//                    log.info("查询发运卡片信息,soNumber:{}", soNumber);
//                    R<Address> shippingAddress = remoteMdmService.getShippingAddress(soShippingAddressVO);
//                    if (shippingAddress.getCode() != 200) {
//                        throw new RuntimeException("soNumber:[" + soNumber + "]查询地址信息服务调用失败，获取地址信息未成功");
//                    }
//
//                    DeliveryShippedResp deliveryShippedResp = new DeliveryShippedResp();
//                    deliveryShippedResp.setBpName(soInfo.getBpName());
//                    deliveryShippedResp.setBusinessPartner(soInfo.getBpCustomer());
//
//                    LambdaQueryWrapper<SoItem> soItemQueryWrapper = new LambdaQueryWrapper<SoItem>()
//                            .and(soItem -> soItem.eq(SoItem::getCompanyCode, companyCode))
//                            .and(soItem -> soItem.eq(SoItem::getSoNumber, soNumber));
//                    List<SoItem> soItemList = soItemMapper.selectList(soItemQueryWrapper);
//
//                    soItemList.forEach(s -> {
//                        R<SkuMaster> skuRes = remoteMdmService.getSkuByNumber(s.getSkuNumber());
//                        log.info("调用SKU服务查询SKU {} 信息的状态为：{}", s.getSoNumber(), skuRes.getCode());
//                        if (skuRes == null && skuRes.getCode() != 200) {
//                            throw new RuntimeException("SKU服务调用失败，获取SKU信息未成功");
//                        }
//                        if (!StringUtils.isEmpty(skuRes.getData().getSkuName())) {
//                            s.setSkuName(skuRes.getData().getSkuName());
//                        }
//
//                        s.setNetAmount(s.getSalesQty().multiply(s.getUnitPrice()));
//                        //根据城市查询税费
//                        R<TaxCalculateResp> tax = this.getTax(s, shippingAddress);
//                        log.info("调用税费查询接口完成,查询状态:{},查询结果:{}", tax.getCode(), tax.getData());
//                        if (tax.getCode() != 200) {
//                            throw new RuntimeException("计算税费接口调用失败,失败原因:" + tax.getMsg());
//                        }
//                        s.setTaxCalculateResp(tax.getData());
////                        deliveryShippedResp.setTotalAmount(deliveryShippedResp.getTotalAmount().add(s.getTaxCalculateResp().getTotalCad()).add(s.getUnitPrice()));
//                        deliveryShippedResp.setTotalAmount(deliveryShippedResp.getTotalAmount().add(s.getTaxCalculateResp().getTotalCad()));
//                    });
//
//                    deliveryShippedResp.setSoItemList(soItemList);
//                    deliveryShippedResps.add(deliveryShippedResp);
//                }
//            }
//        } else {
        deliveryShippedResps = deliveryItemMapper.selectUnBillShippedHeader(soNumber, companyCode);
        if (deliveryShippedResps == null || deliveryShippedResps.isEmpty()) {
            return deliveryShippedResps;
        }

        for (DeliveryShippedResp deliveryShippedResp : deliveryShippedResps) {
            List<SoItem> soItems = getSoItems(companyCode, soNumber);
            //根据deliveryNumber查询shippingAddress
            SoShippingAddressVO soShippingAddressVO = new SoShippingAddressVO();
            soShippingAddressVO.setDeliveryKey(deliveryShippedResp.getDeliveryNumber());
            soShippingAddressVO.setCompanyCode(companyCode);
            log.info("查询发运卡片信息,deliveryNumber:{}", deliveryShippedResp.getDeliveryNumber());
            Address shippingAddress = addressService.getShippingAddress(soShippingAddressVO);
            if (shippingAddress == null) {
                throw new RuntimeException("deliveryNumber:[" + deliveryShippedResp.getDeliveryNumber() + "]查询地址信息服务调用失败，获取地址信息未成功");
            }
            if (shippingAddress != null) {
                deliveryShippedResp.setShippingAddress(shippingAddress.getAddress1());
            }
            soItems.stream().forEach(s -> {
                if (1 == s.getIsKitting()) {
                    for (int i = 0; i < s.getKittingItems().size(); i++) {
                        SkuKitting k = s.getKittingItems().get(i);
                        log.info("获取到的kitting组合中的sku为:{},数量为:{}", k.getComponentSku(), k.getComponentQty());
                        LambdaQueryWrapper<DeliveryItem> deliveryItemQuery = new LambdaQueryWrapper<DeliveryItem>()
                                .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getDeliveryNumber, deliveryShippedResp.getDeliveryNumber()))
                                .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getSkuNumber, k.getComponentSku()))
                                .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getKittingSku, s.getSkuNumber()))
                                .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getReferenceDoc, soNumber))
                                .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getReferenceDocItem, s.getSoItem()))
                                .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getCompanyCode, companyCode));
                        DeliveryItem d = deliveryItemMapper.selectOne(deliveryItemQuery);
                        log.info("获取到的发运表中的sku为:{},可发运数量为:{}", d.getSkuNumber(), d.getDeliveryQty());
                        log.info("获取到的发运表中的sku为:{},已经发运数量为:{}", d.getSkuNumber(), d.getDeliveredQty());
                        k.setId(d.getId());
                        k.setSalesQty(d.getDeliveryQty());
                        k.setShippedQty(d.getDeliveredQty());
                        if (i == s.getKittingItems().size() - 1) {
                            s.setSalesQty(d.getDeliveredQty().divide(k.getComponentQty(), 2, BigDecimal.ROUND_HALF_UP));
                            s.setNetAmount(s.getSalesQty().multiply(s.getUnitPrice()));
                            //根据城市查询税费
                            R<TaxCalculateResp> tax = this.getTax(s, shippingAddress);
                            log.info("调用税费查询接口完成,查询状态:{},查询结果:{}", tax.getCode(), tax.getData());
                            if (tax.getCode() != 200) {
                                throw new RuntimeException("计算税费接口调用失败,失败原因:" + tax.getMsg());
                            }
                            s.setTaxCalculateResp(tax.getData());
//                                    x.setTotalAmount(x.getTotalAmount().add(s.getTaxCalculateResp().getTotalCad()).add(s.getUnitPrice()));
                            deliveryShippedResp.setTotalAmount(deliveryShippedResp.getTotalAmount().add(s.getTaxCalculateResp().getTotalCad()));
                        }
                    }
                } else {
                    LambdaQueryWrapper<DeliveryItem> deliveryItemQuery = new LambdaQueryWrapper<DeliveryItem>()
                            .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getDeliveryNumber, deliveryShippedResp.getDeliveryNumber()))
                            .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getSkuNumber, s.getSkuNumber()))
                            .isNull(DeliveryItem::getKittingSku)
                            .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getReferenceDoc, soNumber))
                            .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getReferenceDocItem, s.getSoItem()))
                            .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getCompanyCode, companyCode));

                    if ("SE".equals(s.getSkuType())) {
                        DeliveryItem d = deliveryItemMapper.selectOne(deliveryItemQuery);
                        s.setId(d.getId());
                        s.setBasicQty(d.getDeliveryQty());
                        s.setShippedQTY(d.getDeliveredQty());
                        s.setNetAmount(s.getShippedQTY().multiply(s.getUnitPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
                    } else {
                        SkuMaster skuRes = skuService.getSku(s.getSkuNumber(), null, companyCode);
                        log.info("调用SKU服务查询SKU {} 信息的状态为：{}", s.getSoNumber(), skuRes);
                        if (skuRes == null) {
                            throw new RuntimeException("SKU服务调用失败，获取SKU信息未成功");
                        }

                        BigDecimal salesBasicRate = skuRes.getSalesBasicRate();
                        log.info(">>>>so 查询未开票列表，该sku:{}的换算单位为:{}", s.getSkuNumber(), salesBasicRate);
                        if (null == salesBasicRate || BigDecimal.ZERO.equals(salesBasicRate)) {
                            salesBasicRate = BigDecimal.ONE;
                        }

                        DeliveryItem d = deliveryItemMapper.selectOne(deliveryItemQuery);
                        s.setId(d.getId());
                        s.setBasicQty(d.getDeliveryQty());
                        s.setShippedQTY(d.getDeliveredQty().divide(salesBasicRate, 3, BigDecimal.ROUND_HALF_UP));
                        s.setNetAmount(s.getShippedQTY().multiply(s.getUnitPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
                    }
                    //根据城市查询税费
                    R<TaxCalculateResp> tax = this.getTax(s, shippingAddress);
                    log.info("调用税费查询接口完成,查询状态:{},查询结果:{}", tax.getCode(), tax.getData());
                    if (tax.getCode() != 200) {
                        throw new RuntimeException("计算税费接口调用失败");
                    }
                    s.setTaxCalculateResp(tax.getData());
//                            x.setTotalAmount(x.getTotalAmount().add(s.getTaxCalculateResp().getTotalCad()).add(s.getUnitPrice()));
                    deliveryShippedResp.setTotalAmount(deliveryShippedResp.getTotalAmount().add(s.getTaxCalculateResp().getTotalCad()));
                }
            });
            deliveryShippedResp.setSoItemList(soItems);
        }
//            }
        return deliveryShippedResps;
    }

    /**
     * 税费计算
     *
     * @param s
     * @param shippingAddress
     * @return
     */
    private R<TaxCalculateResp> getTax(SoItem s, Address shippingAddress) {
        TaxTableCalculate taxTableCalculate = new TaxTableCalculate();
        List<TaxTableItems> list = new ArrayList<>();
        TaxTableItems taxTableItems = new TaxTableItems();
        taxTableItems.setIsTaxExempt(s.getTaxExmpt().toString());
        taxTableItems.setAmount(s.getNetAmount());
        list.add(taxTableItems);
        taxTableCalculate.setProvinceCode(shippingAddress.getRegionCode());
        taxTableCalculate.setTaxTableItemsList(list);

        return R.ok(taxTableService.taxCaculation(taxTableCalculate));
    }

    public List<DeliveryShippedResp> selectFullyBillList(String soNumber, String companyCode) {
        List<DeliveryShippedResp> deliveryShippedResps = new ArrayList<>();
        //查询soHeader的orderType
        LambdaQueryWrapper<SoHeader> soQueryWrapper = new LambdaQueryWrapper<SoHeader>()
                .and(soHeader -> soHeader.eq(SoHeader::getCompanyCode, companyCode))
                .and(soHeader -> soHeader.eq(SoHeader::getSoNumber, soNumber));
        SoHeader soInfo = soHeaderMapper.selectOne(soQueryWrapper);

//        if (ModuleConstant.SOHEADER_ORDER_TYPE.SERVICE_SO.equals(soInfo.getOrderType())) {
//            if (ModuleConstant.SOPO_BILLIING_STATUS.FULLY_INVOICED.equals(soInfo.getBillingStatus())) {
//                //根据soNumber查询shippingAddress
//                SoShippingAddressVO soShippingAddressVO = new SoShippingAddressVO();
//                soShippingAddressVO.setSoKey(soNumber);
//                soShippingAddressVO.setCompanyCode(companyCode);
//                log.info("查询发运卡片信息,soNumber:{}", soNumber);
//                R<Address> shippingAddress = remoteMdmService.getShippingAddress(soShippingAddressVO);
//                if (shippingAddress.getCode() != 200) {
//                    throw new RuntimeException("soNumber:[" + soNumber + "]查询地址信息服务调用失败，获取地址信息未成功");
//                }
//
//                DeliveryShippedResp deliveryShippedResp = new DeliveryShippedResp();
//                deliveryShippedResp.setBusinessPartner(soInfo.getBpCustomer());
//                deliveryShippedResp.setBpName(soInfo.getBpName());
//
//                //通过SoNumber查询soBillHeader
//                LambdaQueryWrapper<SoBillHeader> soBillQuery = new LambdaQueryWrapper<>();
//                soBillQuery.eq(SoBillHeader::getReferenceDoc, soNumber);
//                soBillQuery.eq(SoBillHeader::getCompanyCode, companyCode);
//                SoBillHeader billHeader = soBillHeaderMapper.selectOne(soBillQuery);
//                deliveryShippedResp.setBillNumber(billHeader.getBillingNumber());
//                deliveryShippedResp.setBillPostingDate(billHeader.getPostingDate());
//
//                LambdaQueryWrapper<SoItem> soItemQueryWrapper = new LambdaQueryWrapper<SoItem>()
//                  .and(soItem -> soItem.eq(SoItem::getCompanyCode, companyCode))
//                  .and(soItem -> soItem.eq(SoItem::getSoNumber, soNumber));
//                List<SoItem> soItemList = soItemMapper.selectList(soItemQueryWrapper);
//
//                soItemList.forEach(s -> {
//                    R<SkuMaster> skuRes = remoteMdmService.getSkuByNumber(s.getSkuNumber());
//                    log.info("调用SKU服务查询SKU {} 信息的状态为：{}", s.getSoNumber(), skuRes.getCode());
//                    if (skuRes == null && skuRes.getCode() != 200) {
//                        throw new RuntimeException("SKU服务调用失败，获取SKU信息未成功");
//                    }
//                    if (!StringUtils.isEmpty(skuRes.getData().getSkuName())) {
//                        s.setSkuName(skuRes.getData().getSkuName());
//                    }
//
//                    s.setNetAmount(s.getSalesQty().multiply(s.getUnitPrice()));
//                    //根据城市查询税费
//                    R<TaxCalculateResp> tax = this.getTax(s, shippingAddress);
//                    log.info("调用税费查询接口完成,查询状态:{},查询结果:{}", tax.getCode(), tax.getData());
//                    if (tax.getCode() != 200) {
//                        throw new RuntimeException("计算税费接口调用失败,失败原因:" + tax.getMsg());
//                    }
//                    s.setTaxCalculateResp(tax.getData());
////                    deliveryShippedResp.setTotalAmount(deliveryShippedResp.getTotalAmount().add(s.getTaxCalculateResp().getTotalCad()).add(s.getUnitPrice()));
//                    deliveryShippedResp.setTotalAmount(deliveryShippedResp.getTotalAmount().add(s.getTaxCalculateResp().getTotalCad()));
//                });
//
//                deliveryShippedResp.setSoItemList(soItemList);
//                deliveryShippedResps.add(deliveryShippedResp);
//            }
//        } else {
        deliveryShippedResps = deliveryItemMapper.selectFullyBillList(soNumber, companyCode);
        if (deliveryShippedResps.size() > 0) {
            deliveryShippedResps.forEach(x -> {
                //通过deliveryNumber查询soBillHeader
                LambdaQueryWrapper<SoBillHeader> soBillQuery = new LambdaQueryWrapper<>();
                soBillQuery.eq(SoBillHeader::getReferenceDoc, x.getDeliveryNumber());
                soBillQuery.eq(SoBillHeader::getCompanyCode, companyCode);
                SoBillHeader billHeader = soBillHeaderMapper.selectOne(soBillQuery);
                x.setBillNumber(billHeader.getBillingNumber());
                x.setBillPostingDate(billHeader.getPostingDate());
                List<SoItem> soItems = getSoItems(companyCode, soNumber);
                //根据deliveryNumber查询shippingAddress
                SoShippingAddressVO soShippingAddressVO = new SoShippingAddressVO();
                soShippingAddressVO.setDeliveryKey(x.getDeliveryNumber());
                soShippingAddressVO.setCompanyCode(companyCode);
                log.info("查询发运卡片信息,deliveryNumber:{}", x.getDeliveryNumber());
                Address shippingAddress = addressService.getShippingAddress(soShippingAddressVO);
                if (shippingAddress == null) {
                    throw new RuntimeException("deliveryNumber:[" + x.getDeliveryNumber() + "]查询发运卡片信息服务调用失败，获取发运卡片信息未成功");
                }
                if (shippingAddress != null) {
                    x.setShippingAddress(shippingAddress.getAddress1());
                }
                soItems.stream().forEach(s -> {
                    if (1 == s.getIsKitting()) {
                        for (int i = 0; i < s.getKittingItems().size(); i++) {
                            SkuKitting k = s.getKittingItems().get(i);
                            log.info("获取到的kitting组合中的sku为:{},数量为:{}", k.getComponentSku(), k.getComponentQty());
                            LambdaQueryWrapper<DeliveryItem> deliveryItemQuery = new LambdaQueryWrapper<DeliveryItem>()
                                    .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getDeliveryNumber, x.getDeliveryNumber()))
                                    .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getSkuNumber, k.getComponentSku()))
                                    .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getKittingSku, s.getSkuNumber()))
                                    .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getReferenceDoc, soNumber))
                                    .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getReferenceDocItem, s.getSoItem()))
                                    .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getCompanyCode, companyCode));
                            DeliveryItem d = deliveryItemMapper.selectOne(deliveryItemQuery);
                            log.info("获取到的发运表中的sku为:{},可发运数量为:{}", d.getSkuNumber(), d.getDeliveryQty());
                            log.info("获取到的发运表中的sku为:{},已经发运数量为:{}", d.getSkuNumber(), d.getDeliveredQty());
                            k.setId(d.getId());
                            k.setSalesQty(d.getDeliveryQty());
                            k.setShippedQty(d.getDeliveredQty());
                            if (i == s.getKittingItems().size() - 1) {
                                s.setSalesQty(d.getDeliveredQty().divide(k.getComponentQty(), 2, BigDecimal.ROUND_HALF_UP));
                                s.setNetAmount(s.getSalesQty().multiply(s.getUnitPrice()));
                                //根据城市查询税费
                                R<TaxCalculateResp> tax = this.getTax(s, shippingAddress);
                                log.info("调用税费查询接口完成,查询状态:{},查询结果:{}", tax.getCode(), tax.getData());
                                if (tax.getCode() != 200) {
                                    throw new RuntimeException("计算税费接口调用失败,失败原因:" + tax.getMsg());
                                }
                                s.setTaxCalculateResp(tax.getData());
//                                    x.setTotalAmount(x.getTotalAmount().add(s.getTaxCalculateResp().getTotalCad()).add(s.getUnitPrice()));
                                x.setTotalAmount(x.getTotalAmount().add(s.getTaxCalculateResp().getTotalCad()));
                            }
                        }
                    } else {
                        LambdaQueryWrapper<DeliveryItem> deliveryItemQuery = new LambdaQueryWrapper<DeliveryItem>()
                                .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getDeliveryNumber, x.getDeliveryNumber()))
                                .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getSkuNumber, s.getSkuNumber()))
                                .isNull(DeliveryItem::getKittingSku)
                                .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getReferenceDoc, soNumber))
                                .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getReferenceDocItem, s.getSoItem()))
                                .and(deliveryItem -> deliveryItem.eq(DeliveryItem::getCompanyCode, companyCode));

                        if ("SE".equals(s.getSkuType())) {
                            DeliveryItem d = deliveryItemMapper.selectOne(deliveryItemQuery);
                            s.setId(d.getId());
                            s.setBasicQty(d.getDeliveryQty());
                            s.setShippedQTY(d.getDeliveredQty());
                            s.setNetAmount(s.getShippedQTY().multiply(s.getUnitPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
                        } else {
                            SkuMaster skuRes = skuService.getSku(s.getSkuNumber(), null, companyCode);
                            log.info("调用SKU服务查询SKU {} 信息的状态为：{}", s.getSoNumber(), skuRes);
                            if (skuRes == null) {
                                throw new RuntimeException("SKU服务调用失败，获取SKU信息未成功");
                            }

                            BigDecimal salesBasicRate = skuRes.getSalesBasicRate();
                            log.info(">>>>so 查询已开票列表，该sku:{}的换算单位为:{}", s.getSkuNumber(), salesBasicRate);
                            if (null == salesBasicRate || BigDecimal.ZERO.equals(salesBasicRate)) {
                                salesBasicRate = BigDecimal.ONE;
                            }

                            DeliveryItem d = deliveryItemMapper.selectOne(deliveryItemQuery);
                            s.setId(d.getId());
                            s.setBasicQty(d.getDeliveryQty());
                            s.setShippedQTY(d.getDeliveredQty().divide(salesBasicRate, 3, BigDecimal.ROUND_HALF_UP));
                            s.setNetAmount(s.getShippedQTY().multiply(s.getUnitPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
                        }
                        //根据城市查询税费
                        R<TaxCalculateResp> tax = this.getTax(s, shippingAddress);
                        log.info("调用税费查询接口完成,查询状态:{},查询结果:{}", tax.getCode(), tax.getData());
                        if (tax.getCode() != 200) {
                            throw new RuntimeException("计算税费接口调用失败");
                        }
                        s.setTaxCalculateResp(tax.getData());
//                            x.setTotalAmount(x.getTotalAmount().add(s.getTaxCalculateResp().getTotalCad()).add(s.getUnitPrice()));
                        x.setTotalAmount(x.getTotalAmount().add(s.getTaxCalculateResp().getTotalCad()));
                    }
                });
                x.setSoItemList(soItems);
            });
        }
//        }
        return deliveryShippedResps;
    }

}
