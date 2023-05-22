package com.inossem.oms.base.svc.vo;

import com.inossem.oms.base.svc.domain.SkuMaster;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ApiModel("查询物料凭证列表参数")
@ToString
public class QueryMaterialDocListVo {

    @ApiModelProperty(value = "查询条件",name = "searchText")
    private String searchText;

    @ApiModelProperty(value = "仓库编号",name = "warehouseCode")
    private String warehouseCode;

    @ApiModelProperty(value = "公司编号",name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "订单类型",name = "referenceOrderType")
    private String referenceOrderType;

    @ApiModelProperty(value = "发货时间起",name = "startPostingDate")
//    @JsonFormat(pattern = "yyyy-MM-dd")
    private String startPostingDate;

    @ApiModelProperty(value = "发货时间止",name = "endPostingDate")
//    @JsonFormat(pattern = "yyyy-MM-dd")
    private String endPostingDate;

    @ApiModelProperty(value = "当前页数",name = "pageNum")
    private Integer pageNum;

    @ApiModelProperty(value = "每页条数",name = "pageSize")
    private Integer pageSize;

    private List<SkuMaster> skuMasters;
}
