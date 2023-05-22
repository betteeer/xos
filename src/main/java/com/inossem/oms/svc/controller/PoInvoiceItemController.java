package com.inossem.oms.svc.controller;

import com.inossem.oms.base.svc.domain.PoInvoiceItem;
import com.inossem.oms.svc.service.PoInvoiceItemService;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 【请填写功能名称】Controller
 * 
 * @author ruoyi
 * @date 2022-12-09
 */
@RestController
@RequestMapping("/svc/po/invoice/item")
public class PoInvoiceItemController extends BaseController
{
    @Resource
    private PoInvoiceItemService poInvoiceItemService;

    /**
     * 查询【请填写功能名称】列表
     */
    @GetMapping("/list")
    public TableDataInfo list(PoInvoiceItem poInvoiceItem)
    {
        startPage();
        List<PoInvoiceItem> list = poInvoiceItemService.selectPoInvoiceItemList(poInvoiceItem);
        return getDataTable(list);
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(poInvoiceItemService.selectPoInvoiceItemById(id));
    }

    /**
     * 新增【请填写功能名称】
     */
    @PostMapping
    public AjaxResult add(@RequestBody PoInvoiceItem poInvoiceItem)
    {
        return toAjax(poInvoiceItemService.insertPoInvoiceItem(poInvoiceItem));
    }

    /**
     * 修改【请填写功能名称】
     */
    @PutMapping
    public AjaxResult edit(@RequestBody PoInvoiceItem poInvoiceItem)
    {
        return toAjax(poInvoiceItemService.updatePoInvoiceItem(poInvoiceItem));
    }

    /**
     * 删除【请填写功能名称】
     */
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(poInvoiceItemService.deletePoInvoiceItemByIds(ids));
    }
}
