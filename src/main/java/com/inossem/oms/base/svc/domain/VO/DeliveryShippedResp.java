package com.inossem.oms.base.svc.domain.VO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.inossem.oms.base.svc.domain.SoItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Delivery Shipped Info Response Data
 *
 * @author guoh
 * @date 2022-10-20
 */
@Data
@ApiModel("DeliveryShippedResp")
public class DeliveryShippedResp {

    @ApiModelProperty(value = "deliveryId", name = "deliveryId")
    private Long deliveryId;

    @ApiModelProperty(value = "billNumber", name = "billNumber")
    private String billNumber;

    @ApiModelProperty(value = "billPostingDate", name = "billPostingDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date billPostingDate;

    @ApiModelProperty(value = "businessPartner", name = "businessPartner")
    private String businessPartner;


    @ApiModelProperty(value = "warehouseCode", name = "warehouseCode")
    private String warehouseCode;


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

    @ApiModelProperty(value = "soItemList", name = "soItemList")
    @TableField(exist = false)
    private List<SoItem> soItemList;

    @ApiModelProperty(value = "soItemList", name = "soItemList")
    @TableField(exist = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    private String shippingReference;
}
