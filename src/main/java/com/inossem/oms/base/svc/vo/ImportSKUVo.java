package com.inossem.oms.base.svc.vo;

import com.inossem.oms.base.common.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ImportSKUVo {

    @Excel(name = "sku_number")
    private String skuNumber;

    @Excel(name = "upc_number")
    private String upcNumber;

    @Excel(name = "sku_name")
    private String skuName;

    @Excel(name = "sku_type")
    private String skuType;

    @Excel(name = "sku_group")
    private String skuGroup;

    @Excel(name = "basic_uom")
    private String basicUom;

    @Excel(name = "is_kitting")
    private String isKitting;

    @Excel(name = "sku_description")
    private String skuDescription;

    @Excel(name = "width")
    private BigDecimal width;

    @Excel(name = "height")
    private BigDecimal height;

    @Excel(name = "length")
    private BigDecimal length;

    @Excel(name = "whl_uom")
    private String whlUom;

    @Excel(name = "net_weight")
    private BigDecimal netWeight;

    @Excel(name = "weight_uom")
    private String weightUom;
}
