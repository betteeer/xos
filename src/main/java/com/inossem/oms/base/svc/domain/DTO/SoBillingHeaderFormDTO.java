package com.inossem.oms.base.svc.domain.DTO;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class SoBillingHeaderFormDTO {
    @NotBlank(message = "company code cannot be empty")
    private String companyCode;
    private String searchText;

    private Date postingDateStart;
    private Date postingDateEnd;

    private BigDecimal netAmountStart;
    private BigDecimal netAmountEnd;
    private BigDecimal grossAmountStart;
    private BigDecimal grossAmountEnd;
    private List<String> currencyCode;

    @Pattern(regexp = "^billingNumber|postingDate|netAmount|grossAmount$", message = "order by should within billingNumber,postingDate,netAmount,grossAmount")
    private String orderBy;
    private Boolean isAsc = true;
}
