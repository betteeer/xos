package com.inossem.oms.base.svc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@ApiModel("Reverse物料凭证的操作")
@Data
public class ReversedMaterialDocVO {

    @ApiModelProperty(value = "companyCode",name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "reverseDate",name = "reverseDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date reverseDate;

    @ApiModelProperty(value = "行项目号",name = "docNumber")
    @NotBlank(message = "docNumber cannot be empty")
    private String docNumber;

    @ApiModelProperty(value = "描述",name = "描述")
    private String note;

    @ApiModelProperty(value = "行项目",name = "reversedMaterialDocItemVos")
    private List<ReversedMaterialDocItemVo> reversedMaterialDocItemVos;
}
