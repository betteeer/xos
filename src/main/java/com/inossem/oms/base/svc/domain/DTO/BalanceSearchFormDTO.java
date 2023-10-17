package com.inossem.oms.base.svc.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.List;

@Data
public class BalanceSearchFormDTO {

    @NotBlank(message = "company code cannot be empty")
    private String companyCode;
    private String searchText;

    // None Safety BelowSafety
    private List<String> safetyStock;

    private List<String> warehouse;

    private List<String> skuGroup;
    private BigDecimal totalQtyStart;
    private BigDecimal totalQtyEnd;
    private BigDecimal totalOnhandQtyStart;
    private BigDecimal totalOnhandQtyEnd;
    private BigDecimal totalBlockQtyStart;
    private BigDecimal totalBlockQtyEnd;
    private BigDecimal totalTransferQtyStart;
    private BigDecimal totalTransferQtyEnd;
    private BigDecimal averagePriceStart;
    private BigDecimal averagePriceEnd;
    @Pattern(regexp = "^skuNumber|totalAmount$", message = "order by should within skuNumber,totalAmount")
    private String orderBy;
    private Boolean isAsc = true;
}
