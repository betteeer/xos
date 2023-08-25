package com.inossem.oms.base.svc.domain.dashboard.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.models.auth.In;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(description = "dashboard title bar返回实体类")
public class TitleBarVo {
    private BigDecimal sales;
    private BigDecimal purchases;
    private BigDecimal profit;
    private Long salesOrders;
    private Long unfinishedOrders;
}
