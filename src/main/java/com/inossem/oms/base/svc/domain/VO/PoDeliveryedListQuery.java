package com.inossem.oms.base.svc.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * Deliveryed List Query Params
 *
 * @author guoh
 * @date 2022-10-20
 */
@Data
@ApiModel("DeliveryedListQuery")
public class PoDeliveryedListQuery {

    @ApiModelProperty(value = "seachText", name = "seachText")
    private String seachText;

    @ApiModelProperty(value = "companyCode", name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "carrier", name = "carrier")
    private String carrier;

    @ApiModelProperty(value = "purchasesOrderType", name = "purchasesOrderType")
    private String purchasesOrderType;

    @ApiModelProperty(value = "shippingStaus", name = "shippingStaus")
    private String shippingStaus;

    @ApiModelProperty(value = "completeDelivery", name = "completeDelivery")
    private Integer completeDelivery;

    @ApiModelProperty(value = "shippedStartTime", name = "shippedStartTime")
    private String shippedStartTime;

    @ApiModelProperty(value = "shippedEndTime", name = "shippedEndTime")
    private String shippedEndTime;

}
