package com.inossem.oms.base.svc.domain.VO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inossem.oms.base.svc.domain.SkuBp;
import com.inossem.oms.base.svc.domain.SkuCharacter;
import com.inossem.oms.base.svc.domain.SkuKitting;
import com.inossem.oms.base.svc.domain.SkuTariff;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zoutong
 * @date 2022/10/15
 **/
@Data
@ApiModel("sku")
public class SkuVO {

    private static final long serialVersionUID = 1L;

    private Long id;

    @ApiModelProperty(value = "CompanyCode", name = "CompanyCode")
    private String CompanyCode;

    @ApiModelProperty(value = "SKU Name", name = "SKU Name")
    private String SkuName;

    @ApiModelProperty(value = "skuNumberEx", name = "skuNumberEx")
    private String skuNumberEx;

    @ApiModelProperty(value = "Characters",name = "Characters")
    @JsonProperty("chList")
    private List<SkuCharacter> Characters;

    /*@NotEmpty(message = "Default UOM Not Empty")
    @ApiModelProperty(value = "Default UOM", example = "Default UOM")
    private String DefaultUom;   //TODO 数据库没有*/

    @ApiModelProperty(value = "Description",name = "Description")
    private String SkuDescription;

    @ApiModelProperty(value = "SkuType",name = "SkuType")
    private String SkuType;

    @ApiModelProperty(value = "Upc",name = "Upc")
    @JsonProperty("upcNumber")
    private String Upc;

    @ApiModelProperty(value = "IsKitting",name = "IsKitting")
    private int IsKitting;

    @ApiModelProperty(value = "KittingItems",name = "KittingItems")
    private List<SkuKitting> KittingItems;

    @ApiModelProperty(value = "Weight",name = "Weight")
    private BigDecimal Weight;

    @ApiModelProperty(value = "weightUom",name = "weightUom")
    private String weightUom;

    @ApiModelProperty(value = "width",name = "width")
    private BigDecimal Width;

    @ApiModelProperty(value = "height",name = "height")
    private BigDecimal Height;

    @ApiModelProperty(value = "length",name = "length")
    private BigDecimal Length;

    @ApiModelProperty(value = "WhlUom",name = "WhlUom")
    private String WhlUom;

    @ApiModelProperty(value = "BasicUom",name = "BasicUom")
    private String BasicUom;

    @ApiModelProperty(value = "SalesPrice",name = "SalesPrice")
    private BigDecimal SalesPrice;

    @ApiModelProperty(value = "SalesUom",name = "SalesUom")
    private String SalesUom;

    @ApiModelProperty(value = "SalesBasicRate",name = "SalesBasicRate")
    private BigDecimal SalesBasicRate;

    @ApiModelProperty(value = "PurchaseUom",name = "purchase_uom")
    private String PurchaseUom;

    @ApiModelProperty(value = "PurchaseBasicRate",name = "purchase_basic_rate")
    private BigDecimal PurchaseBasicRate;

    @ApiModelProperty(value = "BPDetails",name = "BPDetails")
    @JsonProperty("BPDetails")
    private List<SkuBp> BPDetails;

    @ApiModelProperty(value = "FTradeDetails",name = "FTradeDetails")
    @JsonProperty("FTradeDetails")
    private List<SkuTariff> FTradeDetails;

    @ApiModelProperty(value = "pictureList",name = "pictureList")
    private List<String> pictureList;

    @ApiModelProperty(value = "the sku group id which the sku belongs to", name = "skuGroupCode")
    private String skuGroupCode;

    @ApiModelProperty(value = "the sku group name which the sku belongs to", name = "skuGroupName")
    private String skuGroupName;

    @ApiModelProperty(value = "the id of the sku owner", name = "skuOwnerCode")
    private String skuOwnerCode;

    @ApiModelProperty(value = "10 - Normal; 20 - Fast; 30 - Slow", name = "fsnIndicator")
    private String fsnIndicator;

    @ApiModelProperty(value = "refers to the config_storage_indicator table.", name = "storageIndicator")
    private String storageIndicator;

    @ApiModelProperty(value = "is hu traceable", name = "isHuTraceable")
    private Integer isHuTraceable;

    @ApiModelProperty(value = "the preserve description of the product", name = "preserveDescription")
    private String preserveDescription;

    @ApiModelProperty(value = "the dispose description of the product", name = "disposeDescription")
    private String disposeDescription;

    @ApiModelProperty(value = "isDelete", name = "isDelete")
    private String isDelete = "0";

    @ApiModelProperty(value = "skuUomConversionVoList", name = "skuUomConversionVoList")
    private List<SkuUomConversionVo> skuUomConversionVoList;

    @ApiModelProperty(value = "skuNumber", name = "skuNumber")
    private String skuNumber = "";

    @ApiModelProperty(value = "skuSatetyStock", name = "skuSatetyStock")
    private BigDecimal skuSatetyStock = BigDecimal.ZERO;
}
