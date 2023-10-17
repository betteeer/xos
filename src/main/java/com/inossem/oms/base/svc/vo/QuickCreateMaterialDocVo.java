package com.inossem.oms.base.svc.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class QuickCreateMaterialDocVo {

    private String companyCode;

    private String warehouseCode;

    private Date postingDate;

    private String skuNumber;

    private BigDecimal skuQty;

    private String transactionType;

}
