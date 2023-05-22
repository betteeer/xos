package com.inossem.oms.base.svc.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * used for orderHeaderResp params
 *
 * @author guoh
 * @date 2022-10-17
 */
@Data
@ApiModel("OrderItems")
public class OrderItems {

    @ApiModelProperty(value = "deliveryItemId", name = "deliveryItemId")
    private String deliveryItemId;

    @ApiModelProperty(value = "skuName", name = "skuName")
    private String skuName;

    @ApiModelProperty(value = "skuNumber", name = "skuNumber")
    private String skuNumber;

    @ApiModelProperty(value = "kittingSku", name = "kittingSku")
    private String kittingSku;

    @ApiModelProperty(value = "warehouseCode", name = "warehouseCode")
    private String warehouseCode;

    @ApiModelProperty(value = "openQTY", name = "openQTY")
    private BigDecimal openQTY;

    @ApiModelProperty(value = "shippedQTY", name = "shippedQTY")
    private BigDecimal shippedQTY;

    @ApiModelProperty(value = "soSkuItem", name = "soSkuItem")
    private String soSkuItem;
}
