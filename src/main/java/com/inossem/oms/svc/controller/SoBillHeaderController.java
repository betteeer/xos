package com.inossem.oms.svc.controller;

import com.inossem.oms.base.svc.domain.DTO.SoBillingHeaderFormDTO;
import com.inossem.oms.base.svc.domain.SoBillHeader;
import com.inossem.oms.svc.service.IDeliveryHeaderService;
import com.inossem.oms.svc.service.SoBillHeaderService;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 【开票】Controller
 *
 * @author guoh
 * @date 2022-11-20
 */
@RestController
@RequestMapping("/svc/billing")
@Slf4j
public class SoBillHeaderController extends BaseController {

    @Autowired
    private IDeliveryHeaderService deliveryHeaderService;

    @Autowired
    private SoBillHeaderService soBillHeaderService;

    /**
     * 查询so_bill头部信息
     */
    @GetMapping("/headerInfo/{soNumber}/{companyCode}")
    public AjaxResult getSoBillHeader(@PathVariable("soNumber") String soNumber,
                                      @PathVariable("companyCode") String companyCode) {
        return AjaxResult.success(soBillHeaderService.getSoBillHeader(soNumber, companyCode));
    }

    /**
     * 查询【未开票】列表
     */
    @GetMapping("/uninvoicedList/{soNumber}/{companyCode}")
    public AjaxResult uninvoicedList(@PathVariable("soNumber") String soNumber,
                                     @PathVariable("companyCode") String companyCode) {
        return AjaxResult.success().withData(deliveryHeaderService.selectUnInvoiceList(soNumber, companyCode));
    }

    /**
     * 查询【已开票】列表
     */
    @GetMapping("/fullyList/{soNumber}/{companyCode}")
    public AjaxResult list(@PathVariable("soNumber") String soNumber,
                           @PathVariable("companyCode") String companyCode) {
        return AjaxResult.success().withData(deliveryHeaderService.selectFullyBillList(soNumber, companyCode));
    }

    /**
     * 新增【开票】
     */
    @PostMapping("/create")
    public AjaxResult create(@RequestBody List<SoBillHeader> soBillHeader) {
        return toAjax(soBillHeaderService.insertSoBillHeader(soBillHeader));
    }
    @PostMapping("/list")
    public TableDataInfo getList(@RequestBody @Validated SoBillingHeaderFormDTO form) {
        startPage();
        return getDataTable(soBillHeaderService.getList(form));
    }

}
