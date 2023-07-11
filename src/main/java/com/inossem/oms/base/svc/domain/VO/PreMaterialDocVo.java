package com.inossem.oms.base.svc.domain.VO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PreMaterialDocVo {
    private String movementType;
    private String stoNumber;
    private String companyCode;
    private String warehouseCode;
    private String toWarehouseCode;
    private BigDecimal averagePrice;
    private String currencyCode;
    private String skuNumber;
    private BigDecimal skuQty;
    private String basicUom;
    private String referenceType;
    private String referenceNumber;
    private String referenceItem;
    private String stockStatus;
}
