package com.inossem.oms.base.svc.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdfPoFormDTO {
    private String companyCode;
    private String currencyCode;
    private String orderNo;
    private String orderDate;
    private String referenceNumber;
    private String paymentTerm;
    private String from;
    private String shipTo;
    private String billTo;
    private String netAmount;
    private String gst_hstAmount;
    private String qstAmount;
    private String pstAmount;
    private String taxTotal;
    private String totalCad;
    private List<Sku> skus;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Sku {
        private String order;
        private String skuName;
        private String skuNumber;
        private String salesQty;
        private String unitPrice;
        private String amount;
        private String uom;
    }
}
