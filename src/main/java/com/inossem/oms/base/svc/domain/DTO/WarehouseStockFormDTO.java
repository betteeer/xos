package com.inossem.oms.base.svc.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class WarehouseStockFormDTO {
    @NotBlank(message = "company code cannot be empty ")
    private String companyCode;
    @NotEmpty(message = "warehouse code cannot be empty ")
    private List<String> warehouseCodes;
    @NotEmpty(message = "skuNumber cannot be empty ")
    private List<String> skuNumbers;
}
