package com.inossem.oms.base.svc.domain.VO;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Delivered List Response Data
 *
 * @author guoh
 * @date 2022-10-20
 */
@Data
@ApiModel("DeliveredListResp")
public class DeliveryedListResp {

    @ApiModelProperty(value = "deliveryNumber", name = "deliveryNumber")
    private String deliveryNumber;

    @ApiModelProperty(value = "trackingNumber", name = "trackingNumber")
    private String trackingNumber;

    @ApiModelProperty(value = "salesOrderNumber", name = "salesOrderNumber")
    private String salesOrderNumber;

    @ApiModelProperty(value = "salesOrderType", name = "salesOrderType")
    private String salesOrderType;

    @ApiModelProperty(value = "shippingStatus", name = "shippingStatus")
    private String shippingStatus;

    @ApiModelProperty(value = "carrier", name = "carrier")
    private String carrier;

    @ApiModelProperty(value = "shippedDate", name = "shippedDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date shippedDate;
}
