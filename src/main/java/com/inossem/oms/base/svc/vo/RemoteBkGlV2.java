package com.inossem.oms.base.svc.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
/**
 * @description: 本类为 同步BK-V2版本物料凭证接口使用 @JSONField不可随意修改
 * @author guoh
 * @time 2023/3/31 19:32
 */
public class RemoteBkGlV2 {

    @JSONField(name = "company_id")
    private String companyId;

    @JSONField(name = "company_code")
    private String companyCode;

    @JSONField(name = "posting_date")
    private String postingDate;

    @JSONField(name = "header_text")
    private String headerText;

    @JSONField(name = "currency")
    private String currency;

    @JSONField(name = "exchange_rate")
    private Integer exchangeRate;

    private BigDecimal totalCredit;

    private BigDecimal totalDebit;

    private String totalCreditCAD;

    private String totalDebitCAD;

    private String createDate;

    @JSONField(name = "line_items")
    private List<RemoteBkGlSubListV2> lineItems;
}
