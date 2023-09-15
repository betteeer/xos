package com.inossem.oms.svc.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.inossem.oms.api.bk.api.BkCoaMappingService;
import com.inossem.oms.api.bk.api.BookKeepingService;
import com.inossem.oms.api.bk.api.ConnectionUtils;
import com.inossem.oms.api.bk.model.BkCoaMappingModel;
import com.inossem.oms.api.bk.model.PoInvoiceModel;
import com.inossem.oms.api.bk.model.TaxContentBuilder;
import com.inossem.oms.base.common.constant.ModuleConstant;
import com.inossem.oms.base.svc.domain.*;
import com.inossem.oms.base.svc.domain.DTO.PoInvoiceHeaderFormDTO;
import com.inossem.oms.base.svc.domain.VO.AddressQueryVo;
import com.inossem.oms.base.svc.mapper.CurrencyExchangeMapper;
import com.inossem.oms.base.svc.mapper.PoHeaderMapper;
import com.inossem.oms.base.svc.mapper.PoInvoiceHeaderMapper;
import com.inossem.oms.base.svc.mapper.PoInvoiceItemMapper;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2022-12-09
 */
@Service
@Slf4j
public class PoInvoiceHeaderService {

    @Resource
    private BookKeepingService bookKeepingService;
    @Resource
    private SkuService skuService;
    @Resource
    private CompanyService companyService;
    @Resource
    private AddressService addressService;
    @Resource
    private BpService bpService;

    @Resource
    private CurrencyExchangeMapper currencyExchangeMapper;
    @Resource
    private PoInvoiceHeaderMapper poInvoiceHeaderMapper;

    @Resource
    private PoInvoiceItemMapper poInvoiceItemMapper;

    @Resource
    private PoHeaderMapper poHeaderMapper;

    @Resource
    private ConditionTableService conditionTableService;

    @Resource
    private BkCoaMappingService bkCoaMappingService;

    /**
     * 新增【请填写功能名称】
     *
     * @param poInvoiceHeader 【请填写功能名称】
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int insertPoInvoiceHeader(PoInvoiceHeader poInvoiceHeader) {
        String companyCode = poInvoiceHeader.getCompanyCode();
        List<PoInvoiceItem> poInvoiceItemList = new ArrayList<>();
        // 生成billNumber
        String billNumber = buildBillNumber(companyCode);
        try {
            poInvoiceHeader.setInvoiceNumber(billNumber);
            Date date = new Date();
            poInvoiceHeader.setGmtCreate(date);
            poInvoiceHeader.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
            poInvoiceHeader.setGmtModified(date);
            poInvoiceHeader.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
            poInvoiceHeaderMapper.insert(poInvoiceHeader);
            log.info("po invoice header save success .. ");

            //poInvoiceItem 参数封装
            poInvoiceItemList = poInvoiceHeader.getPoInvoiceItemList();
            for (PoInvoiceItem item : poInvoiceItemList) {
                item.setInvoiceingNumber(billNumber);
                item.setGmtCreate(date);
                item.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                item.setGmtModified(date);
                item.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
                poInvoiceItemMapper.insert(item);

            }

            //更新po中的开票状态
            PoHeader poHeader = new PoHeader();
            poHeader.setInvoiceStatus(ModuleConstant.SOPO_BILLIING_STATUS.FULLY_INVOICED);
            LambdaQueryWrapper<PoHeader> poHeaderQueryWrapper = new LambdaQueryWrapper<>();
            poHeaderQueryWrapper.eq(PoHeader::getPoNumber, poInvoiceHeader.getReferenceDoc());
            poHeaderQueryWrapper.eq(PoHeader::getCompanyCode, poInvoiceHeader.getCompanyCode());
            poHeaderMapper.update(poHeader, poHeaderQueryWrapper);
        } catch (Exception e) {
            throw new RuntimeException("create Po invoice error", e);
        }
        try {
            if ("USD".equals(poInvoiceHeader.getCurrencyCode())) {
                CurrencyExchange exchangeRate = currencyExchangeMapper.selectOne(new LambdaQueryWrapper<CurrencyExchange>()
                        .eq(CurrencyExchange::getCurrencyFr, poInvoiceHeader.getCurrencyCode()));
                if (exchangeRate != null) {
                    poInvoiceHeader.setExchangeRate(exchangeRate.getRate());
                } else {
                    throw new RuntimeException("po invoice get exchange rate error");
                }
            }
            // 同步bk.
            Boolean activeAp = ConnectionUtils.getConnection(companyCode).getActiveAp();
            String s = "";
            if (activeAp) {
                s = syncToBk(poInvoiceHeader, poInvoiceItemList);
            }
            LambdaQueryWrapper<PoInvoiceHeader> poInvoiceHeaderLambdaQueryWrapper = new LambdaQueryWrapper<PoInvoiceHeader>()
                    .eq(PoInvoiceHeader::getInvoiceNumber, billNumber)
                    .eq(PoInvoiceHeader::getCompanyCode, companyCode);
            PoInvoiceHeader poInvoiceHeader1 = new PoInvoiceHeader();
            poInvoiceHeader1.setAccountingDoc(s);
            poInvoiceHeaderMapper.update(poInvoiceHeader, poInvoiceHeaderLambdaQueryWrapper);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return 1;
    }

    private String syncToBk(PoInvoiceHeader poInvoiceHeader, List<PoInvoiceItem> poInvoiceItemList) {
        try {
            PoInvoiceModel model = getModel(poInvoiceHeader, poInvoiceItemList);
            return bookKeepingService.poBill(model);
        } catch (Exception e) {
            // 先保证业务不受影响，临时不关注异常
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private PoInvoiceModel getModel(PoInvoiceHeader poInvoiceHeader, List<PoInvoiceItem> poInvoiceItemList) throws IOException {


        // 查开票的公司信息
        Company company = getCompany(poInvoiceHeader.getCompanyCode());

        // 客户
        BusinessPartner bp = getBusinessPartner(poInvoiceHeader.getCompanyCode(), poInvoiceHeader.getPartnerId());

        PoInvoiceModel model = new PoInvoiceModel()
                .setFile_url(poInvoiceHeader.getInvoiceTicketUrl())
                .setBr_type("1")
                .setInvoice_comments("")
                .setExchange_rate(poInvoiceHeader.getExchangeRate())
                .setTotal_fee_local(poInvoiceHeader.getNetAmount())
                .setTotal_fee(poInvoiceHeader.getNetAmount())
                .setTotal_tax(poInvoiceHeader.getNetAmount().subtract(poInvoiceHeader.getGrossAmount()))
//                .setGst(poInvoiceHeader.getGstAmount())
//                .setQst(poInvoiceHeader.getQstAmount())
//                .setPst(poInvoiceHeader.getPstAmount())
                .setNet_amount(poInvoiceHeader.getGrossAmount())
                .setInvoice_create_date(DateUtil.formatDate(poInvoiceHeader.getGmtCreate()))
                .setInvoice_due_date(DateUtil.formatDate(poInvoiceHeader.getGmtCreate()))
                .setPosting_date(DateUtil.formatDate(poInvoiceHeader.getPostingDate()))
                .setPay_method("1")
                .setInvoice_currency(poInvoiceHeader.getCurrencyCode())
//                .setInvoice_currency(SoBillHeaderService.CURRENCY_MAPPING.get(poInvoiceHeader.getCurrencyCode()))
                .setReference_no(poInvoiceHeader.getInvoiceNumber())
                .setIssuer_tel(bp.getBpTel())
                .setIssuer_email(bp.getBpEmail())
                .setCompany_id(company.getOrgidEx())
                // bk 的companyCode
                .setCompany_code(company.getCompanyCodeEx())
                //bp number
                .setIssuer_id(bp.getBkBpNumberCustomer())

                .setIssuer_address(null)

                .setIssuer_name(bp.getBpName())
                .setTax_content(TaxContentBuilder.build(poInvoiceHeader.getGstAmount(), BigDecimal.valueOf(0), poInvoiceHeader.getPstAmount(), poInvoiceHeader.getQstAmount()));



               /* //.setSupplierId(bp.getBkBpNumberVendor())
               // .setCompanyName(bp.getBpName())

                // 暂时传空
                .setCompanyAddress("")
                .setCompanyPhone("")
                .setCompanyEmail(company.getCompanyEmail())
                .setCompanyLogo(company.getLogoUrl())

                // 开票方公司GST NO
                .setCompanyGstNo("")
                // 开票方公司PST NO
                .setCompanyPstNo("")

                // 固定传空
                .setGst(null)
                .setPst(null)

                // 开票业务编码
                .setReferenceNo(poInvoiceHeader.getInvoiceNumber())
                .setInvoiceCurrency(SoBillHeaderService.CURRENCY_MAPPING.get(poInvoiceHeader.getCurrencyCode()))
                // 支付方式
                .setPayMethod("1")

                .setInvoiceCreateDate(DateUtil.formatDate(poInvoiceHeader.getGmtCreate()))
                .setInvoiceDueDate(DateUtil.formatDate(poInvoiceHeader.getGmtCreate()))
                .setPostingDate(DateUtil.formatDate(poInvoiceHeader.getPostingDate()))

                // 总金额
                .setAmount(poInvoiceHeader.getGrossAmount())

                // 固定为null
                .setShipping(null)
                .setDiscount(null)

                // 税前总计
                .setTotalTaxable(poInvoiceHeader.getGrossAmount())

                .setTps(poInvoiceHeader.getGstAmount())
                .setTvq(poInvoiceHeader.getQstAmount())
                .setTvp(poInvoiceHeader.getPstAmount())

                .setTotalTax(poInvoiceHeader.getNetAmount().subtract(poInvoiceHeader.getGrossAmount()))
                .setTotalFee(poInvoiceHeader.getNetAmount())

                .setTotalFeeExchangeCAD(poInvoiceHeader.getNetAmount().multiply(BigDecimal.ONE))

//       如果是 usd 会调用 bk的税率接口 ，拿到税率 做换算
                .setExchangeRate(BigDecimal.ONE)
                .setDeposit(null)
                .setInvoiceComments("")

                .setFileId(null)
                .setFilePageIndex(null)
                .setFileUrl(null)
                .setPo("")
                .setPaymentTermsDay1("paymentTermsDay1")
                .setPaymentTermsDay2("paymentTermsDay2")
                .setPaymentTermsDay3("paymentTermsDay3")
                .setPaymentTermsDiscount1("paymentTermsDiscount1")
                .setPaymentTermsDiscount2("paymentTermsDiscount2")
                .setPaymentTermsDiscount3("paymentTermsDiscount3");*/

        // 根据公司信息去查，暂时固定
//        ConditionTable conditionTable = new ConditionTable();
//        conditionTable.setCompanyCode(company.getCompanyCode());
//        conditionTable.setConditionType("P001");
//        List<ConditionTable> tables = conditionTableService.selectConditionTableList(conditionTable);
//        ConditionTable table = null;
//        if (tables == null || tables.isEmpty()) {
//            table = new ConditionTable();
//        } else {
//            table = tables.get(0);
//        }
        BkCoaMappingModel mappingModel = bkCoaMappingService.getOrderTypeMapping(company.getCompanyCode(), "P001");
        if (null == mappingModel) {
            mappingModel = new BkCoaMappingModel();
        }
        List<PoInvoiceModel.PoInvoiceItem> items = new ArrayList<>();
        int index = 0;
        for (PoInvoiceItem item : poInvoiceItemList) {
            PoInvoiceModel.PoInvoiceItem it = new PoInvoiceModel.PoInvoiceItem();

            SkuMaster sku = getSku(poInvoiceHeader.getCompanyCode(), item.getSkuNumber());
            BkCoaMappingModel.CoaItem matchedCoa = getMatchedCoa(sku.getSkuGroupCode(), mappingModel.getCoaJson());
            it.setItem_no(String.valueOf(++index))
                    .setModel(item.getSkuNumber())
                    .setDescription(sku.getSkuName())
                    .setQty(item.getInvoiceQty())
                    //.setUom(item.getInvoiceUom())
                    .setType("")
                    //默认填写dr
                    .setDr_cr("dr")
                    .setUnit_price(item.getUnitPrice())
                    .setTotal(item.getInvoiceQty().multiply(item.getUnitPrice()))

                    .setCredit_coa_name(null != matchedCoa ? matchedCoa.getDebitCoaName() : mappingModel.getDebitCoaName())
                    .setCredit_coa_id(null != matchedCoa ? matchedCoa.getDebitCoaId() : mappingModel.getDebitCoaId())
                    .setCredit_coa_code(null != matchedCoa ? matchedCoa.getDebitCoaCode() : mappingModel.getDebitCoaCode())
            // 根据公司信息去查，暂时固定
            //.setExpenseAccount(table.getAccountName())
            //.setExpenseAccountId(table.getAccountId())
            //.setBankAccount(null)
            ;
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
        return bpService.getBpNameByBpNumber(companyCode, partid);
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


    private String buildBillNumber(String companyCode) {
        LambdaQueryWrapper<PoInvoiceHeader> soBillHeaderLambdaQueryWrapper = new LambdaQueryWrapper<PoInvoiceHeader>().
                eq(PoInvoiceHeader::getCompanyCode, companyCode).
                orderByDesc(PoInvoiceHeader::getId).
                last("limit 1");
        PoInvoiceHeader billInfo = poInvoiceHeaderMapper.selectOne(soBillHeaderLambdaQueryWrapper);
        String billNumber = "6000000000";
        if (billInfo != null) {
            billNumber = BigDecimal.ONE.add(new BigDecimal(billInfo.getInvoiceNumber())).toString();
        }

        log.info("生成的invoice number为:{}", billNumber);

        return billNumber;

    }

    public List<PoInvoiceHeader> getList(PoInvoiceHeaderFormDTO form) {
        log.info(">>>查询列表，入参：[{}]", form);
        MPJLambdaWrapper<PoInvoiceHeader> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(PoInvoiceHeader.class);
        // 指定 company code数据范围
        wrapper.eq(PoInvoiceHeader::getCompanyCode, form.getCompanyCode());
        // 查询 status
        wrapper.in(StringUtils.isNotEmpty(form.getCurrencyCode()), PoInvoiceHeader::getCurrencyCode, form.getCurrencyCode());
        wrapper.between(StringUtils.isNotNull(form.getPostingDateStart()), PoInvoiceHeader::getPostingDate, form.getPostingDateStart(), form.getPostingDateEnd());
        wrapper.between(StringUtils.isNotNull(form.getNetAmountStart()), PoInvoiceHeader::getNetAmount, form.getNetAmountStart(), form.getNetAmountEnd());
        wrapper.between(StringUtils.isNotNull(form.getGrossAmountStart()), PoInvoiceHeader::getGrossAmount, form.getGrossAmountStart(), form.getGrossAmountEnd());

        // 关键字搜索+ 查bpName
        wrapper.leftJoin(PoHeader.class, PoHeader::getPoNumber, PoInvoiceHeader::getReferenceDoc, ext ->{
            ext.nested(StringUtils.isNotEmpty(form.getSearchText()), i -> {
                i.like(PoInvoiceHeader::getInvoiceNumber, form.getSearchText())
                        .or().like(PoInvoiceHeader::getAccountingDoc, form.getSearchText())
                        .or().like(PoHeader::getBpName, form.getSearchText());
            });
            return  ext.selectAs(PoHeader::getBpName, PoInvoiceHeader::getBpName);
        });
        //查询deliveryNumber
        wrapper.leftJoin(DeliveryHeader.class, DeliveryHeader::getBillingNumber, PoInvoiceHeader::getInvoiceNumber,
                ext -> ext.selectAs(DeliveryHeader::getDeliveryNumber, PoInvoiceHeader::getDeliveryNumber));

        // 拼接order by
        wrapper.orderBy(StringUtils.isNotNull(form.getOrderBy()), form.getIsAsc(), StringUtils.toUnderScoreCase(form.getOrderBy()));
        // 排序的字段值相同则按照id倒序
        wrapper.orderBy(true, false, PoInvoiceHeader::getId);
        List<PoInvoiceHeader> poInvoiceHeaders = poInvoiceHeaderMapper.selectJoinList(PoInvoiceHeader.class, wrapper);
        return poInvoiceHeaders;
    }


//    /**
//     * 查询【请填写功能名称】
//     *
//     * @param id 【请填写功能名称】主键
//     * @return 【请填写功能名称】
//     */
//    public PoInvoiceHeader selectPoInvoiceHeaderById(Long id) {
//        return poInvoiceHeaderMapper.selectPoInvoiceHeaderById(id);
//    }
//
//    /**
//     * 查询【请填写功能名称】列表
//     *
//     * @param poInvoiceHeader 【请填写功能名称】
//     * @return 【请填写功能名称】
//     */
//    public List<PoInvoiceHeader> selectPoInvoiceHeaderList(PoInvoiceHeader poInvoiceHeader) {
//        return poInvoiceHeaderMapper.selectPoInvoiceHeaderList(poInvoiceHeader);
//    }
//
//
//    /**
//     * 修改【请填写功能名称】
//     *
//     * @param poInvoiceHeader 【请填写功能名称】
//     * @return 结果
//     */
//    public int updatePoInvoiceHeader(PoInvoiceHeader poInvoiceHeader) {
//        return poInvoiceHeaderMapper.updatePoInvoiceHeader(poInvoiceHeader);
//    }
//
//    /**
//     * 批量删除【请填写功能名称】
//     *
//     * @param ids 需要删除的【请填写功能名称】主键
//     * @return 结果
//     */
//    public int deletePoInvoiceHeaderByIds(Long[] ids) {
//        return poInvoiceHeaderMapper.deletePoInvoiceHeaderByIds(ids);
//    }
//
//    /**
//     * 删除【请填写功能名称】信息
//     *
//     * @param id 【请填写功能名称】主键
//     * @return 结果
//     */
//    public int deletePoInvoiceHeaderById(Long id) {
//        return poInvoiceHeaderMapper.deletePoInvoiceHeaderById(id);
//    }
}
