package com.inossem.oms.svc.controller;

import com.alibaba.druid.util.StringUtils;
import com.inossem.oms.base.svc.domain.SystemConnect;
import com.inossem.oms.base.utils.poi.ExcelUtil;
import com.inossem.oms.svc.service.SystemConnectService;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * system connect infoController
 *
 * @author shigf
 * @date 2022-12-10
 */
@RestController
@RequestMapping("/svc/sysConnect")
public class SystemConnectController extends BaseController {
    @Resource
    private SystemConnectService systemConnectService;

    /**
     * 查询system connect info列表
     */
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody SystemConnect systemConnect) {
        if (StringUtils.isEmpty(systemConnect.getCompanyCode())) {
            return getDataTable(new ArrayList<>());
        }
        startPage();
        List<SystemConnect> list = systemConnectService.selectSyctemConectList(systemConnect);
        return getDataTable(list);
    }


    /**
     * feign接口调用
     */
    @PostMapping("/lists")
    public List<SystemConnect> lists(@RequestBody SystemConnect systemConnect) {
        List<SystemConnect> list = systemConnectService.selectSyctemConectList(systemConnect);
        return list;
    }

    /**
     * 导出system connect info列表
     */
    @PostMapping("/export")
    public void export(HttpServletResponse response, SystemConnect systemConnect) {
        List<SystemConnect> list = systemConnectService.selectSyctemConectList(systemConnect);
        ExcelUtil<SystemConnect> util = new ExcelUtil<SystemConnect>(SystemConnect.class);
        util.exportExcel(response, list, "system connect info数据");
    }

    /**
     * 获取system connect info详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(systemConnectService.selectSyctemConectById(id));
    }

    /**
     * 新增system connect info
     */
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody SystemConnect systemConnect) {
        return toAjax(systemConnectService.insertSyctemConect(systemConnect));
    }

    /**
     * 修改system connect info
     */
    @PostMapping(value = "/edit")
    public AjaxResult edit(@RequestBody SystemConnect systemConnect) {
        return toAjax(systemConnectService.updateSyctemConect(systemConnect));
    }

    /**
     * 删除system connect info
     */
    @PostMapping("/del/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(systemConnectService.deleteSyctemConectById(id));
    }
}
