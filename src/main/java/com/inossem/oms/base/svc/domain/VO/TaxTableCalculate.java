package com.inossem.oms.base.svc.domain.VO;

import com.inossem.oms.base.svc.domain.TaxTableItems;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Tax Table Calculate Params
 *
 * @author guoh
 * @date 2022-10-17
 */
@Data
@ApiModel("TaxTableCalculate")
public class TaxTableCalculate {

    @ApiModelProperty(value = "provinceCode", name = "provinceCode")
    private String provinceCode;

    @ApiModelProperty(value = "taxTableItemsList", name = "taxTableItemsList")
    private List<TaxTableItems> taxTableItemsList;

}
