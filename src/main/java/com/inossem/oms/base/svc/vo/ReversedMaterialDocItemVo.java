package com.inossem.oms.base.svc.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

@Data
@ApiModel("Reverse物料凭证Item的数据")
public class ReversedMaterialDocItemVo {

    @ApiModelProperty(value = "物料凭证ID列表",name = "reverseDate")
    @NotEmpty(message = "materialDocId cannot be empty")
    private Long materialDocId;

//    @ApiModelProperty(value = "商品数量",name = "skuQty")
    private BigDecimal skuQty;

//    @ApiModelProperty(value = "总数",name = "totalAmount")
    private BigDecimal totalAmount;
}
