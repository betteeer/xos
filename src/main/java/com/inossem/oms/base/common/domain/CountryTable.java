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
 * 【请填写功能名称】对象 country_table
 * 
 * @author shigf
 * @date 2022-11-04
 */
@Data
@TableName("country_table")
@ApiModel("国家字典表")
@AllArgsConstructor
@NoArgsConstructor
public class CountryTable
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "国家编码",name = "countryCode")
    private String countryCode;

    @ApiModelProperty(value = "isoCode",name = "isoCode")
    private String isoCode;

    @ApiModelProperty(value = "国家名称",name = "name")
    private String name;

}
