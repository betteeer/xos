package com.inossem.oms.base.svc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("库存查询返回实体")
@AllArgsConstructor
@NoArgsConstructor
public class QueryMaterialDocResVo {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键",name = "id")
    private Long id;

    @ApiModelProperty(value = "公司编号",name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "文档编号",name = "docNumber")
    private String docNumber;

    @ApiModelProperty(value = "文档",name = "docItem")
    private String docItem;

    @ApiModelProperty(value = "发货日期",name = "postingDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date postingDate;

    @ApiModelProperty(value = "移动类型",name = "movementType")
    private String movementType;

    @ApiModelProperty(value = "移动类型名称",name = "transactionType")
    private String transactionType;

    @ApiModelProperty(value = "仓库名称",name = "warehouseName")
    private String warehouseName;

    @ApiModelProperty(value = "仓库编号",name = "warehouseCode")
    private String warehouseCode;

    @ApiModelProperty(value = "进出类型",name = "inOut")
    private Integer inOut;

    @ApiModelProperty(value = "库存状态",name = "stockStatus")
    private String stockStatus;

    @ApiModelProperty(value = "商品编号",name = "skuNumber")
    private String skuNumber;

    @ApiModelProperty(value = "商品名字",name = "skuName")
    private String skuName;

    @ApiModelProperty(value = "商品数量",name = "skuQty")
    private BigDecimal skuQty;

    @ApiModelProperty(value = "基础单位",name = "basicUom")
    private String basicUom;

    @ApiModelProperty(value = "总数",name = "totalAmount")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "货币编号",name = "currencyCode")
    private String currencyCode;

    @ApiModelProperty(value = "订单类型",name = "referenceType")
    private String referenceType;

    @ApiModelProperty(value = "订单编号",name = "referenceNumber")
    private String referenceNumber;

    @ApiModelProperty(value = "订单行",name = "referenceItem")
    private String referenceItem;

    @ApiModelProperty(value = "是否是反向操作 0-正向 1-反向",name = "isReversed")
    private Integer isReversed;

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

    @ApiModelProperty(value = "是否删除0-未删除 1-已删除",name = "isDeleted")
    private Integer isDeleted;

    @ApiModelProperty(value = "描述",name = "note")
    private String note;

    @ApiModelProperty(value = "accountingDoc",name = "accountingDoc")
    private String accountingDoc;
}
