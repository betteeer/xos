package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 物料凭证对象 material_doc
 * 
 * @author shigf
 * @date 2022-10-11
 */
@Data
@TableName("material_doc")
@ApiModel("库存表")
@AllArgsConstructor
@NoArgsConstructor
public class MaterialDoc
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司编号",name = "companyCode")
    @TableField(value = "company_code")
    private String companyCode;

    @ApiModelProperty(value = "文档编号",name = "docNumber")
    @TableField(value = "doc_number")
    private String docNumber;

    @ApiModelProperty(value = "文档",name = "docItem")
    @TableField(value = "doc_item")
    private String docItem;

    @ApiModelProperty(value = "发货日期",name = "postingDate")
    @TableField(value = "posting_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date postingDate;

    @ApiModelProperty(value = "移动类型",name = "movementType")
    @TableField(value = "movement_type")
    private String movementType;

    @ApiModelProperty(value = "仓库编号",name = "warehouseCode")
    @TableField(value = "warehouse_code")
    private String warehouseCode;

    @ApiModelProperty(value = "转移到的仓库编号，仅仅sto会用到该字段",name = "toWarehouseCode")
    @TableField(value = "to_warehouse_code")
    private String toWarehouseCode;

    @ApiModelProperty(value = "进出类型",name = "inOut")
    @TableField(value = "in_out")
    private Integer inOut;

    @ApiModelProperty(value = "库存状态",name = "stockStatus")
    @TableField(value = "stock_status")
    private String stockStatus;

    @ApiModelProperty(value = "商品编号",name = "skuNumber")
    @TableField(value = "sku_number")
    private String skuNumber;

    @ApiModelProperty(value = "商品数量",name = "skuQty")
    @TableField(value = "sku_qty")
    private BigDecimal skuQty;

    @ApiModelProperty(value = "基础单位",name = "basicUom")
    @TableField(value = "basic_uom")
    private String basicUom;

    @ApiModelProperty(value = "总数",name = "totalAmount")
    @TableField(value = "total_amount")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "货币编号",name = "currencyCode")
    @TableField(value = "currency_code")
    private String currencyCode;

    @ApiModelProperty(value = "订单类型",name = "referenceType")
    @TableField(value = "reference_type")
    private String referenceType;

    @ApiModelProperty(value = "订单编号",name = "referenceNumber")
    @TableField(value = "reference_number")
    private String referenceNumber;

    @ApiModelProperty(value = "订单行",name = "referenceItem")
    @TableField(value = "reference_item")
    private String referenceItem;

    @ApiModelProperty(value = "部门",name = "department")
    @TableField(value = "department")
    private String department;

    @ApiModelProperty(value = "是否是反向操作 0-正向 1-反向",name = "isReversed")
    @TableField(value = "is_reversed")
    private Integer isReversed;

    @ApiModelProperty(value = "创建时间",name = "postingDate")
    @TableField(value = "gmt_create")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;

    @ApiModelProperty(value = "修改时间",name = "postingDate")
    @TableField(value = "gmt_modified")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtModified;

    @ApiModelProperty(value = "创建人",name = "createBy")
    @TableField(value = "create_by")
    private String createBy;

    @ApiModelProperty(value = "修改人",name = "modifiedBy")
    @TableField(value = "modified_by")
    private String modifiedBy;

    @ApiModelProperty(value = "是否删除0-未删除 1-已删除",name = "isDeleted")
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    @ApiModelProperty(value = "描述",name = "note")
    @TableField(value = "note")
    private String note;

    @ApiModelProperty(value = "accountingDoc",name = "accountingDoc")
    private String accountingDoc;


    @Override
    public String toString() {
        return "MaterialDoc{" +
                "id=" + id +
                ", companyCode='" + companyCode + '\'' +
                ", docNumber='" + docNumber + '\'' +
                ", docItem='" + docItem + '\'' +
                ", postingDate=" + postingDate +
                ", movementType='" + movementType + '\'' +
                ", warehouseCode='" + warehouseCode + '\'' +
                ", inOut=" + inOut +
                ", stockStatus='" + stockStatus + '\'' +
                ", skuNumber='" + skuNumber + '\'' +
                ", skuQty=" + skuQty +
                ", basicUom='" + basicUom + '\'' +
                ", totalAmount=" + totalAmount +
                ", currencyCode='" + currencyCode + '\'' +
                ", referenceType='" + referenceType + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", referenceItem='" + referenceItem + '\'' +
                ", department='" + department + '\'' +
                ", isReversed=" + isReversed +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", createBy='" + createBy + '\'' +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", isDeleted=" + isDeleted +
                ", note='" + note + '\'' +
                ", accountingDoc='" + accountingDoc + '\'' +
                '}';
    }
}
