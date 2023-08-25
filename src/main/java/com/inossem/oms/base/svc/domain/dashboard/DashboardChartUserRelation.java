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
@ApiModel(description = "dashboard_chart_user_relation表")
@TableName("dashboard_chart_user_relation")
public class DashboardChartUserRelation {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户Id", name = "userId")
    private Integer userId;

    @ApiModelProperty(value = "表Id", name = "chartId")
    private Integer chartId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "gmt的创建时间", name = "gmtCreate")
    private Date gmtCreate;

    @ApiModelProperty(value = "被哪个用户创建", name = "createBy")
    private Integer createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "gmt的修改时间", name = "gmtModified")
    private Date gmtModified;

    @ApiModelProperty(value = "被哪个用户修改", name = "modifiedBy")
    private Integer modifiedBy;

}
