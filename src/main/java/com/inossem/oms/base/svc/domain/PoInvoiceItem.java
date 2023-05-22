package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 【请填写功能名称】对象 po_invoice_item
 * 
 * @author ruoyi
 * @date 2022-12-09
 */
@Data
@TableName("po_invoice_item")
@ApiModel("poInvoiceItem")
public class PoInvoiceItem
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** $column.columnComment */
    private String companyCode;

    /** $column.columnComment */
    private String invoiceingNumber;

    /** $column.columnComment */
    private String invoiceItem;

    /** $column.columnComment */
    private String referenceDoc;

    /** $column.columnComment */
    private String referenceDocItem;

    /** $column.columnComment */
    private String skuNumber;

    /** $column.columnComment */
    private BigDecimal invoiceQty;

    /** $column.columnComment */
    private String invoiceUom;

    /** $column.columnComment */
    private BigDecimal unitPrice;

    /** $column.columnComment */
    private BigDecimal grossAmount;

    /** $column.columnComment */
    private Integer taxExmpt;

    /** $column.columnComment */
    private String currencyCode;

    /** $column.columnComment */
    private BigDecimal gstAmount;

    /** $column.columnComment */
    private BigDecimal hstAcmount;

    /** $column.columnComment */
    private BigDecimal pstAmount;

    /** $column.columnComment */
    private BigDecimal qstAmount;

    /** $column.columnComment */
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
}
