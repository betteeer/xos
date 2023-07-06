package com.inossem.oms.base.svc.domain.VO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SimpleStockBalanceVo {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String CompanyCode;

    private String warehouseCode;

    private String skuNumber;

    private BigDecimal totalOnhandQty;

    private BigDecimal totalBlockQty;

    private BigDecimal totalTransferQty;

    private BigDecimal totalQty;
}
