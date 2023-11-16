package com.inossem.oms.base.svc.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class SoHeaderSearchForm {
    @NotBlank(message = "company code cannot be empty")
    private String companyCode;
    private String searchText;
    // INSO SESO DSSO
    private List<String> orderType;
    // NEW CANL
    private List<String> orderStatus;
    // UNFL PRFL FUFL
    private List<String> deliveryStatus;
    // UNVI PRVI FUIN
    private List<String> invoiceStatus;

    private Date orderDateStart;
    private Date orderDateEnd;

    private BigDecimal grossAmountStart;
    private BigDecimal grossAmountEnd;
    private BigDecimal netAmountStart;
    private BigDecimal netAmountEnd;

    private List<String> currencyCode;
    private List<String> channelIds;
    private List<String> paymentTerm;
    private List<String> isDeliveryBlock;
    private List<String> isBillingBlock;

    private List<String> createBy;
    private List<String> modifiedBy;

    @Pattern(regexp = "^soNumber|netAmount|grossAmount|orderDate|deliveryDate|bpCustomer|createBy|modifiedBy|gmtCreate|gmtModified$",
            message = "order by should within soNumber,netAmount,grossAmount,orderDate,deliveryDate,bpCustomer,createBy,modifiedBy,gmtCreate,gmtModified")
    private String orderBy;
    private Boolean isAsc = true;

}
