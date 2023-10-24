package com.inossem.oms.base.svc.domain.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.inossem.oms.base.svc.domain.DeliveryItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Use Create So Delivery
 *
 * @author guoh
 * @date 2022-10-20
 */
@Data
@ApiModel("DeliveryInfoVo")
public class DeliveryInfoVo {

    @ApiModelProperty(value = "主键", name = "id")
    private Long id;

    @ApiModelProperty(value = "发运单号", name = "deliveryNumber")
    private String deliveryNumber;

    @ApiModelProperty(value = "soNumber", name = "soNumber")
    private String soNumber;

    @ApiModelProperty(value = "poNumber", name = "poNumber")
    private String poNumber;

    @ApiModelProperty(value = "companyCode", name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "shippingAddress", name = "shippingAddress")
    private AddressSaveVo shippingAddress;

    @ApiModelProperty(value = "deliveryType", name = "deliveryType")
    private String deliveryType;

    @ApiModelProperty(value = "bpCustomer", name = "bpCustomer")
    private String bpCustomer;

    @ApiModelProperty(value = "bpVendor", name = "bpVendor")
    private String bpVendor;

    @ApiModelProperty(value = "warehouseCode", name = "warehouseCode")
    private String warehouseCode;

    @ApiModelProperty(value = "deliveryDate", name = "deliveryDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deliveryDate;

    @ApiModelProperty(value = "carrierCode", name = "carrierCode")
    private String carrierCode;

    @ApiModelProperty(value = "trackingNumber", name = "trackingNumber")
    private String trackingNumber;

    @ApiModelProperty(value = "postingDate", name = "postingDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date postingDate;

    @ApiModelProperty(value = "deliveryNotes", name = "deliveryNotes")
    private String deliveryNotes;

    @ApiModelProperty(value = "deliveryItemList", name = "deliveryItemList")
    List<DeliveryItem>  deliveryItemList;

    private String shippingReference;
}
