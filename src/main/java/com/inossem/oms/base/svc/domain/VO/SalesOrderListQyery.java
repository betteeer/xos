package com.inossem.oms.base.svc.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * Sales Order List Query Params
 *
 * @author guoh1
 * @date 2022-10-20
 */
@Data
@ApiModel("SalesOrderListQyery")
public class SalesOrderListQyery {

    @ApiModelProperty(value = "seachText", name = "seachText")
    private String seachText;

    @ApiModelProperty(value = "companyCode", name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "orderType", name = "orderType")
    private String orderType;

    @ApiModelProperty(value = "orderStatus", name = "orderStatus")
    private String orderStatus;

    @ApiModelProperty(value = "deliveryStatus", name = "deliveryStatus")
    private String deliveryStatus;

    @ApiModelProperty(value = "billingStatus", name = "billingStatus")
    private String billingStatus;



}
