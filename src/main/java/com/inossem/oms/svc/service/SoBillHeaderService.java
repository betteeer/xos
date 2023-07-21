package com.inossem.oms.svc.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.inossem.oms.api.bk.api.BkCoaMappingService;
import com.inossem.oms.api.bk.api.BookKeepingService;
import com.inossem.oms.api.bk.model.BkCoaMappingModel;
import com.inossem.oms.api.bk.model.SoInvoiceModel;
import com.inossem.oms.api.bk.model.TaxContentBuilder;
import com.inossem.oms.base.common.constant.ModuleConstant;
import com.inossem.oms.base.svc.domain.*;
import com.inossem.oms.base.svc.domain.VO.AddressQueryVo;
import com.inossem.oms.base.svc.domain.VO.SoBillResp;
import com.inossem.oms.base.svc.mapper.*;
import com.inossem.oms.base.utils.UserInfoUtils;
import com.inossem.oms.mdm.service.AddressService;
import com.inossem.oms.mdm.service.BpService;
import com.inossem.oms.mdm.service.CompanyService;
import com.inossem.oms.mdm.service.SkuService;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 【开票】Service业务层处理
 *
 * @author guoh
 * @date 2022-11-20
 */
@Service
@Slf4j
public class SoBillHeaderService {

    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();
    @Resource
    private SoBillHeaderMapper soBillHeaderMapper;
    @Resource
    private SoHeaderMapper soHeaderMapper;
    @Resource
    private SoBillItemMapper soBillItemMapper;
    @Resource
    private DeliveryItemMapper deliveryItemMapper;
    @Resource
    private DeliveryHeaderMapper deliveryHeaderMapper;
    @Resource
    private BookKeepingService bookKeepingService;
    @Resource
    private AddressService addressService;
    @Resource
    private ConditionTableService conditionTableService;
    @Resource
    private SystemConnectService systemConnectService;
    @Resource
    private BpService bpService;
    @Resource
    private CompanyService companyService;
    @Resource
    private CurrencyExchangeMapper currencyExchangeMapper;

    @Resource
    private SkuService skuService;

    @Resource
    private BkCoaMappingService bkCoaMappingService;
    /**
     * 新增【请填写功能名称】
     *
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int insertSoBillHeader(List<SoBillHeader> soBillHeaders) {
        String billNumber = null;
        for (int i = 0; i < soBillHeaders.size(); i++) {
            SoBillHeader bill = soBillHeaders.get(i);
            billNumber = this.getBillNumber(i, bill.getBillType(), bill.getCompanyCode(), threadLocal);
            SoHeader soHeader;
            if (bill.getReferenceDocType().equals(ModuleConstant.REFERENCE_TYPE.DN)) {
                QueryWrapper<DeliveryItem> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("delivery_number", bill.getReferenceDoc())
                        .eq("company_code", bill.getCompanyCode())
                        .last("limit 1");
                DeliveryItem deliveryItem = deliveryItemMapper.selectOne(queryWrapper);
                //根据deliveryNumber查询delivery_item得到so number 查询so得到so信息
                LambdaQueryWrapper<SoHeader> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(SoHeader::getSoNumber, deliveryItem.getReferenceDoc());
                lambdaQueryWrapper.eq(SoHeader::getCompanyCode, deliveryItem.getCompanyCode());
                soHeader = soHeaderMapper.selectOne(lambdaQueryWrapper);
            } else {
                LambdaQueryWrapper<SoHeader> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(SoHeader::getSoNumber, bill.getReferenceDoc());
                lambdaQueryWrapper.eq(SoHeader::getCompanyCode, bill.getCompanyCode());
                soHeader = soHeaderMapper.selectOne(lambdaQueryWrapper);
            }
            SoBillHeader billInsert = new SoBillHeader();
            billInsert.setCompanyCode(bill.getCompanyCode());
            billInsert.setPartnerId(bill.getPartnerId());
            billInsert.setBillingNumber(billNumber);
            billInsert.setPaymentTerm(soHeader.getPaymentTerm());
            billInsert.setPostingDate(bill.getPostingDate());
            billInsert.setReferenceDoc(bill.getReferenceDoc());
            billInsert.setReferenceDocType(bill.getReferenceDocType());
            billInsert.setCurrencyCode(soHeader.getCurrencyCode());
            billInsert.setGrossAmount(bill.getGrossAmount());
            billInsert.setGstAmount(bill.getGstAmount());
            billInsert.setHstAmount(bill.getHstAmount());
            billInsert.setQstAmount(bill.getQstAmount());
            billInsert.setPstAmount(bill.getPstAmount());
            billInsert.setNetAmount(bill.getNetAmount());
            billInsert.setIsCleared(0);
            //由BK返回   没有BK  所以没值
            billInsert.setAccountingDoc("");
            Date date = new Date();
            billInsert.setGmtCreate(date);
            billInsert.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
            billInsert.setGmtModified(date);
            billInsert.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
            List<SoBillItem> billItems = new ArrayList<>();
            try {
                soBillHeaderMapper.insertSoBillHeader(billInsert);
                bill.getSoBillItemList().forEach(billItem -> {
                    SoBillItem billItemInsert = new SoBillItem();
                    billItemInsert.setCompanyCode(billItem.getCompanyCode());
                    billItemInsert.setBillingNumber(billInsert.getBillingNumber());
                    billItemInsert.setBillingItem(billItem.getBillingItem());
                    billItemInsert.setReferenceDoc(billItem.getReferenceDoc());
                    billItemInsert.setReferenceDocItem(billItem.getReferenceDocItem());
                    billItemInsert.setSkuNumber(billItem.getSkuNumber());
                    billItemInsert.setBillQty(billItem.getBillQty());
                    billItemInsert.setBillUom(billItem.getBillUom());
                    billItemInsert.setUnitPrice(billItem.getUnitPrice());
                    billItemInsert.setGrossAmount(billItem.getGrossAmount());
                    billItemInsert.setTaxExmpt(billItem.getTaxExmpt());
                    billItemInsert.setCurrencyCode(soHeader.getCurrencyCode());
                    billItemInsert.setGstAmount(billItem.getGstAmount());
                    billItemInsert.setHstAcmount(billItem.getHstAcmount());
                    billItemInsert.setQstAmount(billItem.getQstAmount());
                    billItemInsert.setPstAmount(billItem.getPstAmount());
                    billItemInsert.setNetAmount(billItem.getNetAmount());
                    billItemInsert.setGmtCreate(date);
                    billItemInsert.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                    billItemInsert.setGmtModified(date);
                    billItemInsert.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
                    billItems.add(billItemInsert);
                    soBillItemMapper.insertSoBillItem(billItemInsert);
                });
                SoHeader sh = new SoHeader();
                if (bill.getReferenceDocType().equals(ModuleConstant.REFERENCE_TYPE.DN)) {
                    //更新deliveryHeader中的completeBilling
                    LambdaQueryWrapper<DeliveryHeader> deliveryHeaderQueryWrapper = new LambdaQueryWrapper<>();
                    deliveryHeaderQueryWrapper.eq(DeliveryHeader::getDeliveryNumber, bill.getReferenceDoc());
                    deliveryHeaderQueryWrapper.eq(DeliveryHeader::getCompanyCode, bill.getCompanyCode());
                    DeliveryHeader deliveryHeader = new DeliveryHeader();
                    deliveryHeader.setCompleteBilling(1);
                    deliveryHeader.setBillingNumber(billNumber);
                    deliveryHeaderMapper.update(deliveryHeader, deliveryHeaderQueryWrapper);
                    if (ModuleConstant.SOPO_DELIVERY_STATUS.PARTIALLY_FULLFILLED.equals(soHeader.getDeliveryStatus())) {
                        sh.setBillingStatus(ModuleConstant.SOPO_BILLIING_STATUS.PARTIALLY_INVOICED);
                    } else if (ModuleConstant.SOPO_DELIVERY_STATUS.FULLFILLED.equals(soHeader.getDeliveryStatus())) {
                        //根据soNumber查询delive_item 得到还有没有未完成开票的单子  有则soHeader为部分开票
                        List<DeliveryHeader> unComplateBill = deliveryHeaderMapper.getUnComplateBill(soHeader.getSoNumber(), soHeader.getCompanyCode());
                        if (unComplateBill.size() > 0) {
                            sh.setBillingStatus(ModuleConstant.SOPO_BILLIING_STATUS.PARTIALLY_INVOICED);
                        } else {
                            sh.setBillingStatus(ModuleConstant.SOPO_BILLIING_STATUS.FULLY_INVOICED);
                        }
                    }
                } else {
                    sh.setBillingStatus(ModuleConstant.SOPO_BILLIING_STATUS.FULLY_INVOICED);
                }
                LambdaQueryWrapper<SoHeader> soHeaderLambdaQueryWrapper = new LambdaQueryWrapper<>();
                soHeaderLambdaQueryWrapper.eq(SoHeader::getSoNumber, soHeader.getSoNumber());
                soHeaderLambdaQueryWrapper.eq(SoHeader::getCompanyCode, soHeader.getCompanyCode());
                soHeaderMapper.update(sh, soHeaderLambdaQueryWrapper);
            } catch (Exception e) {
                throw new RuntimeException("so create billing error", e);
            }
            // 如果是非merge的情况,则直接同步数据给bookkeeping
            if (ModuleConstant.BILL_TYPE.BILL == bill.getBillType()) {
                try {
                    log.info(">>>>>>非merge的情况,同步数据给bookkeeping");
                    log.info(">>>>>>非merge bill , 同步bk参数 ,billInsert:{} ", billInsert);
                    log.info(">>>>>>非merge bill , 同步bk参数 ,billItems:{} ", billItems);

                    if ("USD".equals(billInsert.getCurrencyCode())) {
                        CurrencyExchange exchangeRate = currencyExchangeMapper.selectOne(new LambdaQueryWrapper<CurrencyExchange>()
                                .eq(CurrencyExchange::getCurrencyFr, billInsert.getCurrencyCode()));
                        if (exchangeRate != null) {
                            billInsert.setExchangeRate(exchangeRate.getRate());
                        } else {
                            throw new RuntimeException("po invoice get exchange rate error");
                        }
                    }
                    List<SoBillItem> cleanSoBillItems = billItems.stream().filter(v -> v.getBillQty().compareTo(BigDecimal.ZERO) == 1).collect(Collectors.toList());
                    String s = syncToBk(billInsert, cleanSoBillItems);
                    LambdaQueryWrapper<SoBillHeader> soBillHeaderLambdaQueryWrapper = new LambdaQueryWrapper<SoBillHeader>()
                            .eq(SoBillHeader::getBillingNumber, billNumber)
                            .eq(SoBillHeader::getCompanyCode, billInsert.getCompanyCode());
                    SoBillHeader soBillHeader = new SoBillHeader();
                    soBillHeader.setAccountingDoc(s);
                    soBillHeaderMapper.update(soBillHeader, soBillHeaderLambdaQueryWrapper);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
        // 如果是merge的情况,则需要整合当前merge的数据
        if (ModuleConstant.BILL_TYPE.MERGE_BILL == soBillHeaders.get(0).getBillType()) {
            try {
                log.info(">>>>>>merge的情况,需要整合当前merge的数据,再同步bookkeeping");
                LambdaQueryWrapper<SoBillHeader> soBillHeaderLambdaQueryWrapper = new LambdaQueryWrapper<SoBillHeader>()
                        .eq(SoBillHeader::getBillingNumber, billNumber)
                        .eq(SoBillHeader::getCompanyCode, soBillHeaders.get(0).getCompanyCode());
                List<SoBillHeader> soBillHeaderList = soBillHeaderMapper.selectList(soBillHeaderLambdaQueryWrapper);
                SoBillHeader sumSoBillHead = soBillHeaderMapper.selectBillHeaderInfo(billNumber, soBillHeaders.get(0).getCompanyCode());
                SoBillHeader billInsert = new SoBillHeader();
                billInsert.setCompanyCode(soBillHeaderList.get(0).getCompanyCode());
                billInsert.setPartnerId(soBillHeaderList.get(0).getPartnerId());
                billInsert.setBillingNumber(billNumber);
                billInsert.setPaymentTerm(soBillHeaderList.get(0).getPaymentTerm());
                billInsert.setPostingDate(soBillHeaderList.get(0).getPostingDate());
                billInsert.setReferenceDoc(soBillHeaderList.get(0).getReferenceDoc());
                billInsert.setReferenceDocType(soBillHeaderList.get(0).getReferenceDocType());
                billInsert.setCurrencyCode(soBillHeaderList.get(0).getCurrencyCode());
                billInsert.setGrossAmount(sumSoBillHead.getGrossAmount());
                billInsert.setGstAmount(sumSoBillHead.getGstAmount());
                billInsert.setHstAmount(sumSoBillHead.getHstAmount());
                billInsert.setQstAmount(sumSoBillHead.getQstAmount());
                billInsert.setPstAmount(sumSoBillHead.getPstAmount());
                billInsert.setNetAmount(sumSoBillHead.getNetAmount());
                billInsert.setIsCleared(0);
                //由BK返回   没有BK  所以没值
                billInsert.setAccountingDoc("");
                billInsert.setGmtCreate(soBillHeaderList.get(0).getGmtCreate());
                billInsert.setCreateBy(soBillHeaderList.get(0).getCreateBy());
                billInsert.setGmtModified(soBillHeaderList.get(0).getGmtModified());
                billInsert.setModifiedBy(soBillHeaderList.get(0).getModifiedBy());

                LambdaQueryWrapper<SoBillItem> soBillItemLambdaQueryWrapper = new LambdaQueryWrapper<SoBillItem>()
                        .eq(SoBillItem::getBillingNumber, billNumber)
                        .eq(SoBillItem::getCompanyCode, soBillHeaderList.get(0).getCompanyCode());
                List<SoBillItem> billItems = soBillItemMapper.selectList(soBillItemLambdaQueryWrapper);
                log.info(">>>>>>merge bill , 同步bk参数 ,billInsert:{} ", billInsert);
                log.info(">>>>>>merge bill , 同步bk参数 ,billItems:{} ", billItems);


                if ("USD".equals(billInsert.getCurrencyCode())) {
                    CurrencyExchange exchangeRate = currencyExchangeMapper.selectOne(new LambdaQueryWrapper<CurrencyExchange>()
                            .eq(CurrencyExchange::getCurrencyFr, billInsert.getCurrencyCode()));

                    if (exchangeRate != null) {
                        billInsert.setExchangeRate(exchangeRate.getRate());
                    } else {
                        throw new RuntimeException("po invoice get exchange rate error");
                    }
                }

                String s = syncToBk(billInsert, billItems);
                LambdaQueryWrapper<SoBillHeader> soBillHeaderLambdaQueryWrappers = new LambdaQueryWrapper<SoBillHeader>()
                        .eq(SoBillHeader::getBillingNumber, billNumber)
                        .eq(SoBillHeader::getCompanyCode, billInsert.getCompanyCode());
                SoBillHeader soBillHeader = new SoBillHeader();
                soBillHeader.setAccountingDoc(s);
                soBillHeaderMapper.update(soBillHeader, soBillHeaderLambdaQueryWrappers);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        return 1;
    }

    /**
     * 同步开票数据给bookkeeping
     *
     * @param bill
     * @param billItems
     * @throws IOException
     */
    private String syncToBk(SoBillHeader bill, List<SoBillItem> billItems) {
        try {
            log.info(">>>billing开票,同步bk信息开始");
            SoInvoiceModel model = getModel(bill, billItems);
            log.info(">>>>>开票同步bk,封装好的model参数为:{}", model);
            return bookKeepingService.soBill(model);
        } catch (Exception e) {
            // 先保证业务不受影响，临时不关注异常
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public static final Map<String, String> CURRENCY_MAPPING = new HashMap<>();

    static {
        CURRENCY_MAPPING.put("CNY", "1");
        CURRENCY_MAPPING.put("CAD", "2");
        CURRENCY_MAPPING.put("USD", "3");
    }

    /**
     * 把oms的数据转为bk需要的数据
     *
     * @param bill
     * @param billItems
     * @return
     */
    private SoInvoiceModel getModel(SoBillHeader bill, List<SoBillItem> billItems) throws IOException {
        log.info("SO billItems：" + billItems);
        // 查开票的公司信息
        Company company = getCompany(bill.getCompanyCode());
        LambdaQueryWrapper<DeliveryItem> wrapper = new LambdaQueryWrapper<DeliveryItem>()
                .eq(DeliveryItem::getDeliveryNumber, bill.getReferenceDoc())
                .eq(DeliveryItem::getCompanyCode, bill.getCompanyCode());
        DeliveryItem deliveryItem = deliveryItemMapper.selectOne(wrapper);
        // 客户
        BusinessPartner bp = getBusinessPartner(bill.getCompanyCode(), bill.getPartnerId());
        Address bpBillAddress = getAddress(bill.getCompanyCode(), "so", "billto", deliveryItem.getReferenceDoc());
//                , bill.getPartnerId());
        log.info(">>>>>SO bill ：" + bill);
        Address bpShiplAddress = getAddress(bill.getCompanyCode(), "SODN", "shipto"
                , bill.getReferenceDoc());
        log.info("SO billing shipto：" + bpShiplAddress);
        SoInvoiceModel model = new SoInvoiceModel()

                .setCompany_id(company.getOrgidEx())
                .setCompany_name(company.getName())
                // bk 的companyCode
                .setCompany_code(company.getCompanyCodeEx())
                // 暂时传空
                .setCompany_address("")
                .setCompany_tel("")
                .setCompany_email(company.getCompanyEmail())
                //.setCompanyLogo(company.getLogoUrl())

                // 开票方公司GST NO
                .setCompany_gst_no("")
                // 开票方公司PST NO
                .setCompany_pst_no("")

                // 固定传空
//                .setGst(bill.getGstAmount().add(bill.getHstAmount()))
//                .setPst(bill.getPstAmount())
//                .setQst(bill.getQstAmount())

                // 开票业务编码
                .setReference_no(bill.getBillingNumber())
                .setInvoice_currency(bill.getCurrencyCode())
//                .setInvoice_currency(CURRENCY_MAPPING.get(bill.getCurrencyCode()))
                // 支付方式
                .setPay_method("1")

                .setInvoice_create_date(DateUtil.formatDate(bill.getGmtCreate()))
                .setInvoice_due_date(DateUtil.formatDate(bill.getGmtCreate()))
                .setPosting_date(DateUtil.formatDate(bill.getPostingDate()))

                // 收票方公司的客户ID
                .setBill_to_customer_id(StringUtils.isEmpty(bp.getBkBpNumberCustomer()) ? bp.getBpNumber() : bp.getBkBpNumberCustomer())
                .setBill_to_receiver("")
                // 02修改为bpname 01/固定为空
                .setBill_to_company(bp.getBpName())
                .setBill_to_street(bpBillAddress.getAddress1())
                .setBill_to_city(bpBillAddress.getCity())
                .setBill_to_province(bpBillAddress.getRegionCode())
                .setBill_to_country(bpBillAddress.getCountryCode())
                .setBill_to_postal_code(bpBillAddress.getPostalCode())
                .setBill_to_tel(bp.getBpTel())
                .setBill_to_email(bp.getBpEmail())

                .setShip_to_receiver("")
                .setShip_to_company(bp.getBpName())
                .setShip_to_street(bpShiplAddress.getAddress1())
                .setShip_to_city(bpShiplAddress.getCity())
                .setShip_to_province(bpShiplAddress.getRegionCode())
                .setShip_to_country(bpShiplAddress.getCountryCode())
                .setShip_to_postal_code(bpShiplAddress.getPostalCode())
                .setShip_to_tel("")
                .setShip_to_email("")

                // 总金额
                .setNet_amount(bill.getGrossAmount())

                // 固定为null
                //.setShipping(null)
                //.setDiscount(null)

                // 税前总计
                //.setTotalTaxable(bill.getGrossAmount())

                //.setTps(bill.getGstAmount())
                //.setTvq(bill.getQstAmount())
                //.setTvp(bill.getPstAmount())

                .setTotal_tax(bill.getNetAmount().subtract(bill.getGrossAmount()))
                .setTotal_fee(bill.getNetAmount())

                //.setTotalFeeExchangeCAD(bill.getNetAmount().multiply(BigDecimal.ONE))
                .setTotal_fee_local(bill.getNetAmount().multiply(BigDecimal.ONE))

//       如果是 usd 会调用 bk的税率接口 ，拿到税率 做换算
                .setExchange_rate(bill.getExchangeRate())
                //.setDeposit(null)
                .setInvoice_comments("")

                //.setFileId(null)
                //.setFilePageIndex(null)
                //.setFileUrl(null)
                .setBank_id(null)
                .setBank_account("")
                .setBank_name("")
                .setBr_type("0")
                .setTax_content(TaxContentBuilder.build(bill.getGstAmount(), bill.getHstAmount(), bill.getPstAmount(), bill.getQstAmount()));


//        // 根据公司信息去查，暂时固定
//        ConditionTable conditionTable = new ConditionTable();
//        conditionTable.setCompanyCode(company.getCompanyCode());
//        conditionTable.setConditionType("S001");
//        List<ConditionTable> tables = conditionTableService.selectConditionTableList(conditionTable);
//        ConditionTable table = null;
//        if (tables == null || tables.isEmpty()) {
//            table = new ConditionTable();
//        } else {
//            table = tables.get(0);
//        }
        BkCoaMappingModel mappingModel = bkCoaMappingService.getOrderTypeMapping(company.getCompanyCode(), "S001");
        if (null == mappingModel) {
            mappingModel = new BkCoaMappingModel();
        }
        
        List<SoInvoiceModel.SoInvoiceItem> items = new ArrayList<>();
        int index = 0;
        for (SoBillItem item : billItems) {
            SoInvoiceModel.SoInvoiceItem it = new SoInvoiceModel.SoInvoiceItem();

            SkuMaster sku = getSku(bill.getCompanyCode(), item.getSkuNumber());
            BkCoaMappingModel.CoaItem matchedCoa = getMatchedCoa(sku.getSkuGroupCode(), mappingModel.getCoaJson());
            it.setItem_no(String.valueOf(++index))
                    .setModel(item.getSkuNumber())
                    // 02 传name 01 传描述
                    .setDescription(sku.getSkuName())
                    .setQty(item.getBillQty())
                    .setUnit_price(item.getUnitPrice())
                    .setTotal(item.getBillQty().multiply(item.getUnitPrice()))

                    .setDr_cr("cr")
                    .setDebit_coa_id(Integer.valueOf(null != matchedCoa ? matchedCoa.getCoaId() : mappingModel.getCoaId()))
                    .setDebit_coa_code(null != matchedCoa ? matchedCoa.getCoaCode() : mappingModel.getCoaCode())
                    .setDebit_coa_name(null != matchedCoa ? matchedCoa.getCoaName() : mappingModel.getCoaName())
                    .setCredit_coa_id(null)
                    .setCredit_coa_code("")
                    .setCredit_coa_name("");

                    /*.setExpenseAccount(table.getAccountName())
                    .setExpenseAccountId(table.getAccountId())
                    .setBankAccount(null)*/
            items.add(it);
        }
        model.setItems(items);

        return model;
    }

    private BkCoaMappingModel.CoaItem getMatchedCoa(String skuGroupCode, List<BkCoaMappingModel.CoaItem> coaJson) {
        if (StringUtils.isNull(coaJson)) return null;
        for (BkCoaMappingModel.CoaItem coaItem : coaJson) {
            if (coaItem.getSkuGroup().equals(skuGroupCode)) {
                return coaItem;
            }
        }
        return null;
    }

    private SkuMaster getSku(String companyCode, String skuNumber) {
        return skuService.getSku(skuNumber, null, companyCode);
    }

    private Address getAddress(String companyCode, String type, String subType, String key) {
        AddressQueryVo vo = new AddressQueryVo();
        vo.setCompanyCode(companyCode);
        vo.setType(type);
        vo.setSubType(subType);
        vo.setKey(key);
        return addressService.getAddress(vo).get(0);
    }

    /**
     * 根据公司编码和客户编码
     *
     * @param companyCode
     * @param partid
     * @return
     */
    private BusinessPartner getBusinessPartner(String companyCode, String partid) {
        return bpService.getBpNameByBpNumber(companyCode,partid);
    }

    /**
     * 根据公司编码查询公司信息
     *
     * @param companyCode
     * @return
     */
    private Company getCompany(String companyCode) {
        return companyService.getCompany(companyCode);
    }


    private String getBillNumber(int i, Integer billType, String companyCode, ThreadLocal<String> threadLocal) {
        LambdaQueryWrapper<SoBillHeader> soBillHeaderLambdaQueryWrapper = new LambdaQueryWrapper<SoBillHeader>().
                eq(SoBillHeader::getCompanyCode, companyCode).
                orderByDesc(SoBillHeader::getId).
                last("limit 1");
        SoBillHeader billInfo = soBillHeaderMapper.selectOne(soBillHeaderLambdaQueryWrapper);
        String billNumber = "6000000000";
        if (ModuleConstant.BILL_TYPE.MERGE_BILL == billType) {
            if (i <= 0) {
                if (billInfo != null) {
                    billNumber = BigDecimal.ONE.add(new BigDecimal(billInfo.getBillingNumber())).toString();
                    threadLocal.set(billNumber);
                }
            } else {
//                billNumber = billInfo.getBillingNumber();
                billNumber = threadLocal.get();
            }
        } else {
            if (billInfo != null) {
                billNumber = BigDecimal.ONE.add(new BigDecimal(billInfo.getBillingNumber())).toString();
            }
        }

        return billNumber;
    }


    @Transactional(rollbackFor = Exception.class)
    public SoBillResp getSoBillHeader(String soNumber, String companyCode) {

        LambdaQueryWrapper<SoHeader> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SoHeader::getSoNumber, soNumber);
        queryWrapper.eq(SoHeader::getCompanyCode, companyCode);
        SoHeader soHeader = soHeaderMapper.selectOne(queryWrapper);

        SoBillResp soBillResp = new SoBillResp();
        soBillResp.setBillingStatus(soHeader.getBillingStatus());
        soBillResp.setTotalAmount(soHeader.getNetAmount());

        BigDecimal billeds = BigDecimal.ZERO;
//        if (ModuleConstant.SOHEADER_ORDER_TYPE.SERVICE_SO.equals(soHeader.getOrderType())) {
//            QueryWrapper<SoBillHeader> soBillHeaderQueryWrapper = new QueryWrapper<>();
//            soBillHeaderQueryWrapper.select("gross_amount");
//            soBillHeaderQueryWrapper.eq("reference_doc", soNumber);
//            soBillHeaderQueryWrapper.eq("company_code", companyCode);
//            SoBillHeader soBillHeader = soBillHeaderMapper.selectOne(soBillHeaderQueryWrapper);
//            if (soBillHeader != null) {
//                billeds = billeds.add(soBillHeader.getGrossAmount());
//            }
//        } else {
        // 得到deliveryNumber
        List<DeliveryHeader> deliveryInfo = deliveryHeaderMapper.getDeliveryNumber(soNumber, companyCode);
        if (deliveryInfo.size() > 0) {
            for (int i = 0; i < deliveryInfo.size(); i++) {
                DeliveryHeader deliveryHeader = deliveryInfo.get(i);
                QueryWrapper<SoBillHeader> soBillHeaderQueryWrapper = new QueryWrapper<>();
                soBillHeaderQueryWrapper.select("net_amount");
                soBillHeaderQueryWrapper.eq("reference_doc", deliveryHeader.getDeliveryNumber());
                soBillHeaderQueryWrapper.eq("company_code", deliveryHeader.getCompanyCode());
                SoBillHeader soBillHeader = soBillHeaderMapper.selectOne(soBillHeaderQueryWrapper);
                billeds = billeds.add(soBillHeader.getNetAmount());
            }
        }
//        }
        soBillResp.setBilled(billeds);
        if (billeds.compareTo(BigDecimal.ZERO) == 0) {
            soBillResp.setBilledPercentage(billeds);
        } else {
            soBillResp.setBilledPercentage(soBillResp.getBilled().divide(soHeader.getNetAmount(), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)));
        }
        return soBillResp;
    }

    public SystemConnect getConnect(String companyCode) {
        SystemConnect connect = new SystemConnect();
        connect.setCompanyCodeEx(Long.parseLong(companyCode));
        connect.setExSystem("bk");
        List<SystemConnect> connects = systemConnectService.selectSyctemConectList(connect);
        if (connects == null || connects.isEmpty()) {
            throw new RuntimeException("获取TOken失败，" + companyCode + " 未查到bk账号信息。");
        }
        return connects.get(0);
    }
}
