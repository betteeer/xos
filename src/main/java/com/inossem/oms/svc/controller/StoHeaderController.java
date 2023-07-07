package com.inossem.oms.svc.controller;

import com.inossem.oms.base.svc.domain.DTO.StoFormDTO;
import com.inossem.oms.base.svc.domain.DTO.StoSearchFormDTO;
import com.inossem.oms.svc.service.StoHeaderService;
import com.inossem.sco.common.core.utils.StringUtils;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/svc/sto")
public class StoHeaderController extends BaseController {

    @Resource
    private StoHeaderService stoHeaderService;

    @PostMapping("/list")
    public TableDataInfo getList(@RequestBody @Validated StoSearchFormDTO searchFormDTO) {
        startPage();
        return getDataTable(stoHeaderService.getList(searchFormDTO));
    }

    @GetMapping("/detail")
    public AjaxResult detail(@RequestParam(required = false) String stoNumber, @RequestParam(required = false) String companyCode) {
        if (StringUtils.isEmpty(stoNumber)) {
            return AjaxResult.error("stoNumber cannot be empty");
        }
        if (StringUtils.isEmpty(companyCode)) {
            return AjaxResult.error("companyCode cannot be empty");
        }
        return AjaxResult.success().withData(stoHeaderService.getDetail(stoNumber, companyCode));
    }

    @PostMapping("/save")
    public AjaxResult saveOrder(@RequestBody StoFormDTO stoFormDTO) {
        return AjaxResult.success().withData(stoHeaderService.saveOrder(stoFormDTO));
    }
//    @PostMapping("/cancel")
//    @PostMapping("/confirm")
//    @PostMapping("/receive")
//    @PostMapping("/revertToOpen")
//    @PostMapping("/revertToIntransit")
}
