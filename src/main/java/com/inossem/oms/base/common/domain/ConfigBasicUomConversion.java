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
 * 【请填写功能名称】对象 config_basic_uom_conversion
 * 
 * @author shigf
 * @date 2022-11-04
 */
@Data
@TableName("config_basic_uom_conversion")
@ApiModel("基础单位转化字典表")
@AllArgsConstructor
@NoArgsConstructor
public class ConfigBasicUomConversion
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "原始单位",name = "fromUom")
    private String fromUom;

    @ApiModelProperty(value = "目标单位",name = "toUom")
    private String toUom;

    @ApiModelProperty(value = "from 的单位数量",name = "denominator")
    private Long denominator;

    @ApiModelProperty(value = "to 的单位数量",name = "numerator")
    private Long numerator;

}
