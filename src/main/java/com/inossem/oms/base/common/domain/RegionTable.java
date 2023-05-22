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
 * 【请填写功能名称】对象 region_table
 * 
 * @author shigf
 * @date 2022-11-04
 */
@Data
@TableName("region_table")
@ApiModel("省份字典表")
@AllArgsConstructor
@NoArgsConstructor
public class RegionTable
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "省份编码",name = "regionCode")
    private String regionCode;

    @ApiModelProperty(value = "国家编码",name = "countryCode")
    private String countryCode;

    @ApiModelProperty(value = "省份名称",name = "name")
    private String name;

}
