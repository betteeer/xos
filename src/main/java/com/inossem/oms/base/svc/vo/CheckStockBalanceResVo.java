package com.inossem.oms.base.svc.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@ApiModel("校验库存余量结果实体")
@AllArgsConstructor
@NoArgsConstructor
public class CheckStockBalanceResVo {


    @ApiModelProperty(value = "余量是否充足",name = "warehouseCode")
    private boolean isAdequate;

    @ApiModelProperty(value = "校验库存余量子对象",name = "checkStockBalanceSubVos")
    private List<CheckStockBalanceResSubVo> checkStockBalanceSubVos;
}
