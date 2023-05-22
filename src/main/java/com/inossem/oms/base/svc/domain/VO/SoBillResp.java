package com.inossem.oms.base.svc.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * 【开票】对象 so_bill_header
 * 
 * @author guoh
 * @date 2022-11-2o
 */
@Data
@ApiModel("开票")
@AllArgsConstructor
@NoArgsConstructor
public class SoBillResp
{

    @ApiModelProperty(value = "billingStatus", name = "billingStatus")
    private String billingStatus;

    @ApiModelProperty(value = "totalAmount", name = "totalAmount")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "billed", name = "billed")
    private BigDecimal billed;

    @ApiModelProperty(value = "billedPercentage", name = "billedPercentage")
    private BigDecimal billedPercentage;

//    @ApiModelProperty(value = "isCompalteBilling", name = "isCompalteBilling")
//    private Integer isCompalteBilling;
}
