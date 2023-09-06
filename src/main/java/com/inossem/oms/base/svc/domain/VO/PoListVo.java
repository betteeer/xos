package com.inossem.oms.base.svc.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

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
    private List<String> orderType;

    @ApiModelProperty(value = "orderStatus", name = "orderStatus")
    private List<String> orderStatus;

    @ApiModelProperty(value = "deliveryStatus", name = "deliveryStatus")
    private List<String> deliveryStatus;

    @ApiModelProperty(value = "invoiceStatus", name = "invoiceStatus")
    private List<String> invoiceStatus;

    private String orderDateStart;
    private String orderDateEnd;
    private BigDecimal grossAmountStart;
    private BigDecimal grossAmountEnd;
    private BigDecimal netAmountStart;
    private BigDecimal netAmountEnd;
    private List<String> currencyCode;

    private String orderBy = "poNumber";
    private Boolean isAsc = true;
}
