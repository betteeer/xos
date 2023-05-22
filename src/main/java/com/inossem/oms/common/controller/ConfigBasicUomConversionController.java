package com.inossem.oms.common.controller;

import com.inossem.oms.base.common.domain.ConfigBasicUomConversion;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import com.inossem.oms.common.service.ConfigBasicUomConversionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 【请填写功能名称】Controller
 * 
 * @author shigf
 * @date 2022-11-04
 */
@RestController
@RequestMapping("/common/common/conversion")
@Api(tags = {"conversion字典相关接口"})
public class ConfigBasicUomConversionController extends BaseController
{
    @Autowired
    private ConfigBasicUomConversionService configBasicUomConversionService;

    /**
     * 查询【请填写功能名称】列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "BasicUom字典列表", notes = "carrier字典列表")
    public TableDataInfo list(ConfigBasicUomConversion configBasicUomConversion)
    {
        startPage();
        List<ConfigBasicUomConversion> list = configBasicUomConversionService.selectConfigBasicUomConversionList(configBasicUomConversion);
        return getDataTable(list);
    }

}
