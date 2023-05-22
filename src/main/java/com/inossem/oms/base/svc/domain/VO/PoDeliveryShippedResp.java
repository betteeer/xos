package com.inossem.oms.base.svc.domain.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Delivery Shipped Info Response Data
 *
 * @author guoh
 * @date 2022-10-20
 */
@Data
@ApiModel("PoDeliveryShippedResp")
public class PoDeliveryShippedResp {

    @ApiModelProperty(value = "businessPartner", name = "businessPartner")
    private String businessPartner;

    @ApiModelProperty(value = "bpName", name = "bpName")
    private String bpName;

    @ApiModelProperty(value = "shippingAddress", name = "shippingAddress")
    private String shippingAddress;

    @ApiModelProperty(value = "deliveryDate", name = "deliveryDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deliveryDate;

    @ApiModelProperty(value = "deliveryNumber", name = "deliveryNumber")
    private String deliveryNumber;

    @ApiModelProperty(value = "carrier", name = "carrier")
    private String carrier;

    @ApiModelProperty(value = "trackingNumber", name = "trackingNumber")
    private String trackingNumber;

    @ApiModelProperty(value = "shippedDate", name = "shippedDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date shippedDate;

    @ApiModelProperty(value = "poOrderItems", name = "poOrderItems")
    private List<PoOrderItems> poOrderItems;

}
