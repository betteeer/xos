package com.inossem.oms.base.svc.domain.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * PO list query params
 *
 * @author kongh
 * @date 2022-11-05
 */
@Data
@ApiModel("PO list view object")
public class PoSaveVo {

    @ApiModelProperty(value = "唯一标识", name = "id")
    private Long id;

    @ApiModelProperty(value = "业务标识", name = "poNumber")
    private String poNumber;

    @ApiModelProperty(value = "公司代码", name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "订单类型", name = "orderType")
    private String orderType;

    @ApiModelProperty(value = "订单状态", name = "orderStatus")
    private String orderStatus;

    @ApiModelProperty(value = "发运状态", name = "deliveryStatus")
    private String deliveryStatus;

    @ApiModelProperty(value = "开票状态", name = "billingStatus")
    private String invoiceStatus;

    @ApiModelProperty(value = "Drop ship是否点击了complete", name = "dropshipComplete")
    private Integer dropshipComplete;

    @ApiModelProperty(value = "BP号", name = "bpVendor")
    private String bpVendor;

    @ApiModelProperty(value = "BP名称", name = "bpName")
    private String bpName;

    @ApiModelProperty(value = "发货地址", name = "payAddress")
    private AddressSaveVo payAddress;

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

    @ApiModelProperty(value = "采购单行信息", name = "poItemList")
    private List<PoItemSaveVo> poItemList;

    @ApiModelProperty(value = "so order header notes", name = "poNotes")
    private String poNotes;
}
