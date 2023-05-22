package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.inossem.oms.base.common.annotation.DefaultValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author zoutong
 * @date 2022/10/15
 **/
@Data
@ApiModel("sku表")
@TableName("sku_master")
@AllArgsConstructor
@NoArgsConstructor
public class SkuMaster {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key", name = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司编号", name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "sku编号", name = "skuNumber")
    private String skuNumber;

    @ApiModelProperty(value = "sku外部编号", name = "skuNumberEx")
    private String skuNumberEx;

    @ApiModelProperty(value = "upc_number", name = "upcNumber")
    private String upcNumber;

    @ApiModelProperty(value = "sku名称", name = "skuName")
    private String skuName = "";

    @ApiModelProperty(value = "sku类型", name = "skuType")
    private String skuType;

    @ApiModelProperty(value = "基础单位", name = "basicUom")
    private String basicUom = "";

    @ApiModelProperty(value = "是否是组合商品", name = "isKitting")
    private int isKitting = 0;

    @ApiModelProperty(value = "sku描述", name = "skuDescription")
    private String skuDescription;

    @ApiModelProperty(value = "宽度", name = "width")
    private BigDecimal width;

    @ApiModelProperty(value = "高度", name = "height")
    private BigDecimal height;

    @ApiModelProperty(value = "长度", name = "length")
    private BigDecimal length;

    @ApiModelProperty(value = "长宽高单位", name = "whlUom")
    private String whlUom = "";

    @ApiModelProperty(value = "总重量", name = "grossWeight")
    private BigDecimal grossWeight;

    @ApiModelProperty(value = "净重", name = "netWeight")
    private BigDecimal netWeight;

    @ApiModelProperty(value = "重量单位", name = "weightUom")
    private String weightUom = "";

    @ApiModelProperty(value = "总体积", name = "grossVolume")
    private BigDecimal grossVolume;

    @ApiModelProperty(value = "净体积", name = "netVolume")
    private BigDecimal netVolume;

    @ApiModelProperty(value = "体积", name = "volumeUom")
    private String volumeUom = "";

    @ApiModelProperty(value = "销售价格", name = "salesPrice")
    private BigDecimal salesPrice;

    @DefaultValue(value="123")
    @ApiModelProperty(value = "销售单位", name = "salesUom")
    private String salesUom = "";

    @ApiModelProperty(value = "销售基础转化关系", name = "salesBasicRate")
    private BigDecimal salesBasicRate;

    @ApiModelProperty(value = "购买单位", name = "purchaseUom")
    private String purchaseUom = "";

    @ApiModelProperty(value = "购买基础转化关系", name = "purchaseBasicRate")
    private BigDecimal purchaseBasicRate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间", name = "gmtCreate")
    private Date gmtCreate;

    @ApiModelProperty(value = "创建者", name = "createBy")
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间", name = "gmtModified")
    private Date gmtModified;

    @ApiModelProperty(value = "修改者", name = "modifiedBy")
    private String modifiedBy;

    @ApiModelProperty(value = "是否删除", name = "isDeleted")
    private int isDeleted = 0;

    @ApiModelProperty(value = "Characters", name = "Characters")
    @TableField(exist = false)
    @JsonProperty("chList")
    private List<SkuCharacter> Characters;

    @ApiModelProperty(value = "KittingItems", name = "KittingItems")
    @TableField(exist = false)
    private List<SkuKitting> KittingItems;

    @ApiModelProperty(value = "BPDetails", name = "BPDetails")
    @JsonProperty("BPDetails")
    @TableField(exist = false)
    private List<SkuBp> BPDetails;

    @ApiModelProperty(value = "FTradeDetails", name = "FTradeDetails")
    @JsonProperty("FTradeDetails")
    @TableField(exist = false)
    private List<SkuTariff> FTradeDetails;

    @ApiModelProperty(value = "pictureList", name = "pictureList")
    @TableField(exist = false)
    private List<PictureTable> pictureList;

    //    `sku_group_code` varchar(45) DEFAULT NULL COMMENT 'the sku group id which the sku belongs to',
//      `sku_owner_code` varchar(45) DEFAULT NULL COMMENT 'the id of the sku owner',
//      `fsn_indicator` varchar(10) DEFAULT NULL COMMENT '10 - Normal; 20 - Fast; 30 - Slow;',
//      `storage_indicator` varchar(10) DEFAULT NULL COMMENT 'refers to the config_storage_indicator table. e.g. 10 - bulk, 20 - heavy, etc.',
//      `is_hu_traceable` tinyint DEFAULT NULL,
//      `preserve_description` varchar(255) DEFAULT NULL COMMENT 'the perserve description of the product',
//      `dispose_description` varchar(255) DEFAULT NULL COMMENT 'the dispose description of the product',
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


    @ApiModelProperty(value = "default 0 if sku is for wms, 1", name = "wmsIndicator")
    private Integer wmsIndicator;

    @ApiModelProperty(value = "skuUomConversionList", name = "skuUomConversionList")
    @TableField(exist = false)
    private List<SkuUomConversion> skuUomConversionList;

    @ApiModelProperty(value = "skuSystemGenerated", name = "skuSystemGenerated")
    private Integer skuSystemGenerated;

    @ApiModelProperty(value = "skuSatetyStock", name = "skuSatetyStock")
    private BigDecimal skuSatetyStock;

    @ApiModelProperty(value = "isUpdate", name = "isUpdate")
    //sku是否可以修改  0-可  1-否
    @TableField(exist = false)
    private Integer isUpdate = 0;
}
