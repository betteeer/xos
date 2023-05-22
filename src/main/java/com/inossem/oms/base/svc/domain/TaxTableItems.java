package com.inossem.oms.base.svc.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * used for Tax Table Calculate Params
 *
 * @author guoh
 * @date 2022-10-17
 */
@Data
@ApiModel("TaxTableItems")
public class TaxTableItems {

    @ApiModelProperty(value = "金额",name = "amount")
    private BigDecimal amount;

    /**
     * 是否免税  1 - 免税
     */
    @ApiModelProperty(value = "是否免税",name = "isTaxExempt")
    private String isTaxExempt;


}
