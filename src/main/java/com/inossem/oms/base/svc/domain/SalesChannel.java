package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("sales_channel")
public class SalesChannel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司编号",name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "编号",name = "channelCode")
    private String channelCode;

    @ApiModelProperty(value = "描述",name = "channelDes")
    private String channelDes;

    @ApiModelProperty(value = "状态",name = "status")
    private String status;
}
