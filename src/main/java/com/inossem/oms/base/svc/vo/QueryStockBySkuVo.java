package com.inossem.oms.base.svc.vo;

import com.inossem.sco.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ApiModel("查询sku库存信息参数")
@ToString
public class QueryStockBySkuVo extends BaseEntity {

    @ApiModelProperty(value = "skuNumber",name = "skuNumber")
    private String skuNumber;

    @ApiModelProperty(value = "公司编号编号",name = "companyCode")
    private String companyCode;

}
