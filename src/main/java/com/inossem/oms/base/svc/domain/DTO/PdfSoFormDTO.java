package com.inossem.oms.base.svc.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class PdfSoFormDTO {
    private String companyCode;
    private String currencyCode;
    private String orderNo;
    private String orderDate;
    private String referenceNumber;
    private String paymentTerm;
    private String to;
    private String shipTo;
    private String netAmount;
    private String gst_hstAmount;
    private String qstAmount;
    private String pstAmount;
    private String taxTotal;
    private String totalCad;
    private List<Sku> skus;
    @Data
    @AllArgsConstructor
    public static class Sku {
        private String order;
        private String skuName;
        private String skuNumber;
        private String salesQty;
        private String unitPrice;
        private String amount;
    }
}
