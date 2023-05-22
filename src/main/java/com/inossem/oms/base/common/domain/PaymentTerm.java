package com.inossem.oms.base.common.domain;

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
 * 【请填写功能名称】对象 payment_term
 * 
 * @author shigf
 * @date 2022-11-04
 */
@Data
@TableName("payment_term")
@ApiModel("支付字典表")
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTerm
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "paymentTerm",name = "paymentTerm")
    private String paymentTerm;

    @ApiModelProperty(value = "描述",name = "description")
    private String description;

    @ApiModelProperty(value = "支付周期",name = "paymentDays")
    private String paymentDays;

    @ApiModelProperty(value = "支付折扣",name = "paymentDiscount")
    private BigDecimal paymentDiscount;

    @ApiModelProperty(value = "创建时间",name = "gmtCreate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date gmtCreate;

    @ApiModelProperty(value = "修改时间",name = "gmtModified")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date gmtModified;

    @ApiModelProperty(value = "创建者",name = "createBy")
    private String createBy;
    @ApiModelProperty(value = "修改者",name = "modifiedBy")
    private String modifiedBy;

    @ApiModelProperty(value = "是否删除",name = "isDeleted")
    private Integer isDeleted;

}
