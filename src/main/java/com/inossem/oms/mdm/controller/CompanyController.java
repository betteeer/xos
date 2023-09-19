package com.inossem.oms.mdm.controller;


import com.inossem.oms.base.svc.domain.Company;
import com.inossem.oms.mdm.service.CompanyService;
import com.inossem.sco.common.core.utils.StringUtils;
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

@RestController
@RequestMapping("/mdm/setting/company")
@Slf4j
@Api(tags = {"company op"})
public class CompanyController extends BaseController {

    @Resource
    private CompanyService companyService;

    @ApiOperation(value = "create company",notes = "create company")
    @PostMapping("/create")
    public AjaxResult create(@Valid @RequestBody Company company, BindingResult result) {
        if (result.hasErrors()){
            return AjaxResult.error(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return AjaxResult.success("create success").withData(companyService.create(company));
    }

    @ApiOperation(value = "modify company",notes = "modify company")
    @PostMapping("/modify")
    public AjaxResult modifyCompany(@Valid @RequestBody Company company, BindingResult result) {
        if (result.hasErrors()){
            return AjaxResult.error(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return AjaxResult.success("modify success").withData(companyService.modify(company));
    }

    @ApiOperation(value = "get company list",notes = "get company list")
    @GetMapping("/list")
    public AjaxResult list(@RequestParam("userName") String userName) {
        if(StringUtils.isEmpty(userName)){
            return AjaxResult.error("error params userName is empty");
        }
        return AjaxResult.success().withData(companyService. list(userName));
    }

    @ApiOperation(value = "get company detail",notes = "get company detail")
    @GetMapping("/get/{id}")
    public AjaxResult getCompany(@PathVariable("id") Long id ,@RequestParam("companyCode") String companyCode) {
        return AjaxResult.success("some-information").withData(companyService.getCompany(companyCode));
    }
}
