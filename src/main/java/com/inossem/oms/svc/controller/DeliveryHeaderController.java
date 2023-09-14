package com.inossem.oms.svc.controller;

import com.inossem.oms.base.svc.domain.DTO.DeliveryHeaderFormDTO;
import com.inossem.oms.base.svc.domain.DTO.DeliveryItemFormDTO;
import com.inossem.oms.svc.service.DeliveryHeaderService;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/svc/delivery")
public class DeliveryHeaderController extends BaseController {

    @Resource
    private DeliveryHeaderService deliveryHeaderService;

    @PostMapping("/list")
    public TableDataInfo getList(@RequestBody @Validated DeliveryHeaderFormDTO form) {
        startPage();
        return getDataTable(deliveryHeaderService.getList(form));
    }
    @PostMapping("/item/list")
    public TableDataInfo getItem(@RequestBody @Validated DeliveryItemFormDTO form) {
        startPage();
        return getDataTable(deliveryHeaderService.getItem(form));
    }
}
