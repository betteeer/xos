package com.inossem.oms.svc.service;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inossem.oms.base.common.constant.ModuleConstant;
import com.inossem.oms.base.svc.domain.*;
import com.inossem.oms.base.svc.domain.VO.*;
import com.inossem.oms.base.svc.mapper.*;
import com.inossem.oms.base.utils.UserInfoUtils;
import com.inossem.oms.mdm.service.AddressService;
import com.inossem.oms.mdm.service.SkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 【PO明细】Service业务层处理
 *
 * @author shigf
 * @date 2022-11-04
 */
@Service
@Slf4j
public class PoHeaderService {
    private static final String ADDRESS_TYPE = "PO";
    private static final String SUB_ADDRESS_SRC_TYPE = "payto";
    private static final String SUB_ADDRESS_DEST_TYPE = "shipto";

    @Resource
    private PoHeaderMapper poHeaderMapper;

    @Resource
    private PoItemMapper poItemMapper;

    @Resource
    private AddressService addressService;

    @Resource
    private SkuService skuService;

    @Resource
    private DeliveryItemMapper deliveryItemMapper;

    @Resource
    private PoInvoiceHeaderMapper poInvoiceHeaderMapper;

    @Resource
    private PoInvoiceItemMapper poInvoiceItemMapper;


    /**
     * 查询【PO明细】列表
     *
     * @param po【PO明细】
     * @return 【PO明细】
     */
    public List<PoHeader> selectPoHeaderList(PoListVo po) {
        return poHeaderMapper.selectPoHeaderList(po);
    }

    public List<PoHeader> selectPoHeaderList1(PoListVo1 po) {
        return poHeaderMapper.selectPoHeaderList1(po);
    }


    /**
     * 开票的详情和订单的详情数据类似，可以使用同一个服务实现
     *
     * @param companyCode
     * @param poNumber
     * @param tag
     * @return
     */
    public Map<String, Object> details(String companyCode, String poNumber, String tag) {
        Map<String, Object> res = new HashMap<>();

        // 1.获取po_header信息
        PoHeader poHeader = poHeaderMapper
                .selectPoHeaderByCompanyAndNumber(companyCode, poNumber);


        // 2. 查invoice信息
        if ("invoice".equals(tag)) {
            res.put("totalAmount", poHeader.getGrossAmount());
            PoInvoiceHeader header = new PoInvoiceHeader();
            header.setReferenceDoc(poNumber);
            header.setCompanyCode(companyCode);
            List<PoInvoiceHeader> pos = poInvoiceHeaderMapper.selectPoInvoiceHeaderList(header);
            String invoiceTicketUrl = null;
            if (pos != null && !pos.isEmpty()) {

                res.put("invoiceAmount", pos.get(0).getGrossAmount());
                res.put("invoicedPercentage", pos.get(0).getGrossAmount().divide(poHeader.getGrossAmount(),2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)));

                invoiceTicketUrl = pos.get(0).getInvoiceTicketUrl();
                header.setInvoiceNumber(pos.get(0).getInvoiceNumber());
                header.setPostingDate(pos.get(0).getPostingDate());
                header.setGrossAmount(pos.get(0).getGrossAmount());
                header.setGstAmount(pos.get(0).getGstAmount());
                header.setHstAmount(pos.get(0).getHstAmount());
                header.setPstAmount(pos.get(0).getPstAmount());
                header.setQstAmount(pos.get(0).getQstAmount());
                header.setNetAmount(pos.get(0).getNetAmount());
            }else{
                res.put("invoiceAmount", 0);
                res.put("invoicedPercentage", 0);
            }
            res.put("invoiceTicketUrl", invoiceTicketUrl == null ? "" : invoiceTicketUrl);
            res.put("poInvoicedHeader", header);
        }
        // 2.获取po_item信息
        List<PoItem> items = poItemMapper.selectPoItemByPoNumber(companyCode, poNumber);
        log.info("获取到{} po的items数量为：{}", poNumber, items.size());

        List<Map<String, Object>> poItems = new ArrayList<>();
        // 4、获取po_item的sku信息
        items.stream().forEach(item -> {
            String skuNumber = item.getSkuNumber();
            SkuMaster skuRes = skuService.getSku(skuNumber, null, companyCode);
            log.info("调用SKU服务查询{} SKU的服务，状态为：{}", skuNumber, skuRes);
            if (skuRes == null) {
                throw new RuntimeException("获取SKU失败");
            }
            SkuMaster sku = skuRes;
            Map<String, Object> poItem = new HashMap<>();
            poItem.put("item", item);
            poItem.put("sku", sku);

            if ("invoice".equals(tag)) {
                LambdaQueryWrapper<PoInvoiceItem> poInvoiceItemLambdaQueryWrapper = new LambdaQueryWrapper<PoInvoiceItem>()
                        .eq(PoInvoiceItem::getReferenceDoc, item.getPoNumber())
                        .eq(PoInvoiceItem::getCompanyCode, companyCode)
                        .eq(PoInvoiceItem::getReferenceDocItem, item.getPoItem());
                PoInvoiceItem poInvoiceItem = poInvoiceItemMapper.selectOne(poInvoiceItemLambdaQueryWrapper);
                if (null != poInvoiceItem) {
                    poItem.put("invoiceQty", poInvoiceItem.getInvoiceQty());
                } else {
                    poItem.put("invoiceQty", 0);
                }
            }

            poItems.add(poItem);
        });

        //5. 查地址信息
        AddressQueryVo queryAddress = new AddressQueryVo();
        queryAddress.setType(ADDRESS_TYPE);
        queryAddress.setSubType(SUB_ADDRESS_SRC_TYPE);
        queryAddress.setKey(poNumber);
        queryAddress.setCompanyCode(companyCode);
        List<Address> srcAddress = addressService.getAddress(queryAddress);
        log.info("远程服务获取地址信息，获取到的结果为：" + srcAddress);
        if (srcAddress == null) {
            throw new RuntimeException("Get Pay Address Info Error");
        }
        if (srcAddress.size() != 1) {
            throw new RuntimeException("Pay Address Config Error!");
        }
        Address srcAddr = srcAddress.get(0);

        queryAddress.setSubType(SUB_ADDRESS_DEST_TYPE);
        List<Address> destAddress =  addressService.getAddress(queryAddress);
        log.info("远程服务获取地址dest信息，获取到的结果为：" + destAddress);
        if (destAddress == null) {
            throw new RuntimeException("Get Ship Address Info Error");
        }
        if (destAddress.size() != 1) {
            throw new RuntimeException("Ship Address Config Error!");
        }
        Address destAddr = destAddress.get(0);

        // 6.查是否有发运记录，如果有，则不允许修改
        LambdaQueryWrapper<DeliveryItem> deliveryItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deliveryItemLambdaQueryWrapper.eq(DeliveryItem::getReferenceDoc, poNumber);
        deliveryItemLambdaQueryWrapper.eq(DeliveryItem::getCompanyCode, companyCode);
        deliveryItemLambdaQueryWrapper.eq(DeliveryItem::getIsDeleted, 0);
        long deliveryCount = deliveryItemMapper.selectCount(deliveryItemLambdaQueryWrapper);

        // 7、组装参数

        res.put("header", poHeader);
        res.put("payToAddress", srcAddr);
        res.put("shipToAddress", destAddr);
        res.put("items", poItems);
        res.put("isUpdate", deliveryCount == 0 ? 0 : 1);



        return res;
    }

    /**
     * 保存po接口
     *
     * @param po
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public PoHeader create(PoSaveVo po) {

        // 1.参数校验
        poSaveParamCheck(po);

        // 2.保存header
        PoHeader header = getPoHeader(po);
        poHeaderMapper.insert(header);

        // 3.保存address
        saveAddress(header, po);

        // 4.保存item
        List<PoItem> items = getPoItems(header, po);
        items.stream().forEach(item -> {
            poItemMapper.insertPoItem(item);
        });

        return header;
    }

    private List<PoItem> getPoItems(PoHeader header, PoSaveVo po) {
        List<PoItem> items = new ArrayList<>();
        List<PoItemSaveVo> transItems = po.getPoItemList();
        for (PoItemSaveVo item : transItems) {
            PoItem i = new PoItem();
            log.info(">>>>>poItem  Id  is : {}", item.getId());
            i.setId(item.getId());
            i.setBasicQty(item.getBasicQty());
            i.setBasicUom(item.getBasicUom());
            i.setCompanyCode(po.getCompanyCode());
            i.setCurrencyCode(po.getCurrencyCode());
            i.setItemType(item.getItemType());
            i.setNetValue(item.getNetValue());
            i.setPoItem(item.getPoItem());
            i.setPoNumber(header.getPoNumber());
            i.setPurchaseQty(item.getPurchaseQty());
            log.info("purchaseUOM is : {}", item.getPurchaseUom());
            if (StringUtils.isEmpty(item.getPurchaseUom())) {
                item.setPurchaseUom(item.getBasicUom());
            }
            i.setPurchaseUom(item.getPurchaseUom());
            i.setSkuNumber(item.getSkuNumber());
            i.setTaxExmpt(item.getTaxExmpt());
            i.setUnitPrice(item.getUnitPrice());
            i.setWarehouseCode(item.getWarehouseCode());


            Date date = new Date();
            i.setGmtCreate(date);
            i.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
            i.setGmtModified(date);
            i.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
            i.setIsDeleted(0);

            items.add(i);
        }

        return items;
    }

    private void saveAddress(PoHeader po, PoSaveVo vo) {
        AddressSaveVo billAddress = vo.getPayAddress();
        billAddress.setCompanyCode(po.getCompanyCode());
        billAddress.setType(ADDRESS_TYPE);
        billAddress.setSubType(SUB_ADDRESS_SRC_TYPE);
        billAddress.setKey(po.getPoNumber());
        addressService.save(billAddress);

        AddressSaveVo shipAddress = vo.getShipAddress();
        shipAddress.setCompanyCode(vo.getCompanyCode());
        shipAddress.setType(ADDRESS_TYPE);
        shipAddress.setSubType(SUB_ADDRESS_DEST_TYPE);
        shipAddress.setKey(po.getPoNumber());
        addressService.save(shipAddress);
    }

    private PoHeader getPoHeader(PoSaveVo po) {
        PoHeader header = new PoHeader();
//        header.setId(po.getId());
        header.setBpName(po.getBpName());
        header.setBpVendor(po.getBpVendor());
        header.setCompanyCode(po.getCompanyCode());
        header.setCurrencyCode(po.getCurrencyCode());
        header.setDeliveryDate(po.getDeliveryDate());
        if ("DSPO".equals(po.getOrderType())) {
            header.setDropshipComplete(0);
        }
        header.setGrossAmount(po.getGrossAmount());
        header.setGstAmount(po.getGstAmount());
        header.setHstAmount(po.getHstAmount());
        header.setNetAmount(po.getNetAmount());
        header.setOrderDate(po.getOrderDate());
        header.setOrderStatus(po.getOrderStatus());
        header.setDeliveryStatus(po.getDeliveryStatus());
        header.setInvoiceStatus(po.getInvoiceStatus());
        header.setOrderType(po.getOrderType());
        header.setPaymentTerm(po.getPaymentTerm());
        header.setPoNotes(po.getPoNotes());


        //参数封装
        LambdaQueryWrapper<PoHeader> poHeaderLambdaQueryWrapper = new LambdaQueryWrapper<PoHeader>().
                eq(PoHeader::getCompanyCode,po.getCompanyCode()).
                orderByDesc(PoHeader::getId).
                last("limit 1");
        PoHeader pos = poHeaderMapper.selectOne(poHeaderLambdaQueryWrapper);
        log.info("创建po,查询po是否有值,用于生成po_Number:{}", pos);
        String poNumber = "30000000";
        if (pos != null) {
            poNumber = BigDecimal.ONE.add(new BigDecimal(pos.getPoNumber())).toString();
        }
        log.info("生成的poNumber为:{}", poNumber);
        header.setPoNumber(poNumber);

        header.setPstAmount(po.getPstAmount());
        header.setQstAmount(po.getQstAmount());
        header.setReferenceNumber(po.getReferenceNumber());

        Date date = new Date();
        header.setGmtCreate(date);
        header.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
        header.setGmtModified(date);
        header.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
        header.setIsDeleted(0);

        header.setClearanceFee(po.getClearanceFee());
        header.setLogisticsCosts(po.getLogisticsCosts());
        header.setOtherExpenses(po.getOtherExpenses());
        return header;
    }

    private void poSaveParamCheck(PoSaveVo po) {
//        throw new RuntimeException("Po 参数异常");
    }

    @Transactional(rollbackFor = Exception.class)
    public Object modify(PoSaveVo po) {
        // 1.参数校验
        if (po.getId() == null || StringUtils.isEmpty(po.getPoNumber())) {
            throw new RuntimeException("param check error");
        }
        poSaveParamCheck(po);
        //根据PoNumber查询delivery_item是否有值(忽略是否有空发运情况  有空发运了也不让修改)
        LambdaQueryWrapper<DeliveryItem> deliveryItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deliveryItemLambdaQueryWrapper.eq(DeliveryItem::getReferenceDoc, po.getPoNumber());
        deliveryItemLambdaQueryWrapper.eq(DeliveryItem::getCompanyCode, po.getCompanyCode());
        deliveryItemLambdaQueryWrapper.eq(DeliveryItem::getIsDeleted, 0);
        List<DeliveryItem> deliveryItemList = deliveryItemMapper.selectList(deliveryItemLambdaQueryWrapper);
        if (deliveryItemList.size() > 0) {
            throw new RuntimeException("当前po存在已发运,不允许修改");
        }
        // 2.更新header
        PoHeader header = new PoHeader();
        header.setId(po.getId());
        header.setBpName(po.getBpName());
        header.setBpVendor(po.getBpVendor());
        header.setCompanyCode(po.getCompanyCode());
        header.setCurrencyCode(po.getCurrencyCode());
        header.setDeliveryDate(po.getDeliveryDate());
        header.setClearanceFee(po.getClearanceFee());
        header.setLogisticsCosts(po.getLogisticsCosts());
        header.setOtherExpenses(po.getOtherExpenses());
        if ("DSPO".equals(po.getOrderType())) {
            header.setDropshipComplete(0);
        }
        header.setGrossAmount(po.getGrossAmount());
        header.setGstAmount(po.getGstAmount());
        header.setHstAmount(po.getHstAmount());
        header.setNetAmount(po.getNetAmount());
        header.setOrderDate(po.getOrderDate());
        header.setOrderStatus(po.getOrderStatus());
        header.setDeliveryStatus(po.getDeliveryStatus());
        header.setInvoiceStatus(po.getInvoiceStatus());
        header.setOrderType(po.getOrderType());
        header.setPaymentTerm(po.getPaymentTerm());
        header.setPoNotes(po.getPoNotes());
        header.setPoNumber(po.getPoNumber());
        header.setPstAmount(po.getPstAmount());
        header.setQstAmount(po.getQstAmount());
        header.setReferenceNumber(po.getReferenceNumber());
        Date date = new Date();
        header.setGmtCreate(date);
        header.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
        header.setGmtModified(date);
        header.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
        header.setIsDeleted(0);
        try {
            poHeaderMapper.updateById(header);
            //更新Po_Item信息
            //根据poNumber和companyCode查询po_item保存的skulist
            LambdaQueryWrapper<PoItem> poItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
            poItemLambdaQueryWrapper.eq(PoItem::getPoNumber, po.getPoNumber());
            poItemLambdaQueryWrapper.eq(PoItem::getCompanyCode, po.getCompanyCode());
            poItemLambdaQueryWrapper.eq(PoItem::getIsDeleted, 0);
            List<PoItem> poItems = poItemMapper.selectList(poItemLambdaQueryWrapper);
            List<Long> ids = new ArrayList<>();
            poItems.forEach(poItem -> {
                ids.add(poItem.getId());
            });
            po.getPoItemList().forEach(xPoItem -> {
                ids.add(xPoItem.getId());
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

            // 4.更新item
            List<PoItem> items = getPoItems(header, po);
            items.forEach(pItem -> {
                poItemMapper.updateById(pItem);
            });

            //判断是否存在删除行
            if (idMap.size() > 0) {
                idMap.forEach((key, value) -> {
                    PoItem poItem = new PoItem();
                    poItem.setId(key);
                    poItem.setIsDeleted(1);
                    poItemMapper.updateById(poItem);
                });
            }

            // 3.更新address
            AddressSaveVo payAddress = po.getPayAddress();
            payAddress.setCompanyCode(po.getCompanyCode());
            payAddress.setType(ADDRESS_TYPE);
            payAddress.setSubType(SUB_ADDRESS_SRC_TYPE);
            payAddress.setKey(po.getPoNumber());
            addressService.modifyAddress(payAddress);

            AddressSaveVo shipAddress = po.getShipAddress();
            shipAddress.setCompanyCode(po.getCompanyCode());
            shipAddress.setType(ADDRESS_TYPE);
            shipAddress.setSubType(SUB_ADDRESS_DEST_TYPE);
            shipAddress.setKey(po.getPoNumber());
            addressService.modifyAddress(shipAddress);
        } catch (Exception e) {
            log.error("modify po order header failed", e);
            throw new RuntimeException(e);
        }
        return header;
    }


    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(String poNumber, String companyCode) {
        PoHeader po = new PoHeader();
        po.setDeliveryStatus(ModuleConstant.SOPO_DELIVERY_STATUS.FULLFILLED);

        LambdaQueryWrapper<PoHeader> poUpdateWrapper = new LambdaQueryWrapper<PoHeader>()
                .and(poHeader -> poHeader.eq(PoHeader::getPoNumber, poNumber))
                .and(poHeader -> poHeader.eq(PoHeader::getCompanyCode, companyCode));

        try {
            return poHeaderMapper.update(po, poUpdateWrapper);
        } catch (Exception e) {
            throw new RuntimeException("update service po status error");
        }
    }


//    /**
//     * 查询【PO明细】
//     *
//     * @param id 【PO明细】主键
//     * @return 【PO明细】
//     */
//    public PoHeader selectPoHeaderById(Long id) {
//        return poHeaderMapper.selectPoHeaderById(id);
//    }
//    /**
//     * 新增【PO明细】
//     *
//     * @param poHeader 【PO明细】
//     * @return 结果
//     */
//    public int insertPoHeader(PoHeader poHeader) {
//        return poHeaderMapper.insertPoHeader(poHeader);
//    }
//
//    /**
//     * 修改【PO明细】
//     *
//     * @param poHeader 【PO明细】
//     * @return 结果
//     */
//    public int updatePoHeader(PoHeader poHeader) {
//        return poHeaderMapper.updatePoHeader(poHeader);
//    }
//
//    /**
//     * 批量删除【PO明细】
//     *
//     * @param ids 需要删除的【PO明细】主键
//     * @return 结果
//     */
//    public int deletePoHeaderByIds(Long[] ids) {
//        return poHeaderMapper.deletePoHeaderByIds(ids);
//    }
//
//    /**
//     * 删除【PO明细】信息
//     *
//     * @param id 【PO明细】主键
//     * @return 结果
//     */
//    public int deletePoHeaderById(Long id) {
//        return poHeaderMapper.deletePoHeaderById(id);
//    }
}
