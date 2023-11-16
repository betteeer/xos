package com.inossem.oms.svc.controller;

import com.inossem.oms.base.svc.domain.DTO.SoItemSearchFormDTO;
import com.inossem.oms.svc.service.SoItemService;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/svc/so/item")
public class SoItemController extends BaseController {
    @Resource
    private SoItemService soItemService;
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody @Validated SoItemSearchFormDTO form ) {
        startPage();
        return getDataTable(soItemService.getList(form));
    }

}
