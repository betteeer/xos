package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 【请填写功能名称】对象 po_invoice_header
 *
 * @author ruoyi
 * @date 2022-12-09
 */
@Data
@TableName("po_invoice_header")
@ApiModel("poInvoiceHeader")
@AllArgsConstructor
@NoArgsConstructor
public class PoInvoiceHeader
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** $column.columnComment */
    private String companyCode;

    /** $column.columnComment */
    private String partnerId;

    /** $column.columnComment */
    private String invoiceNumber;

    /** $column.columnComment */
    private String paymentTerm;

    /** $column.columnComment */
    private Date postingDate;

    /** $column.columnComment */
    private String referenceDoc;

    /** $column.columnComment */
    private String referenceDocType;

    /** $column.columnComment */
    private String currencyCode;

    /** $column.columnComment */
    private BigDecimal grossAmount;

    /** $column.columnComment */
    private BigDecimal gstAmount;

    /** $column.columnComment */
    private BigDecimal hstAmount;

    /** $column.columnComment */
    private BigDecimal qstAmount;

    /** $column.columnComment */
    private BigDecimal pstAmount;

    /** $column.columnComment */
    private BigDecimal netAmount;

    /** $column.columnComment */
    private Integer isCleared;

    /** $column.columnComment */
    private String accountingDoc;

    /**
     * 发票图片的URL
     */
    private String invoiceTicketUrl;


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

    @TableField(exist = false)
    List<PoInvoiceItem> poInvoiceItemList;

    @ApiModelProperty(value = "exchangeRate", name = "exchangeRate")
    @TableField(exist = false)
    private BigDecimal exchangeRate = BigDecimal.ONE;

    @TableField(exist=false)
    private String bpName;
    @TableField(exist = false)
    private String deliveryNumber;

}
