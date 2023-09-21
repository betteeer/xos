package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;


/**
 * @author guoh
 * @date 2022-10-20
 */
@Data
@ApiModel("DeliveryItem")
@TableName("delivery_item")
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryItem {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ApiModelProperty(value = "主键", name = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 公司代码
     */
    @ApiModelProperty(value = "公司代码", name = "companyCode")
    private String companyCode;

    /**
     * delivery_number
     */
    @ApiModelProperty(value = "发运单号", name = "deliveryNumber")
    private String deliveryNumber;

    /**
     * 行
     */
    @ApiModelProperty(value = "行", name = "deliveryNumber")
    private String deliveryItem;

    /**
     * reference_doc
     */
    @ApiModelProperty(value = "referenceDoc", name = "referenceDoc")
    private String referenceDoc;

    /**
     * reference_doc_item
     */
    @ApiModelProperty(value = "referenceDocItem", name = "referenceDocItem")
    private String referenceDocItem;

    /**
     * sku
     */
    @ApiModelProperty(value = "skuNumber", name = "skuNumber")
    private String skuNumber;

    /**
     * kitting_sku
     */
    @ApiModelProperty(value = "kittingSku", name = "kittingSku")
    private String kittingSku;

    /**
     * kiiting_delivery_qty
     */
    @ApiModelProperty(value = "kittingDeliveryQty", name = "kittingDeliveryQty")
    private BigDecimal kittingDeliveryQty;

    /**
     * bp_sku_number
     */
    @ApiModelProperty(value = "bpSkuNumber", name = "bpSkuNumber")
    private String bpSkuNumber;

    /**
     * 仓库编码
     */
    @ApiModelProperty(value = "仓库编码", name = "warehouseCode")
    private String warehouseCode;

    /**
     * 是否发运完成
     */
    @ApiModelProperty(value = "是否发运完成", name = "completeDelivery")
    private Integer completeDelivery;

    /**
     * 发运数量
     */
    @ApiModelProperty(value = "发运数量", name = "deliveryQty")
    private BigDecimal deliveryQty;

    /**
     * 实际发运数量
     */
    @ApiModelProperty(value = "实际发运数量", name = "deliveredQty")
    private BigDecimal deliveredQty;

    /**
     * 基础数量单位
     */
    @ApiModelProperty(value = "基础数量单位", name = "deliveredQty")
    private String basicUom;

    /**
     * 是否删除
     */
    @ApiModelProperty(value = "是否删除", name = "isDeleted")
    private Integer isDeleted;


    /**
     * 用于组合商品查询库存里面的移动平均价
     */
    @ApiModelProperty(value = "avagUnitPrice", name = "avagUnitPrice")
    @TableField(exist = false)
    private BigDecimal avagUnitPrice;

    @TableField(exist = false)
    private  String skuName;
    @TableField(exist=false)
    private String kittingSkuName;
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date postingDate;
    @TableField(exist = false)
    private String skuType;
}

