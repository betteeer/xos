package com.inossem.oms.base.svc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.inossem.oms.base.svc.domain.SkuMaster;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 【请填写功能名称】对象 stock_balance
 * 
 * @author shigf
 * @date 2022-10-11
 */
@Data
@ApiModel("库存返回实体")
@AllArgsConstructor
@NoArgsConstructor
public class QueryStockBalanceResVo
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键",name = "id")
    private Long id;

    @ApiModelProperty(value = "公司编号",name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "仓库编号",name = "warehouseCode")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库名称",name = "warehouseName")
    private String warehouseName;

    @ApiModelProperty(value = "货品编号",name = "skuNumber",example="0")
    private String skuNumber;

    @ApiModelProperty(value = "商品名字",name = "skuName")
    private String skuName;

    @ApiModelProperty(value = "总金额",name = "totalAmount")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "移动平均价",name = "averagePrice")
    private BigDecimal averagePrice;

    @ApiModelProperty(value = "货币代码",name = "currencyCode")
    private String currencyCode;

    @ApiModelProperty(value = "现有正常数量",name = "totalOnhandQty")
    private BigDecimal totalOnhandQty;

    @ApiModelProperty(value = "总冻结数量",name = "totalBlockQty")
    private BigDecimal totalBlockQty;

    @ApiModelProperty(value = "总移动数量",name = "totalTransferQty")
    private BigDecimal totalTransferQty;

    @ApiModelProperty(value = "总数量",name = "totalQty")
    private BigDecimal totalQty;

    @ApiModelProperty(value = "基础单位",name = "basicUom")
    private String basicUom;

    @ApiModelProperty(value = "物品信息",name = "skuMaster")
    @Deprecated
    private SkuMaster skuMaster;

    @ApiModelProperty(value = "安全库存",name = "skuSatetyStock")
    private BigDecimal skuSatetyStock;

    @ApiModelProperty(value = "创建时间",name = "postingDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;

    @ApiModelProperty(value = "修改时间",name = "postingDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtModified;

    @ApiModelProperty(value = "创建人",name = "createBy")
    private String createBy;

    @ApiModelProperty(value = "修改人",name = "modifiedBy")
    private String modifiedBy;

    @ApiModelProperty(value = "是否删除 0-未删除 1-已删除",name = "isDeleted",example="0")
    private Integer isDeleted;
}
