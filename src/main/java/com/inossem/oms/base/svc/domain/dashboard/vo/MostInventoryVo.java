package com.inossem.oms.base.svc.domain.dashboard.vo;

import com.inossem.oms.base.svc.domain.dashboard.dto.MostInventoryDto;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(description = "dashboard most on hand inventory返回实体类")
public class MostInventoryVo {

    private BigDecimal totalQuantity;
    private BigDecimal totalAmount;
    private BigDecimal top20Quantity;
    private BigDecimal top20Amount;
    private List<MostInventoryDto> datasets;

}
