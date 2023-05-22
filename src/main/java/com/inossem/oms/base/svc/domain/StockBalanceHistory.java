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
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 【请填写功能名称】对象 stock_balance_history
 * 
 * @author shigf
 * @date 2022-10-11
 */
@Data
@TableName("stock_balance_history")
@ApiModel("库存容量历史记录")
@AllArgsConstructor
@NoArgsConstructor
public class StockBalanceHistory {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "周期",name = "id")
    @TableField(value = "周期")
    private String period;

    @ApiModelProperty(value = "公司编号",name = "id")
    @TableField(value = "公司编号")
    private String companyCode;

    @ApiModelProperty(value = "仓库编号",name = "warehouseCode")
    @TableField(value = "warehouse_code")
    private String warehouseCode;

    @ApiModelProperty(value = "货品数量",name = "skuNum",example="0")
    @TableField(value = "sku_num")
    private String skuNum;

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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("period", getPeriod())
            .append("companyCode", getCompanyCode())
            .append("warehouseCode", getWarehouseCode())
            .append("skuNum", getSkuNum())
            .append("totalAmount", getTotalAmount())
            .append("averagePrice", getAveragePrice())
            .append("currencyCode", getCurrencyCode())
            .append("totalOnhandQty", getTotalOnhandQty())
            .append("totalBlockQty", getTotalBlockQty())
            .append("totalTransferQty", getTotalTransferQty())
            .append("totalQty", getTotalQty())
            .append("basicUom", getBasicUom())
            .append("isDeleted", getIsDeleted())
            .toString();
    }
}
