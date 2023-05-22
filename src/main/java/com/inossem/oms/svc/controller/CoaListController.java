package com.inossem.oms.svc.controller;

import com.inossem.oms.base.svc.domain.CoaList;
import com.inossem.oms.base.utils.poi.ExcelUtil;
import com.inossem.oms.svc.service.CoaListService;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * system connect infoController
 * 
 * @author shigf
 * @date 2022-12-10
 */
@RestController
@RequestMapping("/svc/coaList")
public class CoaListController extends BaseController
{
    @Resource
    private CoaListService coaListService;

    /**
     * 查询system connect info列表
     */
    @GetMapping("/list")
    public TableDataInfo list(CoaList coaList)
    {
        startPage();
        List<CoaList> list = coaListService.selectCoaListList(coaList);
        return getDataTable(list);
    }

    /**
     * 导出system connect info列表
     */
    @PostMapping("/export")
    public void export(HttpServletResponse response, CoaList coaList)
    {
        List<CoaList> list = coaListService.selectCoaListList(coaList);
        ExcelUtil<CoaList> util = new ExcelUtil<CoaList>(CoaList.class);
        util.exportExcel(response, list, "system connect info数据");
    }

    /**
     * 获取system connect info详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(coaListService.selectCoaListById(id));
    }

    /**
     * 新增system connect info
     */
    @PostMapping
    public AjaxResult add(@RequestBody CoaList coaList)
    {
        return toAjax(coaListService.insertCoaList(coaList));
    }

    /**
     * 修改system connect info
     */
    @PutMapping
    public AjaxResult edit(@RequestBody CoaList coaList)
    {
        return toAjax(coaListService.updateCoaList(coaList));
    }

    /**
     * 删除system connect info
     */
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(coaListService.deleteCoaListByIds(ids));
    }
}
