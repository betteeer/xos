package com.inossem.oms.base.svc.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 列表的响应View Object
 *
 * @author kgh
 * @date 2022-11-05 20:06
 */
@Data
@ApiModel("PO list response view object")
public class PoListResVo {

    @ApiModelProperty(value = "id", name = "id")
    private Long id;
    @ApiModelProperty(value = "companyCode", name = "companyCode")
    private String companyCode;
    @ApiModelProperty(value = "orderType", name = "orderType")
    private String orderType;
    @ApiModelProperty(value = "orderNumber", name = "orderNumber")
    private String orderNumber;
    @ApiModelProperty(value = "orderDate", name = "orderDate")
    private String orderDate;
    @ApiModelProperty(value = "orderStatus", name = "orderStatus")
    private String orderStatus;
    @ApiModelProperty(value = "vendor", name = "vendor")
    private String vendor;
    @ApiModelProperty(value = "referenceOrderNumber", name = "referenceOrderNumber")
    private String referenceOrderNumber;
    @ApiModelProperty(value = "orderTotalAmount", name = "orderTotalAmount")
    private BigDecimal orderTotalAmount;


}
