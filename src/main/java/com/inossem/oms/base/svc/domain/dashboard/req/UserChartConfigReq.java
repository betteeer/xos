package com.inossem.oms.base.svc.domain.dashboard.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "保存用户chart配置入参实体类")
public class UserChartConfigReq {
    private Integer userId;
    private List<Integer> config;
}
