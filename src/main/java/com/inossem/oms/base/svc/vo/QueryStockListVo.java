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

    private String transferQtySort;

    @ApiModelProperty(value = "只按skuNumber去查询",name = "onlySkuNumber")
    // 加这个字段是为了避免前端通过传递searchText来查库存时查到额外数据的情况，比如传递了skuNUmber=12345，但是也会将123456的库存也返回
    // 加了它，就只会用skuNumber=searchText去查，而不会去模糊查询
    private Boolean onlySkuNumber = false;

}
