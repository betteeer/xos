package com.inossem.oms.base.svc.domain.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.inossem.oms.base.svc.domain.SoItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * use create so order header
 *
 * @author guoh
 * @date 2022-10-20
 */
@Data
@ApiModel("SoOrderHeaderInfoVo")
public class SoOrderHeaderInfoVo {

    @ApiModelProperty(value = "soOrderId", name = "soOrderId")
    private Long soOrderId;

    @ApiModelProperty(value = "soOrderNumber", name = "soOrderNumber")
    private String soOrderNumber;

    @ApiModelProperty(value = "公司代码", name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "订单类型", name = "orderType")
    private String orderType;

    @ApiModelProperty(value = "订单状态", name = "orderStatus")
    private String orderStatus;

    @ApiModelProperty(value = "发运状态", name = "deliveryStatus")
    private String deliveryStatus;

    @ApiModelProperty(value = "开票状态", name = "billingStatus")
    private String billingStatus;

    @ApiModelProperty(value = "Drop ship是否点击了complete", name = "dropshipComplete")
    private Integer dropshipComplete;

    @ApiModelProperty(value = "BP号", name = "bpCustomer")
    private String bpCustomer;

    @ApiModelProperty(value = "bpName", name = "bpName")
    private String bpName;

    @ApiModelProperty(value = "发货地址", name = "billAddress")
    private AddressSaveVo billAddress;

    @ApiModelProperty(value = "收货地址", name = "shipAddress")
    private AddressSaveVo shipAddress;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "订单日期", name = "orderDate")
    private Date orderDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "客户要求送货日期", name = "deliveryDate")
    private Date deliveryDate;

    @ApiModelProperty(value = "订单付款期限类型", name = "paymentTerm")
    private String paymentTerm;

    @ApiModelProperty(value = "参考凭证号", name = "referenceNumber")
    private String referenceNumber;

    @ApiModelProperty(value = "货币", name = "currencyCode")
    private String currencyCode;

    @ApiModelProperty(value = "总共税前金额", name = "grossAmount")
    private BigDecimal grossAmount;

    @ApiModelProperty(value = "gst税", name = "gstAmount")
    private BigDecimal gstAmount;

    @ApiModelProperty(value = "hst税", name = "hstAmount")
    private BigDecimal hstAmount;

    @ApiModelProperty(value = "qst税", name = "qstAmount")
    private BigDecimal qstAmount;

    @ApiModelProperty(value = "pst税", name = "pstAmount")
    private BigDecimal pstAmount;

    @ApiModelProperty(value = "总共税后金额", name = "netAmount")
    private BigDecimal netAmount;

    @ApiModelProperty(value = "销售单行信息", name = "soItemList")
    private List<SoItem> soItemList;

    @ApiModelProperty(value = "so order header notes", name = "soNotes")
    private String soNotes;

}
