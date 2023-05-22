package com.inossem.oms.base.common.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 【请填写功能名称】对象 config_uom
 * 
 * @author shigf
 * @date 2022-11-04
 */
@Data
@TableName("config_uom")
@ApiModel("基础单位字典表")
@AllArgsConstructor
@NoArgsConstructor
public class ConfigUom
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "单位编码",name = "uomCode")
    private String uomCode;

    @ApiModelProperty(value = "单位类型",name = "uomType")
    private String uomType;

    @ApiModelProperty(value = "默认小数位数",name = "decimalPlace")
    private Long decimalPlace;

    @ApiModelProperty(value = "描述",name = "description")
    private String description;

}
