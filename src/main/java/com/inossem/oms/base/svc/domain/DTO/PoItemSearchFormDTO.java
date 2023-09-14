package com.inossem.oms.base.svc.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PoItemSearchFormDTO {
    @NotBlank(message = "company code cannot be empty")
    private String companyCode;
    private String searchText;

    private List<String> itemType;

    private List<String> warehouseCode;

    private BigDecimal unitPriceStart;
    private BigDecimal unitPriceEnd;

    private BigDecimal purchaseQtyStart;
    private BigDecimal purchaseQtyEnd;

    private List<String> currencyCode;
    private List<String> taxExmpt;

    @Pattern(regexp = "^poNumber|purchaseQty|basicQty|unitPrice|netValue$", message = "order by should within poNumber,purchaseQty,basicQty,unitPrice,netValue")
    private String orderBy;
    private Boolean isAsc = true;

}
