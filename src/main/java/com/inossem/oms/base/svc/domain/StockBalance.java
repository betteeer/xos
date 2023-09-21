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
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 【请填写功能名称】对象 stock_balance
 * 
 * @author shigf
 * @date 2022-10-11
 */
@Data
@TableName("stock_balance")
@ApiModel("库存表")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StockBalance
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司编号",name = "companyCode")
    @TableField(value = "company_code")
    private String companyCode;

    @ApiModelProperty(value = "仓库编号",name = "warehouseCode")
    @TableField(value = "warehouse_code")
    private String warehouseCode;

    @ApiModelProperty(value = "货品编号",name = "skuNumber",example="0")
    @TableField(value = "sku_number")
    private String skuNumber;

//    @ApiModelProperty(value = "货品名称",name = "skuName",example="0")
//    private String skuName;

    @ApiModelProperty(value = "总金额",name = "totalAmount")
    @TableField(value = "total_amount")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "移动平均价",name = "averagePrice")
    @TableField(value = "average_price")
    private BigDecimal averagePrice;

    @ApiModelProperty(value = "货币代码",name = "currencyCode")
    @TableField(value = "currency_code")
    private String currencyCode;

    @ApiModelProperty(value = "现有正常数量",name = "totalOnhandQty")
    @TableField(value = "total_onhand_qty")
    private BigDecimal totalOnhandQty;

    @ApiModelProperty(value = "总冻结数量",name = "totalBlockQty")
    @TableField(value = "total_block_qty")
    private BigDecimal totalBlockQty;

    @ApiModelProperty(value = "总移动数量",name = "totalTransferQty")
    @TableField(value = "total_transfer_qty")
    private BigDecimal totalTransferQty;

    @ApiModelProperty(value = "总数量",name = "totalQty")
    @TableField(value = "total_qty")
    private BigDecimal totalQty;

    @ApiModelProperty(value = "基础单位",name = "basicUom")
    @TableField(value = "basic_uom")
    private String basicUom;

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

    @ApiModelProperty(value = "是否删除 0-未删除 1-已删除",name = "isDeleted",example="0")
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    //带有尾差的移动平均价格
    @TableField(exist = false)
    private BigDecimal averagePriceNumber = BigDecimal.ONE;
    @TableField(exist = false)
    private BigDecimal skuSatetyStock;
    @TableField(exist = false)
    private String skuGroupName;

}
