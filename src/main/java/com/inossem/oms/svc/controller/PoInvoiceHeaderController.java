package com.inossem.oms.svc.controller;

import com.inossem.oms.base.svc.domain.PoInvoiceHeader;
import com.inossem.oms.svc.service.PoInvoiceHeaderService;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 【请填写功能名称】Controller
 * 
 * @author ruoyi
 * @date 2022-12-09
 */
@RestController
@RequestMapping("/svc/po/invoice/header")
public class PoInvoiceHeaderController extends BaseController
{
    @Resource
    private PoInvoiceHeaderService poInvoiceHeaderService;


    /**
     * 新增【po开票】
     */
    @PostMapping(value = "/create")
    public AjaxResult create(@RequestBody PoInvoiceHeader poInvoiceHeader)
    {
        return toAjax(poInvoiceHeaderService.insertPoInvoiceHeader(poInvoiceHeader));
    }



//    /**
//     * 查询【请填写功能名称】列表
//     */
//    @GetMapping("/list")
//    public TableDataInfo list(PoInvoiceHeader poInvoiceHeader)
//    {
//        startPage();
//        List<PoInvoiceHeader> list = poInvoiceHeaderService.selectPoInvoiceHeaderList(poInvoiceHeader);
//        return getDataTable(list);
//    }
//
//
//    /**
//     * 获取【请填写功能名称】详细信息
//     */
//    @GetMapping(value = "/{id}")
//    public AjaxResult getInfo(@PathVariable("id") Long id)
//    {
//        return AjaxResult.success(poInvoiceHeaderService.selectPoInvoiceHeaderById(id));
//    }
//
//    /**
//     * 新增【请填写功能名称】
//     */
//    @PostMapping
//    public AjaxResult add(@RequestBody PoInvoiceHeader poInvoiceHeader)
//    {
//        return toAjax(poInvoiceHeaderService.insertPoInvoiceHeader(poInvoiceHeader));
//    }
//
//    /**
//     * 修改【请填写功能名称】
//     */
//    @PutMapping
//    public AjaxResult edit(@RequestBody PoInvoiceHeader poInvoiceHeader)
//    {
//        return toAjax(poInvoiceHeaderService.updatePoInvoiceHeader(poInvoiceHeader));
//    }
//
//    /**
//     * 删除【请填写功能名称】
//     */
//	@DeleteMapping("/{ids}")
//    public AjaxResult remove(@PathVariable Long[] ids)
//    {
//        return toAjax(poInvoiceHeaderService.deletePoInvoiceHeaderByIds(ids));
//    }
}
