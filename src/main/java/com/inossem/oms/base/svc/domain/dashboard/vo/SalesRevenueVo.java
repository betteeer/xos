package com.inossem.oms.base.svc.domain.dashboard.vo;

import com.inossem.oms.base.svc.domain.dashboard.dto.SalesRevenueAxisYDto;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel(description = "dashboard sales revenue返回实体类")
public class SalesRevenueVo {

    private List<LocalDate>  labels;
    private List<SalesRevenueAxisYDto> datasets;

}
