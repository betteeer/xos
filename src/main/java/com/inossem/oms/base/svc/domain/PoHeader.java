package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 【请填写功能名称】对象 po_header
 *
 * @author shigf
 * @date 2022-11-04
 */
@Data
@TableName("po_header")
@ApiModel("PoHeader")
public class PoHeader
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键", name = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司代码", name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "订单类型", name = "orderType")
    private String orderType;

    @ApiModelProperty(value = "订单号", name = "poNumber")
    private String poNumber;

    @ApiModelProperty(value = "订单状态", name = "orderStatus")
    private String orderStatus;

    @ApiModelProperty(value = "发运状态", name = "deliveryStatus")
    private String deliveryStatus;

    @ApiModelProperty(value = "开票状态", name = "invoiceStatus")
    private String invoiceStatus;

    @ApiModelProperty(value = "Drop ship是否点击了complete", name = "dropshipComplete")
    private Integer dropshipComplete;

    @ApiModelProperty(value = "进货商家", name = "bpVendor")
    private String bpVendor;

    @ApiModelProperty(value = "商家名称", name = "bpName")
    private String bpName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "订单日期", name = "orderDate")
    private Date orderDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "客户要求送货日期", name = "deliveryDate")
    private Date deliveryDate;

    @ApiModelProperty(value = "订单付款期限类型", name = "paymentTerm")
    private String paymentTerm;

    @ApiModelProperty(value = "参考凭证号", name = "referenceNumber")
    private String referenceNumber;

    @ApiModelProperty(value = "货币", name = "currencyCode")
    private String currencyCode;

    @ApiModelProperty(value = "总共税前金额", name = "grossAmount")
    private BigDecimal grossAmount;

    @ApiModelProperty(value = "gst税", name = "gstAmount")
    private BigDecimal gstAmount;

    @ApiModelProperty(value = "hst税", name = "hstAmount")
    private BigDecimal hstAmount;

    @ApiModelProperty(value = "qst税", name = "qstAmount")
    private BigDecimal qstAmount;

    @ApiModelProperty(value = "pst税", name = "pstAmount")
    private BigDecimal pstAmount;

    @ApiModelProperty(value = "总共税后金额", name = "netAmount")
    private BigDecimal netAmount;


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

    @ApiModelProperty(value = "po备注", name = "soNotes")
    private String poNotes;

    @ApiModelProperty(value = "invoiceNumber", name = "invoiceNumber")
    @TableField(exist = false)
    private String invoiceNumber;

    @ApiModelProperty(value = "invoiceNumber", name = "invoiceNumber")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date invoiceDate;

}
