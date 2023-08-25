package com.inossem.oms.base.svc.domain.dashboard;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "dashboard_chart表")
@TableName("dashboard_chart")
public class DashboardChart {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "配置项", name = "config")
    private String config;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "gmt的创建时间", name = "gmtCreate")
    private Date gmtCreate;

    @ApiModelProperty(value = "被哪个用户创建", name = "createBy")
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "gmt的修改时间", name = "gmtModified")
    private Date gmtModified;

    @ApiModelProperty(value = "被哪个用户修改", name = "modifiedBy")
    private String modifiedBy;

}
