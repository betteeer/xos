package com.inossem.oms.svc.controller;

import com.inossem.oms.base.svc.domain.DTO.DeliveryHeaderFormDTO;
import com.inossem.oms.base.svc.domain.DTO.DeliveryItemFormDTO;
import com.inossem.oms.svc.service.DeliveryHeaderService;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/next/number")
    public AjaxResult<String> getNextDeliveryNumber(@RequestParam String soNumber, @RequestParam String companyCode) {
        String num = deliveryHeaderService.getNextDeliveryNumber(soNumber, companyCode);
        return AjaxResult.success().withData(num);
    }

    @PostMapping("/so/list")
    public TableDataInfo getSoList(@RequestBody @Validated DeliveryHeaderFormDTO form) {
        startPage();
        return getDataTable(deliveryHeaderService.getSoList(form));
    }
    @PostMapping("/so/item/list")
    public TableDataInfo getSoItem(@RequestBody @Validated DeliveryItemFormDTO form) {
        startPage();
        return getDataTable(deliveryHeaderService.getSoItem(form));
    }
}
