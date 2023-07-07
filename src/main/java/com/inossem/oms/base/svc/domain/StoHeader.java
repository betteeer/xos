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
import java.util.List;

@Data
@TableName("sto_header")
@ApiModel("sto_header表")
public class StoHeader {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "主键", name = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司编号", name = "companyCode")
    @TableField(value = "company_code")
    private String companyCode;

    @ApiModelProperty(value = "类型", name = "sto type")
    @TableField(value = "order_type")
    private String orderType;

    @ApiModelProperty(value = "sto编号", name = "stoNumber")
    @TableField(value = "sto_number")
    private String stoNumber;

    @ApiModelProperty(value = "from warehouse code", name = "fromWarehouseCode")
    @TableField(value = "from_warehouse_code")
    private String fromWarehouseCode;

    @ApiModelProperty(value = "to warehouse code", name = "toWarehouseCode")
    @TableField(value = "to_warehouse_code")
    private String toWarehouseCode;

    @ApiModelProperty(value = "reference number for sto", name = "referenceNumber")
    @TableField(value = "reference_number")
    private String referenceNumber;

    @ApiModelProperty(value = "sto status", name = "orderStatus")
    @TableField(value = "order_status")
    private String orderStatus;

    @ApiModelProperty(value = "sto create date", name = "createDate")
    @TableField(value = "create_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @ApiModelProperty(value = "ship out date", name = "shipoutDate")
    @TableField(value = "shipout_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date shipoutDate;

    @ApiModelProperty(value = "receive date", name = "receiveDate")
    @TableField(value = "receive_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date receiveDate;

    @ApiModelProperty(value = "ship out material document number", name = "shipoutMaterialDoc")
    @TableField(value = "shipout_material_doc")
    private String shipoutMaterialDoc;

    @ApiModelProperty(value = "receive material document number", name = "receiveMaterialDoc")
    @TableField(value = "receive_material_doc")
    private String receiveMaterialDoc;

    @ApiModelProperty(value = "receive material document number", name = "trackingNumber")
    @TableField(value = "tracking_number")
    private String trackingNumber;
    /**
     * 是否被删除  1-deleted
     */
    @ApiModelProperty(value = "是否被删除", name = "isDeleted")
    @TableField(value = "is_deleted")
    private Integer isDeleted = 0;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "gmt的STO创建时间", name = "gmtCreate")
    @TableField(value = "gmt_create")
    private Date gmtCreate;

    @ApiModelProperty(value = "被哪个用户创建", name = "createBy")
    @TableField(value = "create_by")
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "gmt的STO修改时间", name = "gmtModified")
    @TableField(value = "gmt_modified")
    private Date gmtModified;

    @ApiModelProperty(value = "被哪个用户修改", name = "modifiedBy")
    @TableField(value = "modified_by")
    private String modifiedBy;

    @ApiModelProperty(value = "STO备注", name = "stoNotes")
    @TableField(value = "sto_notes")
    private String stoNotes;

    @TableField(exist = false)
    private List<StoItem> items;
    @TableField(exist = false)
    private String fromWarehouseName;
    @TableField(exist = false)
    private String toWarehouseName;
}
