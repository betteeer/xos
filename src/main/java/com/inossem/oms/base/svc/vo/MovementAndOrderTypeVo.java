package com.inossem.oms.base.svc.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel("调用movementType/list返回给bkp的vo")
@AllArgsConstructor
@NoArgsConstructor
public class MovementAndOrderTypeVo {

    @ApiModelProperty(value = "类型",name = "type")
    private String type;
    @ApiModelProperty(value = "code",name = "code")
    private String code;

    @ApiModelProperty(value = "描述",name = "description")
    private String description;

    @ApiModelProperty(value = "公司code", name = "companyCode")
    private String company_code;
}
