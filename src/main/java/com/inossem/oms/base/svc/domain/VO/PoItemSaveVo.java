package com.inossem.oms.base.svc.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author kgh
 * @date 2022-11-08 14:30
 */
@Data
@ApiModel("po item save vo")
public class PoItemSaveVo {

    @ApiModelProperty(value = "唯一标识", name = "id")
    private Long id;

    @ApiModelProperty(value = "公司代码", name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "PO的行项目号", name = "poItem")
    private String poItem;

    @ApiModelProperty(value = "sku Number", name = "skuNumber")
    private String skuNumber;

    @ApiModelProperty(value = "行项目类型", name = "itemType")
    private String itemType;

    @ApiModelProperty(value = "仓库编码", name = "warehouseCode")
    private String warehouseCode;

    @ApiModelProperty(value = "进货单位的数量", name = "purchaseQty")
    private BigDecimal purchaseQty;

    @ApiModelProperty(value = "进货单位", name = "purchaseUom")
    private String purchaseUom;

    @ApiModelProperty(value = "基本数量", name = "basicQty")
    private BigDecimal basicQty;

    @ApiModelProperty(value = "基本单位", name = "basicUom")
    private String basicUom;

    @ApiModelProperty(value = "单位价格", name = "unitPrice")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "净价", name = "netValue")
    private BigDecimal netValue;

    @ApiModelProperty(value = "税", name = "taxExmpt")
    private Integer taxExmpt;

    @ApiModelProperty(value = "货币", name = "currencyCode")
    private String currencyCode;

}
