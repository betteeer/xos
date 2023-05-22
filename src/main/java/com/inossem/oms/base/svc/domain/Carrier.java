package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zoutong
 * @date 2022/10/17
 **/
@Data
@ApiModel("运输公司表")
@TableName("carrier_table")
@AllArgsConstructor
@NoArgsConstructor
public class Carrier {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司编号",name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "运输公司编号",name = "carrierCode")
    private String carrierCode;

    @ApiModelProperty(value = "运输公司名称",name = "name")
    private String name;

    @ApiModelProperty(value = "状态",name = "status")
    private int status;

    @ApiModelProperty(value = "access_token",name = "accessToken")
    private String accessToken;

    @ApiModelProperty(value = "运输公司用户名",name = "carrierUsername")
    private String carrierUsername;

    @ApiModelProperty(value = "运输公司用户密码",name = "carrierPassword")
    private String carrierPassword;

    @ApiModelProperty(value = "tracking_url",name = "trackingUrl")
    private String trackingUrl;

    @ApiModelProperty(value = "logo_url",name = "logoUrl")
    private String logoUrl = "";

    @ApiModelProperty(value = "图片列表",name = "pictureList")
    @TableField(exist = false)
    private List<String> pictureList;
}
