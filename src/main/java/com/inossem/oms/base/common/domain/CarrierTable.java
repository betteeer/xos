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
 * 【请填写功能名称】对象 carrier_table
 * 
 * @author shigf
 * @date 2022-11-04
 */
@Data
@TableName("carrier_table_list")
@ApiModel("运输公司表")
@AllArgsConstructor
@NoArgsConstructor
public class CarrierTable
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "运输公司编码",name = "carrierCode")
    private String carrierCode;

    @ApiModelProperty(value = "运输公司名称",name = "name")
    private String name;

    @ApiModelProperty(value = "状态",name = "status")
    private Integer status;

    @ApiModelProperty(value = "accessToken",name = "accessToken")
    private String accessToken;

    @ApiModelProperty(value = "运输公司用户名",name = "carrierUsername")
    private String carrierUsername;

    @ApiModelProperty(value = "运输公司用户密码",name = "carrierPassword")
    private String carrierPassword;

    @ApiModelProperty(value = "trackingUrl",name = "trackingUrl")
    private String trackingUrl;

    @ApiModelProperty(value = "logoUrl",name = "logoUrl")
    private String logoUrl;
}
