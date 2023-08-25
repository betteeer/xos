package com.inossem.oms.svc.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.inossem.oms.base.svc.domain.dashboard.DashboardChart;
import com.inossem.oms.base.svc.domain.dashboard.DashboardChartUserRelation;
import com.inossem.oms.base.svc.domain.dashboard.req.UserChartConfigReq;
import com.inossem.oms.base.svc.mapper.DashboardChartMapper;
import com.inossem.oms.base.svc.mapper.DashboardChartUserRelationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DashboardChartConfigService {

    @Resource
    private DashboardChartMapper dashboardChartMapper;

    @Resource
    private DashboardChartUserRelationMapper dashboardChartUserRelationMapper;

    public List<Object> getChartConfigList() {
        //1.单表查询：dashboard_chart表
        List<DashboardChart> dashboardCharts = dashboardChartMapper.selectList(Wrappers.lambdaQuery(DashboardChart.class));
        //2.对象解析
        ArrayList<Object> res = new ArrayList<>();
        try {
            for (DashboardChart dashboardChart : dashboardCharts) {
                Object parse = JSON.parse(dashboardChart.getConfig());
                res.add(parse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Transactional
    public List<Object> getChartConfigByUserId(Integer userId) {

        //1.关联查询：dashboard_chart表和dashboard_chart_user_relation表
        List<DashboardChart> dashboardCharts = dashboardChartMapper.queryChartConfigByUserId(userId);
        //2.若该user尚未配置chart,则默认给其配置所有的chart->insert dashboard_chart_user_relation
        if (CollectionUtils.isEmpty(dashboardCharts)) {
            dashboardCharts = dashboardChartMapper.selectList(Wrappers.lambdaQuery(DashboardChart.class));
        }
        ArrayList<Object> res = new ArrayList<>();
        ArrayList<DashboardChartUserRelation> relationList = new ArrayList<>();
        //3.对象解析
        try {
            for (DashboardChart dashboardChart : dashboardCharts) {
                //关联查询的dashboardChart对象没有id，默认给配置的dashboardChart有id->insert dashboard_chart_user_relation
                if (Objects.nonNull(dashboardChart.getId())) {
                    DashboardChartUserRelation relation = new DashboardChartUserRelation();
                    relation.setChartId(dashboardChart.getId());
                    relation.setUserId(userId);
                    relation.setCreateBy(userId);
                    relation.setGmtCreate(new Date());
                    relation.setModifiedBy(userId);
                    relation.setGmtModified(new Date());
                    relationList.add(relation);
                }
                Object parse = JSON.parse(dashboardChart.getConfig());
                res.add(parse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //4.insert dashboard_chart_user_relation
        if (CollectionUtils.isNotEmpty(relationList)) dashboardChartUserRelationMapper.insertBatch(relationList);
        return res;
    }

    public void saveChartConfig(UserChartConfigReq req) {

        //更新 = delete+insert
        LambdaQueryWrapper<DashboardChartUserRelation> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(DashboardChartUserRelation::getUserId, req.getUserId());
        dashboardChartUserRelationMapper.delete(wrapper);

        ArrayList<DashboardChartUserRelation> relationList = new ArrayList<>();
        for (Integer chartId : req.getConfig()) {
            DashboardChartUserRelation relation = new DashboardChartUserRelation();
            relation.setChartId(chartId);
            relation.setUserId(req.getUserId());
            relation.setCreateBy(req.getUserId());
            relation.setGmtCreate(new Date());
            relation.setModifiedBy(req.getUserId());
            relation.setGmtModified(new Date());
            relationList.add(relation);
        }
        dashboardChartUserRelationMapper.insertBatch(relationList);

    }
}
