package com.inossem.oms.base.svc.domain.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Order Header Response Data
 *
 * @author shigf
 * @date 2022-10-20
 */
@Data
@ApiModel("PoOrderHeaderResp")
public class PoOrderHeaderResp {

    @ApiModelProperty(value = "deliveryId", name = "deliveryId")
    private String deliveryId;
    
    @ApiModelProperty(value = "poNumber", name = "poNumber")
    private String poNumber;

    @ApiModelProperty(value = "businessPartner", name = "businessPartner")
    private String businessPartner;

    @ApiModelProperty(value = "businessName", name = "businessName")
    private String businessName;

    @ApiModelProperty(value = "shippingAddress", name = "shippingAddress")
    private String shippingAddress;

//    @ApiModelProperty(value = "收货地址", name = "shipAddress")
//    private Address shippingAddress;

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
