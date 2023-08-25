package com.inossem.oms.base.svc.domain.dashboard.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(description = "dashboard most on hand inventory返回实体类")
public class MostInventoryDto {

    private String skuNumber;
    private String skuName;
    private BigDecimal amount;
    private BigDecimal quantity;

}
