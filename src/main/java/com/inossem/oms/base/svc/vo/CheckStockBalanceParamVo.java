package com.inossem.oms.base.svc.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@ApiModel("校验库存余量实体")
@AllArgsConstructor
@NoArgsConstructor
public class CheckStockBalanceParamVo {

    @ApiModelProperty(value = "仓库编号",name = "warehouseCode")
    @NotBlank(message = "warehouseCode cannot be empty")
    private String warehouseCode;

    @ApiModelProperty(value = "companyCode",name = "companyCode")
    @NotBlank(message = "companyCode cannot be empty")
    private String companyCode;

    @ApiModelProperty(value = "校验库存余量子对象",name = "checkStockBalanceSubVos")
    private List<CheckStockBalanceParamSubVo> checkStockBalanceSubVos;

}
