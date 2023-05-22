package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 【开票明细】对象 so_bill_item
 * 
 * @author guoh
 * @date 2022-11-20
 */
@Data
@ApiModel("开票明细")
@TableName("so_bill_item")
@AllArgsConstructor
@NoArgsConstructor
public class SoBillItem
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键", name = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "companyCode", name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "billingNumber", name = "billingNumber")
    private String billingNumber;

    @ApiModelProperty(value = "billingItem", name = "billingItem")
    private String billingItem;

    @ApiModelProperty(value = "referenceDoc", name = "referenceDoc")
    private String referenceDoc;

    @ApiModelProperty(value = "referenceDocItem", name = "referenceDocItem")
    private String referenceDocItem;

    @ApiModelProperty(value = "skuNumber", name = "skuNumber")
    private String skuNumber;

    @ApiModelProperty(value = "billQty", name = "billQty")
    private BigDecimal billQty;

    @ApiModelProperty(value = "billUom", name = "billUom")
    private String billUom;

    @ApiModelProperty(value = "unitPrice", name = "unitPrice")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "grossAmount", name = "grossAmount")
    private BigDecimal grossAmount;

    @ApiModelProperty(value = "taxExmpt", name = "taxExmpt")
    private Integer taxExmpt;

    @ApiModelProperty(value = "currencyCode", name = "currencyCode")
    private String currencyCode;

    @ApiModelProperty(value = "gstAmount", name = "gstAmount")
    private BigDecimal gstAmount;

    @ApiModelProperty(value = "hstAcmount", name = "hstAcmount")
    private BigDecimal hstAcmount;

    @ApiModelProperty(value = "pstAmount", name = "pstAmount")
    private BigDecimal pstAmount;

    @ApiModelProperty(value = "qstAmount", name = "qstAmount")
    private BigDecimal qstAmount;

    @ApiModelProperty(value = "netAmount", name = "netAmount")
    private BigDecimal netAmount;

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

}
