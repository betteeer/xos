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
    @PostMapping("/cancel")
    public AjaxResult cancelOrder(@RequestBody StoFormDTO stoFormDTO) {
        if (StringUtils.isEmpty(stoFormDTO.getStoNumber())) {
            return AjaxResult.error("stoNumber cannot be empty");
        }
        return AjaxResult.success().withData(stoHeaderService.cancelOrder(stoFormDTO.getStoNumber()));
    }
    @PostMapping("/transfer")
    public AjaxResult transferOrder(@RequestBody StoFormDTO stoFormDTO) {
        return AjaxResult.success().withData(stoHeaderService.transferOrder(stoFormDTO));
    }

    @PostMapping("/receive")
    public AjaxResult receiveOrder(@RequestBody StoFormDTO stoFormDTO) {
        return AjaxResult.success().withData(stoHeaderService.receiveOrder(stoFormDTO));
    }
    @PostMapping("/revertToOpen")
    public AjaxResult revertToOpen(@RequestBody StoFormDTO stoFormDTO) {
        return AjaxResult.success().withData(stoHeaderService.revertToOpen(stoFormDTO));
    }

    @PostMapping("/revertToIntransit")
    public AjaxResult revertToIntransit(@RequestBody StoFormDTO stoFormDTO) {
        return AjaxResult.success().withData(stoHeaderService.revertToIntransit(stoFormDTO));
    }

    @PostMapping("/freeField/save")
    public AjaxResult saveFreeField(@RequestBody StoFormDTO stoFormDTO) {
        return AjaxResult.success().withData(stoHeaderService.saveFreeField(stoFormDTO));
    }

}
