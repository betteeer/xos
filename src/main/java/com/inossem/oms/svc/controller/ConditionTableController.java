package com.inossem.oms.svc.controller;

import com.inossem.oms.base.svc.domain.ConditionTable;
import com.inossem.oms.base.utils.poi.ExcelUtil;
import com.inossem.oms.svc.service.ConditionTableService;
import com.inossem.sco.common.core.domain.R;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * system connect infoController
 *
 * @author shigf
 * @date 2022-12-10
 */
@RestController
@RequestMapping("/svc/conditionTable")
public class ConditionTableController extends BaseController
{
    @Autowired
    private ConditionTableService conditionTableService;

    /**
     * 查询system connect info列表
     */
    @RequestMapping("/list")
    public TableDataInfo list(ConditionTable conditionTable)
    {
        startPage();
        List<ConditionTable> list = conditionTableService.selectConditionTableList(conditionTable);
        return getDataTable(list);
    }

    /**
     * 查询system connect info列表
     */
    @GetMapping("/innerList")
    public R<List<ConditionTable>> innerList(String companyCode, String type)
    {
        startPage();
        ConditionTable table = new ConditionTable();
        table.setCompanyCode(companyCode);
        table.setIsDeleted(0);
        table.setConditionType(type);
        List<ConditionTable> list = conditionTableService.selectConditionTableList(table);
        return R.ok(list);
    }

    /**
     * 导出system connect info列表
     */
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConditionTable conditionTable)
    {
        List<ConditionTable> list = conditionTableService.selectConditionTableList(conditionTable);
        ExcelUtil<ConditionTable> util = new ExcelUtil<ConditionTable>(ConditionTable.class);
        util.exportExcel(response, list, "system connect info数据");
    }

    /**
     * 获取system connect info详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(conditionTableService.selectConditionTableById(id));
    }

    /**
     * 新增system connect info
     */
    @PostMapping
    public AjaxResult add(@RequestBody ConditionTable conditionTable)
    {
        return toAjax(conditionTableService.insertConditionTable(conditionTable));
    }

    /**
     * 修改system connect info
     */
    @PutMapping
    public AjaxResult edit(@RequestBody ConditionTable conditionTable)
    {
        return toAjax(conditionTableService.updateConditionTable(conditionTable));
    }

    /**
     * 删除system connect info
     */
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(conditionTableService.deleteConditionTableByIds(ids));
    }
}
