package com.inossem.oms.base.svc.domain.dashboard.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(description = "dashboard sales percentage表实体类")
public class SalesPercentageVo {
    private String name;
    private BigDecimal value;
    private BigDecimal grossProfit;
    private Integer totalOrder;
}
