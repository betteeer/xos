package com.inossem.oms.svc.controller;

import com.inossem.oms.base.svc.domain.PoItem;
import com.inossem.oms.svc.service.PoItemService;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 【请填写功能名称】Controller
 * 
 * @author shigf
 * @date 2022-11-04
 */
@RestController
@RequestMapping("/svc/po/item")
public class PoItemController extends BaseController
{
    @Resource
    private PoItemService poItemService;

    /**
     * 查询【请填写功能名称】列表
     */
    @GetMapping("/list")
    public TableDataInfo list(PoItem poItem)
    {
        startPage();
        List<PoItem> list = poItemService.selectPoItemList(poItem);
        return getDataTable(list);
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(poItemService.selectPoItemById(id));
    }

    /**
     * 新增【请填写功能名称】
     */
    @PostMapping
    public AjaxResult add(@RequestBody PoItem poItem)
    {
        return toAjax(poItemService.insertPoItem(poItem));
    }

    /**
     * 修改【请填写功能名称】
     */
    @PutMapping
    public AjaxResult edit(@RequestBody PoItem poItem)
    {
        return toAjax(poItemService.updatePoItem(poItem));
    }

    /**
     * 删除【请填写功能名称】
     */
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(poItemService.deletePoItemByIds(ids));
    }
}
