package com.inossem.oms.svc.controller;

import com.inossem.oms.base.svc.domain.dashboard.req.UserChartConfigReq;
import com.inossem.oms.svc.service.DashboardChartConfigService;
import com.inossem.sco.common.core.domain.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/svc/chartConfig")
public class DashboardChartConfigController {

    @Resource
    private DashboardChartConfigService dashboardChartConfigService;

    @GetMapping("/list")
    @ApiOperation(value = "chart config list", notes = "获取dashboard所有chart配置")
    public R<List<Object>> getChartConfigList() {
        return R.ok(dashboardChartConfigService.getChartConfigList());
    }

    @GetMapping
    @ApiOperation(value = "user chart config", notes = "获取某用户dashboard chart配置")
    public R<List<Object>> getChartConfigByUserId(@RequestParam("userId") Integer userId) {
        return R.ok(dashboardChartConfigService.getChartConfigByUserId(userId));
    }

    @PostMapping("/save")
    @ApiOperation(value = "save chart config", notes = "保存某用户dashboard chart配置")
    public R saveChartConfig(@RequestBody UserChartConfigReq req) {
        dashboardChartConfigService.saveChartConfig(req);
        return R.ok();
    }
}
