package com.inossem.oms.base.svc.domain.dashboard.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(description = "dashboard best seller返回实体类")
public class BestSellerVo {
    private String skuName;
    private String skuNumber;
    private BigDecimal soldCount;
    private double amount;
}
