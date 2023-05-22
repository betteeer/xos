package com.inossem.oms.base.svc.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class RemoteBkGlSubList {

    private String itemNo;

    private String description;

    private BigDecimal debit;

    private BigDecimal credit;

    private int expenseAccountId;

    private String expenseAccount;
}
