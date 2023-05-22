package com.inossem.oms.common.controller;

import com.inossem.oms.base.common.domain.CountryTable;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import com.inossem.oms.common.service.CountryTableService;
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
@RequestMapping("/common/common/country")
@Api(tags = {"countryTable字典相关接口"})
public class CountryTableController extends BaseController {
    @Resource
    private CountryTableService countryTableService;

    /**
     * 查询【请填写功能名称】列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "countryTable字典列表", notes = "countryTable字典列表")
    public TableDataInfo list(CountryTable countryTable) {
        startPage();
        List<CountryTable> list = countryTableService.selectCountryTableList(countryTable);
        return getDataTable(list);
    }
}
