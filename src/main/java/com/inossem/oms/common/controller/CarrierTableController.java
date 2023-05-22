package com.inossem.oms.common.controller;

import com.inossem.oms.base.common.domain.CarrierTable;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import com.inossem.oms.common.service.CarrierTableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/common/common/carrier")
@Slf4j
@Api(tags = {"carrier字典相关接口"})
public class CarrierTableController extends BaseController
{
    @Autowired
    private CarrierTableService carrierTableService;


    @GetMapping("/list")
    @ApiOperation(value = "carrier字典列表", notes = "carrier字典列表")
    public TableDataInfo list(CarrierTable carrierTable)
    {
        startPage();
        List<CarrierTable> list = carrierTableService.selectCarrierTableList(carrierTable);
        return getDataTable(list);
    }
}
