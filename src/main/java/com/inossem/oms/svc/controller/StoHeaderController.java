package com.inossem.oms.svc.controller;

import com.inossem.oms.base.svc.domain.DTO.StoFormDTO;
import com.inossem.oms.svc.service.StoHeaderService;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/svc/sto")
public class StoHeaderController extends BaseController {

    @Resource
    private StoHeaderService stoHeaderService;

    @PostMapping("/list")
    public void getList() {
        stoHeaderService.getList();
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
