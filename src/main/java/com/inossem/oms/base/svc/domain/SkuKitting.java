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
 * @author zoutong
 * @date 2022/10/17
 **/

@Data
@ApiModel("sku与kitting关联表")
@TableName("sku_kitting")
@AllArgsConstructor
@NoArgsConstructor
public class SkuKitting {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司编号",name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "主sku编号",name = "kittingSku")
    private String kittingSku;

    @ApiModelProperty(value = "版本",name = "version")
    private int version = 1;

    @ApiModelProperty(value = "component行数",name = "componentLine")
    private String componentLine;

    @ApiModelProperty(value = "component的sku编号",name = "componentSku")
    private String componentSku;

    @ApiModelProperty(value = "sku名称",name = "skuName")
    @TableField(exist = false)
    private String skuName = "";

    @ApiModelProperty(value = "基础单位",name = "basicUom")
    @TableField(exist = false)
    private String basicUom = "";

    @ApiModelProperty(value = "sku描述",name = "skuDescription")
    @TableField(exist = false)
    private String skuDescription = "";

    @ApiModelProperty(value = "component数量",name = "componentQty")
    private BigDecimal componentQty;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间",name = "gmtCreate")
    private Date gmtCreate;

    @ApiModelProperty(value = "创建者",name = "createBy")
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间",name = "gmtModified")
    private Date gmtModified;

    @ApiModelProperty(value = "修改者",name = "modifiedBy")
    private String modifiedBy;

    @ApiModelProperty(value = "是否删除",name = "isDeleted")
    private int isDeleted;

    @ApiModelProperty(value = "销售单位的数量", name = "salesQty")
    @TableField(exist = false)
    private BigDecimal salesQty;

    @ApiModelProperty(value = "shippedQty",name = "shippedQty")
    @TableField(exist = false)
    private BigDecimal shippedQty = BigDecimal.ZERO;


}
