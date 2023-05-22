package com.inossem.oms.base.svc.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@ApiModel("校验库存余量结果子实体")
@AllArgsConstructor
@NoArgsConstructor
public class CheckStockBalanceResSubVo {

    @ApiModelProperty(value = "余量是否充足",name = "warehouseCode")
    private boolean isAdequate;

    @ApiModelProperty(value = "商品取用数量",name = "userQty")
    private BigDecimal useQty;

    @ApiModelProperty(value = "商品编号",name = "skuNumber")
    private String skuNumber;

    @ApiModelProperty(value = "商品可用数量",name = "userQty")
    private BigDecimal availableQty;

    @ApiModelProperty(value = "移动平均价",name = "averagePrice")
    private BigDecimal averagePrice;

}
