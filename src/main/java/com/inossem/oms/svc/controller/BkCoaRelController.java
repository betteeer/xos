package com.inossem.oms.svc.controller;

import com.alibaba.druid.util.StringUtils;
import com.inossem.oms.svc.service.BkCoaRelService;
import com.inossem.oms.base.svc.domain.BkCoaRel;
import com.inossem.oms.base.utils.poi.ExcelUtil;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * FI COA mappingController
 *
 * @author shigf
 * @date 2022-12-10
 */
@RestController
@RequestMapping("/svc/coaRel")
@Slf4j
public class BkCoaRelController extends BaseController {
    @Resource
    private BkCoaRelService bkCoaRelService;

    @GetMapping("/sync")
    public AjaxResult sync(String companyCode) {
        log.info(">>>>> sync 接口入参,companyCode :{}", companyCode);
        bkCoaRelService.sync(companyCode);
        return AjaxResult.success();
    }

    /**
     * 查询FI COA mapping列表
     */
    @GetMapping("/list")
    public TableDataInfo list(BkCoaRel bkCoaRel) {
        if (StringUtils.isEmpty(bkCoaRel.getCompanyCode())) {
            return getDataTable(new ArrayList<>());
        }
        startPage();
        List<BkCoaRel> list = bkCoaRelService.selectBkCoaRelList(bkCoaRel);
        return getDataTable(list);
    }

    /**
     * 导出FI COA mapping列表
     */
    @PostMapping("/export")
    public void export(HttpServletResponse response, BkCoaRel bkCoaRel) {
        List<BkCoaRel> list = bkCoaRelService.selectBkCoaRelList(bkCoaRel);
        ExcelUtil<BkCoaRel> util = new ExcelUtil<BkCoaRel>(BkCoaRel.class);
        util.exportExcel(response, list, "FI COA mapping数据");
    }

    /**
     * 获取FI COA mapping详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(bkCoaRelService.selectBkCoaRelById(id));
    }

    /**
     * 新增FI COA mapping
     */
    @PostMapping
    public AjaxResult add(@RequestBody BkCoaRel bkCoaRel) {
        return toAjax(bkCoaRelService.insertBkCoaRel(bkCoaRel));
    }

    /**
     * 修改FI COA mapping
     */
    @PutMapping
    public AjaxResult edit(@RequestBody BkCoaRel bkCoaRel) {
        return toAjax(bkCoaRelService.updateBkCoaRel(bkCoaRel));
    }

    /**
     * 删除FI COA mapping
     */
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(bkCoaRelService.deleteBkCoaRelByIds(ids));
    }
}
