package com.inossem.oms.common.controller;

import com.inossem.oms.base.common.domain.ConfigUom;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import com.inossem.oms.common.service.ConfigUomService;
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
@RequestMapping("/common/common/uom")
@Api(tags = {"ConfigUom字典相关接口"})
public class ConfigUomController extends BaseController
{
    @Resource
    private ConfigUomService configUomService;

    /**
     * 查询【请填写功能名称】列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "ConfigUom字典列表", notes = "ConfigUom字典列表")
    public TableDataInfo list(ConfigUom configUom)
    {
        startPage();
        List<ConfigUom> list = configUomService.selectConfigUomList(configUom);
        return getDataTable(list);
    }

}
