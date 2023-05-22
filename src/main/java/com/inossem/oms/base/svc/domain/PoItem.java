package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 【请填写功能名称】对象 po_item
 * 
 * @author shigf11
 * @date 2022-11-04
 */
@Data
@TableName("po_item")
@ApiModel("PoItem")
public class PoItem
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键", name = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司代码", name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "订单号", name = "poNumber")
    private String poNumber;

    @ApiModelProperty(value = "PO的行项目号", name = "poItem")
    private String poItem;

    @ApiModelProperty(value = "sku Number", name = "skuNumber")
    private String skuNumber;

    @ApiModelProperty(value = "行项目类型", name = "itemType")
    private String itemType;

    @ApiModelProperty(value = "仓库编码", name = "warehouseCode")
    private String warehouseCode;

    @ApiModelProperty(value = "进货单位的数量", name = "purchaseQty")
    private BigDecimal purchaseQty;

    @ApiModelProperty(value = "进货单位", name = "purchaseUom")
    private String purchaseUom;

    @ApiModelProperty(value = "基本数量", name = "basicQty")
    private BigDecimal basicQty;

    @ApiModelProperty(value = "基本单位", name = "basicUom")
    private String basicUom;

    @ApiModelProperty(value = "单位价格", name = "unitPrice")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "净价", name = "netValue")
    private BigDecimal netValue;

    @ApiModelProperty(value = "税", name = "taxExmpt")
    private Integer taxExmpt;

    @ApiModelProperty(value = "货币", name = "currencyCode")
    private String currencyCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "gmt的PO创建时间", name = "gmtCreate")
    private Date gmtCreate;

    @ApiModelProperty(value = "被哪个用户创建", name = "createBy")
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "gmt的PO修改时间", name = "gmtModified")
    private Date gmtModified;

    @ApiModelProperty(value = "被哪个用户修改", name = "modifiedBy")
    private String modifiedBy;

    /**
     * 是否被删除  1-deleted
     */
    @ApiModelProperty(value = "是否被删除", name = "isDeleted")
    private Integer isDeleted;

}
