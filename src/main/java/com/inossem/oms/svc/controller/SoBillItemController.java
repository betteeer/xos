package com.inossem.oms.svc.controller;


import com.inossem.oms.base.svc.domain.SoBillItem;
import com.inossem.oms.svc.service.SoBillItemService;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 【开票明细】Controller
 * 
 * @author guoh
 * @date 2022-11-20
 */
@RestController
@RequestMapping("/svc/billing/item")
@Slf4j
public class SoBillItemController extends BaseController
{
    @Autowired
    private SoBillItemService soBillItemService;

    /**
     * 查询【请填写功能名称】列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SoBillItem soBillItem)
    {
        startPage();
        List<SoBillItem> list = soBillItemService.selectSoBillItemList(soBillItem);
        return getDataTable(list);
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(soBillItemService.selectSoBillItemById(id));
    }

    /**
     * 新增【请填写功能名称】
     */
    @PostMapping
    public AjaxResult add(@RequestBody SoBillItem soBillItem)
    {
        return toAjax(soBillItemService.insertSoBillItem(soBillItem));
    }

    /**
     * 修改【请填写功能名称】
     */
    @PutMapping
    public AjaxResult edit(@RequestBody SoBillItem soBillItem)
    {
        return toAjax(soBillItemService.updateSoBillItem(soBillItem));
    }

    /**
     * 删除【请填写功能名称】
     */
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(soBillItemService.deleteSoBillItemByIds(ids));
    }
}
