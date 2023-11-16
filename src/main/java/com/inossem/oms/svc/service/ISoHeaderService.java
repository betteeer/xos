package com.inossem.oms.svc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.inossem.oms.base.common.constant.ModuleConstant;
import com.inossem.oms.base.svc.domain.*;
import com.inossem.oms.base.svc.domain.DTO.SoHeaderSearchForm;
import com.inossem.oms.base.svc.domain.VO.AddressQueryVo;
import com.inossem.oms.base.svc.domain.VO.AddressSaveVo;
import com.inossem.oms.base.svc.domain.VO.SalesOrderListQyery;
import com.inossem.oms.base.svc.domain.VO.SoOrderHeaderInfoVo;
import com.inossem.oms.base.svc.mapper.DeliveryItemMapper;
import com.inossem.oms.base.svc.mapper.SoHeaderMapper;
import com.inossem.oms.base.svc.mapper.SoItemMapper;
import com.inossem.oms.base.utils.UserInfoUtils;
import com.inossem.oms.mdm.service.AddressService;
import com.inossem.oms.mdm.service.BpService;
import com.inossem.oms.mdm.service.SkuService;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 销售单主信息 Service接口
 *
 * @author shigf
 * @date 2022-10-17
 */
@Service
@Slf4j
public class ISoHeaderService {
    public static final String TYPE_ADDRESS_SO = "so";
    public static final String SUBTYPE_ADDRESS_SO_BILL = "billto";
    public static final String SUBTYPE_ADDRESS_SO_SHIP = "shipto";

    @Resource
    private SoHeaderMapper soHeaderMapper;

    @Resource
    private SoItemMapper soItemMapper;

    @Resource
    private DeliveryItemMapper deliveryItemMapper;

    @Resource
    private AddressService addressService;

    @Resource
    private BpService bpService;

    @Resource
    private SkuService skuService;

    /**
     * get sales order list
     *
     * @param salesOrderListQyery
     * @return
     */
    public List<SoHeader> selectSoHeaderList(SalesOrderListQyery salesOrderListQyery) {
        List<SoHeader> soHeaderList = soHeaderMapper.selectListBySoHeaderVo(salesOrderListQyery);
        return soHeaderList;
    }

    /**
     * create so order header
     *
     * @param soOrderHeaderInfoVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SoHeader create(SoOrderHeaderInfoVo soOrderHeaderInfoVo) {
        //封装So_Header信息
        SoHeader soHeader = this.soHeaderInfoPacking(soOrderHeaderInfoVo);
        try {
            soHeaderMapper.insert(soHeader);
            //封装So_Item信息
            List<SoItem> soItemList = this.soItemInfoPacking(soHeader, soOrderHeaderInfoVo);
            soItemMapper.insertBatch(soItemList);
            // 保存地址信息
            AddressSaveVo billAddress = soOrderHeaderInfoVo.getBillAddress();
            billAddress.setCompanyCode(soOrderHeaderInfoVo.getCompanyCode());
            billAddress.setType(TYPE_ADDRESS_SO);
            billAddress.setSubType(SUBTYPE_ADDRESS_SO_BILL);
            billAddress.setKey(soHeader.getSoNumber());
//            R<Address> res = remoteMdmService.saveAddress(billAddress);
//            if (res.getCode() != 200) {
//                throw new RuntimeException("远程服务调用失败，保存地址信息异常");
//            }
            addressService.save(billAddress);
            AddressSaveVo shipAddress = soOrderHeaderInfoVo.getShipAddress();
            shipAddress.setCompanyCode(soOrderHeaderInfoVo.getCompanyCode());
            shipAddress.setType(TYPE_ADDRESS_SO);
            shipAddress.setSubType(SUBTYPE_ADDRESS_SO_SHIP);
            shipAddress.setKey(soHeader.getSoNumber());
//            res = remoteMdmService.saveAddress(shipAddress);
//            if (res.getCode() != 200) {
//                throw new RuntimeException("远程服务调用失败，保存地址信息异常");
//            }
            addressService.save(shipAddress);
        } catch (Exception e) {
            log.error("create so order header failed", e);
            throw new RuntimeException(e);
        }
        return soHeader;
    }

    /**
     * Packaging So Order Header Params
     *
     * @param soOrderHeaderInfoVo
     * @return
     */
    private SoHeader soHeaderInfoPacking(SoOrderHeaderInfoVo soOrderHeaderInfoVo) {
        //参数封装
        LambdaQueryWrapper<SoHeader> soHeaderLambdaQueryWrapper = new LambdaQueryWrapper<SoHeader>()
                .eq(SoHeader::getCompanyCode, soOrderHeaderInfoVo.getCompanyCode())
                .orderByDesc(SoHeader::getId).
                last("limit 1");
        SoHeader so = soHeaderMapper.selectOne(soHeaderLambdaQueryWrapper);
        String soNumber = "10000000";
        if (so != null) {
            soNumber = BigDecimal.ONE.add(new BigDecimal(so.getSoNumber())).toString();
        }
        SoHeader soHeader = new SoHeader();
        soHeader.setCompanyCode(soOrderHeaderInfoVo.getCompanyCode());
        soHeader.setSoNumber(soNumber);
        soHeader.setOrderType(soOrderHeaderInfoVo.getOrderType());
        if (ModuleConstant.SOHEADER_ORDER_TYPE.DROPSHIP_SO.equals(soOrderHeaderInfoVo.getOrderType())) {
            soHeader.setDropshipComplete(0);
        }
        soHeader.setOrderStatus(soOrderHeaderInfoVo.getOrderStatus());
        soHeader.setDeliveryStatus(soOrderHeaderInfoVo.getDeliveryStatus());
        soHeader.setBillingStatus(soOrderHeaderInfoVo.getBillingStatus());
        soHeader.setBpCustomer(soOrderHeaderInfoVo.getBpCustomer());
        soHeader.setBpName(soOrderHeaderInfoVo.getBpName());
        soHeader.setOrderDate(soOrderHeaderInfoVo.getOrderDate());
        soHeader.setDeliveryDate(soOrderHeaderInfoVo.getDeliveryDate());
        soHeader.setPaymentTerm(soOrderHeaderInfoVo.getPaymentTerm());
        soHeader.setReferenceNumber(soOrderHeaderInfoVo.getReferenceNumber());
        soHeader.setCurrencyCode(soOrderHeaderInfoVo.getCurrencyCode());
        soHeader.setGrossAmount(soOrderHeaderInfoVo.getGrossAmount());
        soHeader.setGstAmount(soOrderHeaderInfoVo.getGstAmount());
        soHeader.setHstAmount(soOrderHeaderInfoVo.getHstAmount());
        soHeader.setQstAmount(soOrderHeaderInfoVo.getQstAmount());
        soHeader.setPstAmount(soOrderHeaderInfoVo.getPstAmount());
        soHeader.setNetAmount(soOrderHeaderInfoVo.getNetAmount());
        if (!StringUtils.isEmpty(soOrderHeaderInfoVo.getSoNotes())) {
            soHeader.setSoNotes(soOrderHeaderInfoVo.getSoNotes());
        }
        Date date = new Date();
        soHeader.setGmtCreate(date);
        soHeader.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
        soHeader.setGmtModified(date);
        soHeader.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
        soHeader.setIsDeleted(0);

        soHeader.setClearanceFee(soOrderHeaderInfoVo.getClearanceFee());
        soHeader.setLogisticsCosts(soOrderHeaderInfoVo.getLogisticsCosts());
        soHeader.setOtherExpenses(soOrderHeaderInfoVo.getOtherExpenses());

        return soHeader;
    }

    /**
     * Packaging So Item Params
     *
     * @param soHeader
     * @param soOrderHeaderInfoVo
     * @return
     */
    private List<SoItem> soItemInfoPacking(SoHeader soHeader, SoOrderHeaderInfoVo soOrderHeaderInfoVo) {
        List<SoItem> soItemList = new ArrayList<>();
        List<SoItem> soItemListParams = soOrderHeaderInfoVo.getSoItemList();
        soItemListParams.forEach(x -> {
            SoItem soItem = new SoItem();
            soItem.setId(x.getId());
            log.info(">>>>>SoItem  Id  is : {}", x.getId());
            soItem.setCompanyCode(soOrderHeaderInfoVo.getCompanyCode());
            soItem.setSoNumber(soHeader.getSoNumber());
            soItem.setSoItem(x.getSoItem());
            soItem.setSkuNumber(x.getSkuNumber());
            soItem.setIsKitting(x.getIsKitting());
            soItem.setSkuType(x.getSkuType());
            if (StringUtils.isNotBlank(x.getWarehouseCode())) {
                soItem.setWarehouseCode(x.getWarehouseCode());
            }
            soItem.setSalesQty(x.getSalesQty());
            soItem.setSalesUom(x.getSalesUom());
            soItem.setBasicQty(x.getBasicQty());
            soItem.setBasicUom(x.getBasicUom());
            soItem.setUnitPrice(x.getUnitPrice());
            soItem.setNetValue(x.getNetValue());
            soItem.setTaxExmpt(x.getTaxExmpt());
            soItem.setCurrencyCode(x.getCurrencyCode());
            soItem.setGmtCreate(soHeader.getGmtCreate());
            soItem.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
            soItem.setGmtModified(soHeader.getGmtModified());
            soItem.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
            soItem.setIsDeleted(0);
            if (1 == x.getIsKitting()) {
                soItem.setSkuVersion(x.getSkuVersion());
            }
            soItemList.add(soItem);
        });
        return soItemList;
    }

    /**
     * 查询so  order header
     *
     * @param companyCode
     * @param soNumber
     * @return
     */
    public Map<String, Object> details(String companyCode, String soNumber) {
        // 查soHeader
        LambdaQueryWrapper<SoHeader> wrapper = new LambdaQueryWrapper<SoHeader>().
                and(com -> com.eq(SoHeader::getCompanyCode, companyCode)).
                and(com -> com.eq(SoHeader::getSoNumber, soNumber));
        SoHeader header = soHeaderMapper.selectOne(wrapper);

        Integer isUpdate = 0;
        if (ModuleConstant.SOHEADER_ORDER_TYPE.SERVICE_SO.equals(header.getOrderType())) {
            if (ModuleConstant.SOPO_DELIVERY_STATUS.FULLFILLED.equals(header.getOrderStatus())) {
                isUpdate = 1;
            }
        } else {
            //根据soNumber查询delivery_item是否有值(忽略是否有空发运情况  有空发运了也不让修改)
            //允许修改
            Boolean aBoolean = this.checkSoDelivery(soNumber, companyCode);
            if (aBoolean) {
                //不允许修改
                isUpdate = 1;
            }
        }
        BusinessPartner bp = bpService.getBpNameByBpNumber(header.getCompanyCode(), header.getBpCustomer());
        header.setBpName(bp.getBpName());
        if (header == null) {
            return null;
        }
        // 查地址(发货地址)
        AddressQueryVo addressQueryVo = new AddressQueryVo();
        addressQueryVo.setCompanyCode(companyCode);
        addressQueryVo.setType(TYPE_ADDRESS_SO);
        addressQueryVo.setSubType(SUBTYPE_ADDRESS_SO_BILL);
        addressQueryVo.setKey(soNumber);
        List<Address> billAddress = addressService.getAddress(addressQueryVo);
        log.info("调用地址信息查询服务SoNumber:{} ,CompanyCode:{},信息的状态为：{}", soNumber, companyCode, billAddress);
        // 查地址(收货地址)
        addressQueryVo.setSubType(SUBTYPE_ADDRESS_SO_SHIP);
        List<Address> shipAddress = addressService.getAddress(addressQueryVo);
        log.info("调用地址信息查询服务SoNumber:{} ,CompanyCode:{},信息的状态为：{}", soNumber, companyCode, shipAddress);
        // 组合
        Map<String, Object> res = new HashMap<>();
        res.put("header", header);
        res.put("items", getSoItems(companyCode, soNumber));
        res.put("billAddress", billAddress);
        res.put("shipAddress", shipAddress);
        res.put("isUpdate", isUpdate);
        return res;
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
//                R<SkuMaster> skuByNumberAndVersion = remoteMdmService.getSkuByNumberAndVersion(si.getSkuNumber(), si.getSkuVersion(),companyCode);
                SkuMaster skuByNumberAndVersion = skuService.getSku(si.getSkuNumber(), si.getSkuVersion(), companyCode);
                log.info("调用KittingSKU服务查询SKU {} 信息的状态为：{}", si.getSoNumber(), skuByNumberAndVersion);
                if (null == skuByNumberAndVersion) {
                    throw new RuntimeException("kittingSKU服务调用失败，获取SKU信息未成功");
                }
                if (!StringUtils.isEmpty(skuByNumberAndVersion.getSkuName())) {
                    si.setSkuName(skuByNumberAndVersion.getSkuName());
                }
                si.setKittingItems(skuByNumberAndVersion.getKittingItems());
            } else {
                SkuMaster skuRes = skuService.getSku(si.getSkuNumber(), null, companyCode);
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

//    private List<SoItem> getSoItems(String companyCode, String soNumber) {
//        // 查soItem
//        LambdaQueryWrapper<SoItem> itemWrapper = new LambdaQueryWrapper<SoItem>().
//                and(com -> com.eq(SoItem::getCompanyCode, companyCode)).
//                and(com -> com.eq(SoItem::getSoNumber, soNumber))
//                .orderByAsc(SoItem::getSoItem);
//        List<SoItem> items = soItemMapper.selectList(itemWrapper);
//        log.info("查到的 {} 的明细条数为：{}", soNumber, items.size());
//        items.stream().forEach(it -> {
//            String skuNumber = it.getSkuNumber();
//            R<SkuMaster> skuRes = remoteMdmService.getSkuByNumber(skuNumber);
//            log.info("调用SKU服务查询SKU {} 信息的状态为：{}", skuNumber, skuRes.getCode());
//            if (skuRes == null && skuRes.getCode() != 200) {
//                throw new RuntimeException("SKU服务调用失败，获取SKU信息未成功");
//            }
//            if (!StringUtils.isEmpty(skuRes.getData().getSkuName())) {
//                it.setSkuName(skuRes.getData().getSkuName());
//            }
//        });
//        return items;
//    }


    @Transactional(rollbackFor = Exception.class)
    public void updateBpName(String companyCode, String bpNumber, String bpName) {
        try {
            SoHeader soHeader = new SoHeader();
            soHeader.setBpName(bpName);
            QueryWrapper<SoHeader> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("company_code", companyCode);
            queryWrapper.eq("bp_customer", bpNumber);
            soHeaderMapper.update(soHeader, queryWrapper);
        } catch (Exception e) {
            log.error("update so order header bpName failed", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * 验证so是否存在发运记录  或者 空单    有则不让修改
     *
     * @return
     */
    private Boolean checkSoDelivery(String soOrderNumber, String companyCode) {
        Boolean b = false;
        //根据soNumber查询delivery_item是否有值(忽略是否有空发运情况  有空发运了也不让修改)
        LambdaQueryWrapper<DeliveryItem> deliveryItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deliveryItemLambdaQueryWrapper.eq(DeliveryItem::getReferenceDoc, soOrderNumber);
        deliveryItemLambdaQueryWrapper.eq(DeliveryItem::getCompanyCode, companyCode);
        deliveryItemLambdaQueryWrapper.eq(DeliveryItem::getIsDeleted, 0);
        List<DeliveryItem> deliveryItemList = deliveryItemMapper.selectList(deliveryItemLambdaQueryWrapper);
        if (deliveryItemList.size() > 0) {
            b = true;
        }
        return b;
    }


    /**
     * modify so order header
     *
     * @param soOrderHeaderInfoVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SoHeader modify(SoOrderHeaderInfoVo soOrderHeaderInfoVo) {
        //根据soNumber查询delivery_item是否有值(忽略是否有空发运情况  有空发运了也不让修改)
        Boolean aBoolean = this.checkSoDelivery(soOrderHeaderInfoVo.getSoOrderNumber(), soOrderHeaderInfoVo.getCompanyCode());
        if (aBoolean) {
            throw new RuntimeException("当前so存在已发运,不允许修改");
        }
        SoHeader soHeader = new SoHeader();
        soHeader.setId(soOrderHeaderInfoVo.getSoOrderId());
        soHeader.setCompanyCode(soOrderHeaderInfoVo.getCompanyCode());
        soHeader.setSoNumber(soOrderHeaderInfoVo.getSoOrderNumber());
        soHeader.setOrderType(soOrderHeaderInfoVo.getOrderType());
        if (ModuleConstant.SOHEADER_ORDER_TYPE.DROPSHIP_SO.equals(soOrderHeaderInfoVo.getOrderType())) {
            soHeader.setDropshipComplete(0);
        }
        soHeader.setOrderStatus(soOrderHeaderInfoVo.getOrderStatus());
        soHeader.setDeliveryStatus(soOrderHeaderInfoVo.getDeliveryStatus());
        soHeader.setBillingStatus(soOrderHeaderInfoVo.getBillingStatus());
        soHeader.setBpCustomer(soOrderHeaderInfoVo.getBpCustomer());
        soHeader.setBpName(soOrderHeaderInfoVo.getBpName());
        soHeader.setOrderDate(soOrderHeaderInfoVo.getOrderDate());
        soHeader.setDeliveryDate(soOrderHeaderInfoVo.getDeliveryDate());
        soHeader.setPaymentTerm(soOrderHeaderInfoVo.getPaymentTerm());
        soHeader.setReferenceNumber(soOrderHeaderInfoVo.getReferenceNumber());
        soHeader.setCurrencyCode(soOrderHeaderInfoVo.getCurrencyCode());
        soHeader.setGrossAmount(soOrderHeaderInfoVo.getGrossAmount());
        soHeader.setGstAmount(soOrderHeaderInfoVo.getGstAmount());
        soHeader.setHstAmount(soOrderHeaderInfoVo.getHstAmount());
        soHeader.setQstAmount(soOrderHeaderInfoVo.getQstAmount());
        soHeader.setPstAmount(soOrderHeaderInfoVo.getPstAmount());
        soHeader.setNetAmount(soOrderHeaderInfoVo.getNetAmount());
        soHeader.setClearanceFee(soOrderHeaderInfoVo.getClearanceFee());
        soHeader.setLogisticsCosts(soOrderHeaderInfoVo.getLogisticsCosts());
        soHeader.setOtherExpenses(soOrderHeaderInfoVo.getOtherExpenses());
        if (!StringUtils.isEmpty(soOrderHeaderInfoVo.getSoNotes())) {
            soHeader.setSoNotes(soOrderHeaderInfoVo.getSoNotes());
        }
        Date date = new Date();
        soHeader.setGmtCreate(date);
        soHeader.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
        soHeader.setGmtModified(date);
        soHeader.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
        soHeader.setIsDeleted(0);
        try {
            soHeaderMapper.updateById(soHeader);
            //根据 soNumber 和 companyCode查询so_item 保存的 skulist Id
            LambdaQueryWrapper<SoItem> soItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
            soItemLambdaQueryWrapper.eq(SoItem::getSoNumber, soOrderHeaderInfoVo.getSoOrderNumber());
            soItemLambdaQueryWrapper.eq(SoItem::getCompanyCode, soOrderHeaderInfoVo.getCompanyCode());
            soItemLambdaQueryWrapper.eq(SoItem::getIsDeleted, 0);
            List<SoItem> soItems = soItemMapper.selectList(soItemLambdaQueryWrapper);
            List<Long> ids = new ArrayList<>();
            soItems.forEach(soItem -> {
                ids.add(soItem.getId());
            });
            soOrderHeaderInfoVo.getSoItemList().forEach(xSoItem -> {
                ids.add(xSoItem.getId());
            });
            Map<Long, Object> idMap = new HashMap<>();
            for (Long o : ids) {
                if (idMap.isEmpty()) {
                    idMap.put(o, null);
                } else {
                    if (idMap.containsKey(o)) {
                        idMap.remove(o);
                    } else {
                        idMap.put(o, null);
                    }
                }
            }

            //更新item
            List<SoItem> soItemList = this.soItemInfoPacking(soHeader, soOrderHeaderInfoVo);
            soItemList.forEach(sItem -> {
                if (sItem.getId() == null) {
                    soItemMapper.insert(sItem);
                } else {
                    soItemMapper.updateById(sItem);
                }
            });

            //判断是否存在删除行
            if (idMap.size() > 0) {
                idMap.forEach((key, value) -> {
                    SoItem soItem = new SoItem();
                    soItem.setId(key);
                    soItem.setIsDeleted(1);
                    soItemMapper.updateById(soItem);
                });
            }

            // 更新地址信息
            AddressSaveVo billAddress = soOrderHeaderInfoVo.getBillAddress();
            billAddress.setCompanyCode(soOrderHeaderInfoVo.getCompanyCode());
            billAddress.setType(TYPE_ADDRESS_SO);
            billAddress.setSubType(SUBTYPE_ADDRESS_SO_BILL);
            billAddress.setKey(soOrderHeaderInfoVo.getSoOrderNumber());
            addressService.modifyAddress(billAddress);
            AddressSaveVo shipAddress = soOrderHeaderInfoVo.getShipAddress();
            shipAddress.setCompanyCode(soOrderHeaderInfoVo.getCompanyCode());
            shipAddress.setType(TYPE_ADDRESS_SO);
            shipAddress.setSubType(SUBTYPE_ADDRESS_SO_SHIP);
            shipAddress.setKey(soOrderHeaderInfoVo.getSoOrderNumber());
            addressService.modifyAddress(shipAddress);
        } catch (Exception e) {
            log.error("modify so order header failed", e);
            throw new RuntimeException(e);
        }
        return soHeader;
    }

    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(String soNumber, String companyCode) {
        SoHeader so = new SoHeader();
        so.setDeliveryStatus(ModuleConstant.SOPO_DELIVERY_STATUS.FULLFILLED);

        LambdaQueryWrapper<SoHeader> soUpdateWrapper = new LambdaQueryWrapper<SoHeader>()
                .and(soHeader -> soHeader.eq(SoHeader::getSoNumber, soNumber))
                .and(soHeader -> soHeader.eq(SoHeader::getCompanyCode, companyCode));

        try {
            return soHeaderMapper.update(so, soUpdateWrapper);
        } catch (Exception e) {
            throw new RuntimeException("update service so status error");
        }
    }
}
