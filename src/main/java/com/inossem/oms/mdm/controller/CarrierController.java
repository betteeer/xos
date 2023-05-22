package com.inossem.oms.mdm.controller;

import com.inossem.oms.base.svc.domain.Carrier;
import com.inossem.oms.mdm.service.CarrierService;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Objects;

/**
 * @author zoutong
 * @date 2022/10/17
 **/
@RestController
@RequestMapping("/mdm/setting/carrier")
@Slf4j
@Api(tags = {"carrier op"})
public class CarrierController extends BaseController {

    @Resource
    private CarrierService carrierService;

    @ApiOperation(value = "create carrier",notes = "create carrier")
    @PostMapping("/create")
    public AjaxResult create(@Valid @RequestBody Carrier carrier, BindingResult result) {
        if (result.hasErrors()){
            return AjaxResult.error(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return AjaxResult.success("create success").withData(carrierService.create(carrier));
    }

    @ApiOperation(value = "modify carrier",notes = "modify carrier")
    @PostMapping("/modify")
    public AjaxResult modifyCarrier(@Valid @RequestBody Carrier carrier, BindingResult result) {
        if (result.hasErrors()){
            return AjaxResult.error(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return AjaxResult.success("modify success").withData(carrierService.modify(carrier));
    }

    @ApiOperation(value = "get carrier list",notes = "get carrier list")
    @GetMapping("/list")
    public AjaxResult list(@RequestParam(value = "companyCode") String companyCode) {
        return AjaxResult.success().withData(carrierService.list(companyCode));
    }

    @ApiOperation(value = "get one carrier detail",notes = "get one carrier detail")
    @GetMapping("/get/{id}")
    public AjaxResult getCarrier(@PathVariable("id") Long id) {
        return AjaxResult.success("some-information").withData(carrierService.getCarrier(id));
    }

    @ApiOperation(value = "delete one carrier",notes = "delete one carrier")
    @GetMapping("/delete/{id}")
    public AjaxResult deleteCarrier(@PathVariable("id") Long id) {
        return AjaxResult.success("some-information").withData(carrierService.delete(id));
    }
}
