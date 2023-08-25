package com.inossem.oms.base.svc.domain.dashboard.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "dashboard入参筛选实体类")
public class DashboardReq {

    private String companyCode;
    private String startDate;
    private String endDate;

}
