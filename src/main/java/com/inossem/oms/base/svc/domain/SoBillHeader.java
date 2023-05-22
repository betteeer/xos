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
import java.util.List;


/**
 * 【开票】对象 so_bill_header
 * 
 * @author guoh
 * @date 2022-11-2o
 */
@Data
@ApiModel("开票")
@TableName("so_bill_header")
@AllArgsConstructor
@NoArgsConstructor
public class SoBillHeader
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键", name = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "companyCode", name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "partnerId", name = "partnerId")
    private String partnerId;

    @ApiModelProperty(value = "billingNumber", name = "billingNumber")
    private String billingNumber;

    @ApiModelProperty(value = "paymentTerm", name = "paymentTerm")
    private String paymentTerm;

    @ApiModelProperty(value = "postingDate", name = "postingDate")
    private Date postingDate;

    @ApiModelProperty(value = "referenceDoc", name = "referenceDoc")
    private String referenceDoc;

    @ApiModelProperty(value = "referenceDocType", name = "referenceDocType")
    private String referenceDocType;

    @ApiModelProperty(value = "currencyCode", name = "currencyCode")
    private String currencyCode;

    @ApiModelProperty(value = "grossAmount", name = "grossAmount")
    private BigDecimal grossAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "gstAmount", name = "gstAmount")
    private BigDecimal gstAmount;

    @ApiModelProperty(value = "hstAmount", name = "hstAmount")
    private BigDecimal hstAmount;

    @ApiModelProperty(value = "qstAmount", name = "qstAmount")
    private BigDecimal qstAmount;

    @ApiModelProperty(value = "pstAmount", name = "pstAmount")
    private BigDecimal pstAmount;

    @ApiModelProperty(value = "netAmount", name = "netAmount")
    private BigDecimal netAmount;

    @ApiModelProperty(value = "isCleared", name = "isCleared")
    private Integer isCleared;

    @ApiModelProperty(value = "accountingDoc", name = "accountingDoc")
    private String accountingDoc;

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

    @ApiModelProperty(value = "soBillItemList", name = "soBillItemList")
    @TableField(exist = false)
    private List<SoBillItem> soBillItemList;

    @ApiModelProperty(value = "billType", name = "billType")
    @TableField(exist = false)
    private Integer billType;

    @ApiModelProperty(value = "exchangeRate", name = "exchangeRate")
    @TableField(exist = false)
    private BigDecimal exchangeRate = BigDecimal.ONE;

}
