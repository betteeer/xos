package com.inossem.oms.base.svc.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("SkuUomConversion")
public class SkuUomConversionVo {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "公司编号", name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "sku编号", name = "skuNumber")
    private String skuNumber;

    @ApiModelProperty(value = "基础单位", name = "basicUom")
    private String basicUom;

    @ApiModelProperty(value = "转换单位", name = "conversionUom")
    private String conversionUom;

    @ApiModelProperty(value = "分子", name = "numerator")
    private int numerator;
}
