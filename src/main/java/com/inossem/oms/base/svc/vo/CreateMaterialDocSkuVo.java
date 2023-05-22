package com.inossem.oms.base.svc.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@ApiModel("创建物料凭证的Sku列表参数")
public class CreateMaterialDocSkuVo {

    @ApiModelProperty(value = "商品编号",name = "skuNumber")
    @Length(max = 45,message = "skuNumber Max Length is 45")
    @NotBlank(message = "skuNumber cannot be empty")
    private String skuNumber;

    @ApiModelProperty(value = "商品数量",name = "skuQty")
    @DecimalMin(value = "0",message = "min skuQty is 0")
    private BigDecimal skuQty;

    @ApiModelProperty(value = "基础单位",name = "basicUom")
    private String basicUom;

    //支持两种模式，传入总价或是单价
    @ApiModelProperty(value = "总价",name = "totalAmount")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "单价",name = "itemAmount")
    private BigDecimal itemAmount;

    @ApiModelProperty(value = "货币编号",name = "currencyCode")
    private String currencyCode;

    @ApiModelProperty(value = "索引编号",name = "referenceNumber")
    private String referenceNumber;

    @ApiModelProperty(value = "索引行",name = "referenceItem")
    private String referenceItem;
}
