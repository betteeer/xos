package com.inossem.oms.base.svc.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
public class RemoteBkGl {
    private String orgId;

    private String companyCode;

    private String description;

    private String createDate;

    private String postingDate;

    private String currency;

    private BigDecimal totalDebit;

    private String totalDebitExchangeCAD;

    private BigDecimal totalCredit;

    private String totalCreditExchangeCAD;

    private String exchangeRate;

    private List<RemoteBkGlSubList> itemList;
}
