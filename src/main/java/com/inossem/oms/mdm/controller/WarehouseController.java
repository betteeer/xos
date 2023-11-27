package com.inossem.oms.mdm.controller;

import com.inossem.oms.base.svc.domain.VO.WarehouseVO;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import com.inossem.oms.mdm.service.WarehouseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author zoutong
 * @date 2022/10/17
 **/
@RestController
@RequestMapping("/mdm/setting/warehouse")
@Slf4j
@Api(tags = {"warehouse op"})
public class WarehouseController extends BaseController {

    @Resource
    private WarehouseService warehouseService;

    @ApiOperation(value = "create warehouse",notes = "create warehouse")
    @PostMapping("/create")
    public AjaxResult Create(@RequestHeader(name = "X-Userid") String userId, @RequestBody WarehouseVO warehouse) {
        return AjaxResult.success("create success").withData(warehouseService.Create(warehouse, userId));
    }

    @ApiOperation(value = "modify warehouse",notes = "modify warehouse")
    @PostMapping("/modify")
    public AjaxResult ModifyWarehouse(@RequestHeader(name = "X-Userid") String userId, @RequestBody WarehouseVO warehouse) {
        return AjaxResult.success("modify success").withData(warehouseService.Modify(warehouse, userId));
    }

    @ApiOperation(value = "get warehouse list",notes = "get warehouse list")
    @GetMapping("/list")
    public AjaxResult list(@RequestParam(value = "companyCode")String companyCode) {
        return AjaxResult.success().withData(warehouseService.list(companyCode));
    }

    @ApiOperation(value = "get warehouse detail",notes = "get warehouse detail")
    @GetMapping("/get/{skuCode}")
    public AjaxResult getWarehouse(@PathVariable("skuCode") String skuCode) {
        return AjaxResult.success("some-information").withData(null);
    }
}
