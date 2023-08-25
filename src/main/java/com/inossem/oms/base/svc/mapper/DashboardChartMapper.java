package com.inossem.oms.base.svc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inossem.oms.base.svc.domain.dashboard.DashboardChart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DashboardChartMapper extends BaseMapper<DashboardChart> {

    List<DashboardChart> queryChartConfigByUserId(@Param("userId") Integer userId);
}
