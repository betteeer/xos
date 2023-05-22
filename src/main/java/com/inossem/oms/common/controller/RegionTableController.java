package com.inossem.oms.common.controller;

import com.inossem.oms.base.common.domain.RegionTable;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import com.inossem.oms.common.service.RegionTableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 【请填写功能名称】Controller
 * 
 * @author shigf
 * @date 2022-11-04
 */
@RestController
@RequestMapping("/common/common/table")
@Api(tags = {"RegionTable字典相关接口"})
public class RegionTableController extends BaseController {

    @Resource
    private RegionTableService regionTableService;

    /**
     * 查询【请填写功能名称】列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "RegionTable字典列表", notes = "RegionTable字典列表")
    public TableDataInfo list(RegionTable regionTable) {
        startPage();
        List<RegionTable> list = regionTableService.selectRegionTableList(regionTable);
        return getDataTable(list);
    }
}