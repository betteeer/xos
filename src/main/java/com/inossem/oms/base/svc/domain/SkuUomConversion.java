package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@ApiModel("SkuUomConversion表")
@TableName("sku_uom_conversion")
@AllArgsConstructor
@NoArgsConstructor
public class SkuUomConversion {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key", name = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司编号", name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "sku编号", name = "skuNumber")
    private String skuNumber;

    @ApiModelProperty(value = "基础单位", name = "basicUom")
    private String basicUom = "";

    @ApiModelProperty(value = "转换单位", name = "conversionUom")
    private String conversionUom;

    @ApiModelProperty(value = "分子", name = "numerator")
    private int numerator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间", name = "createTime")
    private Date createTime;

    @ApiModelProperty(value = "创建者", name = "createBy")
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间", name = "gmtModified")
    private Date updateTime;

    @ApiModelProperty(value = "修改者", name = "updateBy")
    private String updateBy;
}
