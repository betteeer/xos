package com.inossem.oms.base.svc.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class DeliveryItemFormDTO {
    @NotBlank(message = "company code cannot be empty")
    private String companyCode;
    private String searchText;

    private List<String> status;

    private List<String> warehouseCode;
    @Pattern(regexp = "^deliveryNumber|deliveryQty|deliveredQty|referenceDoc$", message = "order by should within deliveryNumber,deliveryQty,deliveredQty,referenceDoc")
    private String orderBy;
    private Boolean isAsc = true;

}
