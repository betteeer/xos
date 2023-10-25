package com.inossem.oms.common.controller;

import com.inossem.oms.base.common.domain.SpecialConfig;
import com.inossem.oms.common.service.SpecialConfigService;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/common/specialConfig")
public class SpecialConfigController {
    @Resource
    private SpecialConfigService specialConfigService;
    @GetMapping("/pdfConfig")
    public AjaxResult<SpecialConfig> findOne(@RequestParam String companyCode){
        return AjaxResult.success().withData(specialConfigService.findOne(companyCode));
    }
    @PostMapping("/pdfConfig/update")
    public AjaxResult<Boolean> update(@RequestBody SpecialConfig specialConfig){
        return AjaxResult.success().withData(specialConfigService.update(specialConfig));
    }
}
