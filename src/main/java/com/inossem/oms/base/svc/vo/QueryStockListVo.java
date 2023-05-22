package com.inossem.oms.base.svc.vo;

import com.inossem.oms.base.svc.domain.SkuMaster;
import com.inossem.sco.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ApiModel("查询库存列表参数")
@ToString
public class QueryStockListVo extends BaseEntity {

    @ApiModelProperty(value = "查询条件",name = "searchText")
    private String searchText;

    @ApiModelProperty(value = "仓库编号",name = "warehouseCode")
    private String warehouseCode;

    @ApiModelProperty(value = "公司编号编号",name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "当前页数",name = "pageNum")
    private Integer pageNum;

    @ApiModelProperty(value = "每页条数",name = "pageSize")
    private Integer pageSize;

    private List<SkuMaster> skuMasters;

    private String skuCodeSort;

    private String totalStockQtySort;

    private String onHandQtySort;

    private String blockQtySort;

    private String balanceValueSort;


}
