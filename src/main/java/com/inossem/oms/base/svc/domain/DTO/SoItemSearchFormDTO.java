package com.inossem.oms.base.svc.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SoItemSearchFormDTO {
    @NotBlank(message = "company code cannot be empty")
    private String companyCode;
    private String searchText;

    private List<String> itemType;

    private List<String> warehouseCode;

    private BigDecimal unitPriceStart;
    private BigDecimal unitPriceEnd;

    private BigDecimal salesQtyStart;
    private BigDecimal salesQtyEnd;

    private List<String> currencyCode;
    private List<String> taxExmpt;

    @Pattern(regexp = "^salesQty|basicQty|unitPrice|netValue$", message = "order by should within salesQty,basicQty,unitPrice,netValue")
    private String orderBy;
    private Boolean isAsc = true;

}
