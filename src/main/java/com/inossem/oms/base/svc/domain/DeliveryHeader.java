package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


/**
 * @author guoh
 * @date 2022-10-20
 */
@Data
@ApiModel("DeliveryHeader")
@TableName("delivery_header")
public class DeliveryHeader {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ApiModelProperty(value = "主键", name = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 公司代码
     */
    @ApiModelProperty(value = "公司代码", name = "companyCode")
    private String companyCode;

    /**
     * delivery Number 发运单号
     */
    @ApiModelProperty(value = "发运单号", name = "deliveryNumber")
    private String deliveryNumber;

    /**
     * Delivery type：
     * DN：SO delivery
     * ASN:   PO delivery
     */
    @ApiModelProperty(value = "发运单类型", name = "deliveryType")
    private String deliveryType;

    /**
     * 是否发运完成   this field only for service/drop ship PO, when delivery complete
     */
    @ApiModelProperty(value = "是否发运完成", name = "completeDelivery")
    private Integer completeDelivery;

    /**
     * 发运时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "发运时间", name = "deliveryDate")
    private Date deliveryDate;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "发布时间", name = "postingDate")
    private Date postingDate;

    /**
     * 客户
     */
    @ApiModelProperty(value = "客户", name = "bpCustomer")
    private String bpCustomer;

    /**
     * 供应商
     */
    @ApiModelProperty(value = "供应商", name = "bpVendor")
    private String bpVendor;

    /**
     * 是否完成开票
     */
    @ApiModelProperty(value = "是否完成开票", name = "completeBilling")
    private Integer completeBilling;

    /**
     * 票号
     */
    @ApiModelProperty(value = "票号", name = "billingNumber")
    private String billingNumber;

    /**
     * 仓库代码
     */
    @ApiModelProperty(value = "仓库代码", name = "warehouseCode")
    private String warehouseCode;

    /**
     * 承运商代码
     */
    @ApiModelProperty(value = "承运商代码", name = "carrierCode")
    private String carrierCode;

    /**
     * tracking_number
     */
    @ApiModelProperty(value = "快递单号", name = "trackingNumber")
    private String trackingNumber;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间", name = "gmtCreate")
    private Date gmtCreate;

    @ApiModelProperty(value = "被哪个用户创建", name = "createBy")
    private String createBy;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间", name = "gmtModified")
    private Date gmtModified;

    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人", name = "modifiedBy")
    private String modifiedBy;

    /**
     * 是否删除
     */
    @ApiModelProperty(value = "是否删除", name = "isDeleted")
    private Integer isDeleted;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", name = "deliveryNotes")
    private String deliveryNotes;

    private String shippingReference;


    @TableField(exist = false)
    private String bpName;
    @TableField(exist = false)
    private String orderType;
    @TableField(exist = false)
    private String poNumber;
}
