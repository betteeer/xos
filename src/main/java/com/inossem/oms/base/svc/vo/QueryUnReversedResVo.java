package com.inossem.oms.base.svc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@ApiModel("unReverse物料凭证返回实体")
@Data
public class QueryUnReversedResVo {

    @ApiModelProperty(value = "文档编号",name = "docNumber")
    private String docNumber;

    @ApiModelProperty(value = "移动类型名字",name = "transactionType")
    private String transactionType;

    @ApiModelProperty(value = "移动类型",name = "movementType")
    private String movementType;

    @ApiModelProperty(value = "冻结类型",name = "stockStatus")
    private String stockStatus;

    @ApiModelProperty(value = "仓库名称",name = "warehouseName")
    private String warehouseName;

    @ApiModelProperty(value = "仓库",name = "warehouse")
    private String warehouse;

    @ApiModelProperty(value = "发货时间",name = "warehouse")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date postDate;

    @ApiModelProperty(value = "所有的物料凭证项",name = "queryUnReversedSubResVoList")
    private List<QueryUnReversedSubResVo> queryUnReversedSubResVoList;
}
