package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("sto_item")
@ApiModel("sto_item表")
public class StoItem {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "主键", name = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司编号", name = "companyCode")
    @TableField(value = "company_code")
    private String companyCode;

    @ApiModelProperty(value = "sto编号", name = "stoNumber")
    @TableField(value = "sto_number")
    private String stoNumber;

    @ApiModelProperty(value = "STO的行项目号", name = "stoItem")
    @TableField(value = "sto_item")
    private String stoItem;

    @ApiModelProperty(value = "sku Number", name = "skuNumber")
    @TableField(value = "sku_number")
    private String skuNumber;

    @ApiModelProperty(value = "from warehouse code", name = "fromWarehouseCode")
    @TableField(value = "from_warehouse_code")
    private String fromWarehouseCode;

    @ApiModelProperty(value = "to warehouse code", name = "toWarehouseCode")
    @TableField(value = "to_warehouse_code")
    private String toWarehouseCode;

    @ApiModelProperty(value = "转移的数量", name = "basicTransferQty")
    @TableField(value = "basic_transfer_qty")
    private BigDecimal basicTransferQty;

    @ApiModelProperty(value = "基本单位", name = "basicUom")
    @TableField(value = "basic_uom")
    private String basicUom;

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
    private String createBy = "";

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "gmt的STO修改时间", name = "gmtModified")
    @TableField(value = "gmt_modified")
    private Date gmtModified;

    @ApiModelProperty(value = "被哪个用户修改", name = "modifiedBy")
    @TableField(value = "modified_by")
    private String modifiedBy = "";
}
