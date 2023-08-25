package com.inossem.oms.base.svc.domain.dashboard.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "dashboard most inventory入参筛选实体类")
public class MostInventoryReq {

    private String companyCode;
    private String warehouseCode;

}
