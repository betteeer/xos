package com.inossem.oms.base.svc.domain.dashboard.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "dashboard sales revenue纵轴实体类")
public class SalesRevenueAxisYDto {
    private String label;
    private double[] data;
}
