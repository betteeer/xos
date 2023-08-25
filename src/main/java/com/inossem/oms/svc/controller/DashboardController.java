package com.inossem.oms.svc.controller;

import com.inossem.oms.base.svc.domain.dashboard.req.DashboardReq;
import com.inossem.oms.base.svc.domain.dashboard.req.MostInventoryReq;
import com.inossem.oms.base.svc.domain.dashboard.vo.*;
import com.inossem.oms.svc.service.DashboardService;
import com.inossem.sco.common.core.domain.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/svc/dashboard")
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    @ApiOperation(value = "title bar", notes = "获取title bar数据")
    @PostMapping("/summary")
    public R<TitleBarVo> getTitleBar(@RequestBody DashboardReq req)  {
        return R.ok(dashboardService.getTitleBar(req));
    }

    @ApiOperation(value = "sales revenue chart", notes = "获取sales revenue表数据")
    @PostMapping("/salesRevenue")
    public R<SalesRevenueVo> getSalesRevenue(@RequestBody DashboardReq req) {
        return R.ok(dashboardService.getSalesRevenue(req));
    }

    @ApiOperation(value = "sales percentage chart", notes = "获取sales percentage表数据")
    @PostMapping("/salesPercentage")
    public R<List<SalesPercentageVo>> getSalesPercentage(@RequestBody DashboardReq req) {
        return R.ok(dashboardService.getSalesPercentage(req));
    }

    @ApiOperation(value = "best seller chart", notes = "获取best seller表数据")
    @PostMapping("/bestSeller")
    public R<List<BestSellerVo>> getBestSeller(@RequestBody DashboardReq req) {
        return R.ok(dashboardService.getBestSeller(req));
    }

    @ApiOperation(value = "most on hand inventory chart", notes = "获取most on hand inventory表数据")
    @PostMapping("/mostInventory")
    public R<MostInventoryVo> getMostInventory(@RequestBody MostInventoryReq res){
        return R.ok(dashboardService.getMostInventory(res));
    }

}
