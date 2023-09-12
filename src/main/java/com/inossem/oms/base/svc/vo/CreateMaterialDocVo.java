package com.inossem.oms.base.svc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Data
@ApiModel("创建物料凭证参数")
@ToString
public class CreateMaterialDocVo {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "公司编号",name = "companyCode")
    @Length(max = 10,message = "companyCode Max Length is 10")
    @NotBlank(message = "companyCode  cannot be empty")
    private String companyCode;

    @ApiModelProperty(value = "日期",name = "postingDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date postingDate;

    @ApiModelProperty(value = "移动类型",name = "movementType")
    @Length(max = 3,message = "movementType Max Length is 3")
    @NotBlank(message = "movementType cannot be empty")
    private String movementType;

    @ApiModelProperty(value = "仓库编号",name = "warehouseCode")
    @Length(max = 10,message = "warehouseCode Max Length is 10")
    @NotBlank(message = "warehouseCode cannot be empty")
    private String warehouseCode;

//    @ApiModelProperty(value = "进出类型",name = "inOut")
//    private Integer inOut;

    @ApiModelProperty(value = "是否冻结库存 A-不冻结 B-冻结",name = "stockStatus")
    @Length(max = 1,message = "stockStatus Max Length is 1")
    @NotBlank(message = "stockStatus cannot be empty")
    private String stockStatus;

    @ApiModelProperty(value = "商品列表及数量",name = "createMaterialDocSkuVoList")
    private List<CreateMaterialDocSkuVo> createMaterialDocSkuVoList;

    @ApiModelProperty(value = "订单类型 DN ASN INAJ ",name = "referenceType")
    private String referenceType;

    @ApiModelProperty(value = "部门",name = "department")
    @Length(max = 100,message = "department Max Length is 100")
    private String department;

    @ApiModelProperty(value = "备注",name = "note")
    @Length(max = 500,message = "note Max Length is 500")
    private String note;
}
