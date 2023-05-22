package com.inossem.oms.base.svc.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@ApiModel("校验库存余量子实体")
@AllArgsConstructor
@NoArgsConstructor
public class CheckStockBalanceParamSubVo {

    @ApiModelProperty(value = "商品取用数量",name = "userQty")
    private BigDecimal useQty;

    @ApiModelProperty(value = "商品编号",name = "skuNumber")
    @NotBlank(message = "warehouseCode cannot be empty")
    private String skuNumber;
}
