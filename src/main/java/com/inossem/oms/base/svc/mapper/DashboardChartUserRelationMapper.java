package com.inossem.oms.base.svc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inossem.oms.base.svc.domain.dashboard.DashboardChartUserRelation;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

public interface DashboardChartUserRelationMapper extends BaseMapper<DashboardChartUserRelation> {

    void insertBatch(@Param("list") ArrayList<DashboardChartUserRelation> relationList);
}
