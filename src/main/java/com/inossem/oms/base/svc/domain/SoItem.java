package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.inossem.oms.base.svc.domain.VO.TaxCalculateResp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 销售订单行项目 对象
 *
 * @author shigf
 * @date 2022-10-17
 */
@Data
@TableName("so_item")
@ApiModel("SoItem")
@AllArgsConstructor
@NoArgsConstructor
public class SoItem {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键", name = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司代码", name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "订单号", name = "soNumber")
    private String soNumber;

    @ApiModelProperty(value = "SO的行项目号", name = "soItem")
    private String soItem;

    @ApiModelProperty(value = "sku Number", name = "skuNumber")
    private String skuNumber;

    @ApiModelProperty(value = "是否是kitting物料", name = "isKitting")
    private Integer isKitting;

    @ApiModelProperty(value = "kittingSku", name = "kittingSku")
    private String kittingSku;

    @ApiModelProperty(value = "skuVersion", name = "skuVersion")
    private String skuVersion;

    /**
     * Inventory sku/service sku
     */
    @ApiModelProperty(value = "sku类型", name = "skuType")
    private String skuType;

    @ApiModelProperty(value = "仓库编码", name = "warehouseCode")
    private String warehouseCode;

    @ApiModelProperty(value = "销售单位的数量", name = "salesQty")
    private BigDecimal salesQty;

    @ApiModelProperty(value = "shippedQTY", name = "shippedQTY")
    @TableField(exist = false)
    private BigDecimal shippedQTY = BigDecimal.ZERO;

    @ApiModelProperty(value = "销售单位", name = "salesUom")
    private String salesUom;

    @ApiModelProperty(value = "基本单位的数量", name = "basicSalesQty")
    private BigDecimal basicQty;

    @ApiModelProperty(value = "基本单位", name = "basicSalesUom")
    private String basicUom;

    @ApiModelProperty(value = "单价", name = "unitPrice")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "netValue", name = "netValue")
    private BigDecimal netValue;

    @ApiModelProperty(value = "税", name = "taxExmpt")
    private Integer taxExmpt;

    @ApiModelProperty(value = "货币", name = "currencyCode")
    private String currencyCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "gmt的SO创建时间", name = "gmtCreate")
    private Date gmtCreate;

    @ApiModelProperty(value = "被哪个用户创建", name = "createBy")
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "gmt的SO修改时间", name = "gmtModified")
    private Date gmtModified;

    @ApiModelProperty(value = "被哪个用户修改", name = "modifiedBy")
    private String modifiedBy;

    /**
     * 是否被删除  1-deleted
     */
    @ApiModelProperty(value = "是否被删除", name = "isDeleted")
    private Integer isDeleted;

    @ApiModelProperty(value = "skuName",name = "skuName")
    @TableField(exist = false)
    private String skuName;

    @ApiModelProperty(value = "KittingItems",name = "KittingItems")
    @TableField(exist = false)
    private List<SkuKitting> KittingItems;

    @TableField(exist = false)
    private TaxCalculateResp taxCalculateResp;

    /**
     * 用于开票list中的 netAmount
     */
    @ApiModelProperty(value = "netAmount",name = "netAmount")
    @TableField(exist = false)
    private BigDecimal netAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "skuDescription",name = "skuDescription")
    @TableField(exist = false)
    private String skuDescription;


}
