package com.inossem.oms.base.svc.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * PO list query params
 *
 * @author kongh
 * @date 2022-11-05
 */
@Data
@ApiModel("PO list view object")
public class PoListVo {

    @ApiModelProperty(value = "searchText", name = "searchText")
    private String searchText;

    @ApiModelProperty(value = "companyCode", name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "orderType", name = "orderType")
    private String orderType;

    @ApiModelProperty(value = "orderStatus", name = "orderStatus")
    private String orderStatus;

    @ApiModelProperty(value = "deliveryStatus", name = "deliveryStatus")
    private String deliveryStatus;

    @ApiModelProperty(value = "invoiceStatus", name = "invoiceStatus")
    private String invoiceStatus;

}
