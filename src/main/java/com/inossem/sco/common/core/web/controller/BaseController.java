
package com.inossem.sco.common.core.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.inossem.sco.common.core.utils.DateUtils;
import com.inossem.sco.common.core.utils.PageUtils;
import com.inossem.sco.common.core.web.domain.AjaxPageResult;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import com.inossem.sco.common.core.web.domain.PageVO;
import com.inossem.sco.common.core.web.page.PageDomain;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import com.inossem.sco.common.core.web.page.TableSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;

public class BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BaseController() {
    }


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            public void setAsText(String text) {
                this.setValue(DateUtils.parseDate(text));
            }
        });
    }

    protected void startPage() {
        PageUtils.startPage();
    }

    protected void clearPage() {
        PageUtils.clearPage();
    }

    protected TableDataInfo getDataTable(List<?> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setRows(list);
        rspData.setMsg("查询成功");
        rspData.setTotal((new PageInfo(list)).getTotal());
        return rspData;
    }

    protected AjaxResult toAjax(int rows) {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    protected AjaxResult toAjax(boolean result) {
        return result ? this.success() : this.error();
    }

    public AjaxResult success() {
        return AjaxResult.success();
    }

    public AjaxResult error() {
        return AjaxResult.error();
    }

    public AjaxResult success(String message) {
        return AjaxResult.success(message);
    }

    public AjaxResult error(String message) {
        return AjaxResult.error(message);
    }

    protected Page<?> preparePage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        return new Page((long)pageDomain.getPageNum(), (long)pageDomain.getPageSize());
    }

    protected Page<?> preparePage(PageVO pv) {
        long pageNum = null == pv ? 1L : (null == pv.getPageNum() ? 1L : (long)pv.getPageNum());
        long pageSize = null == pv ? 10L : (null == pv.getPageSize() ? 10L : (long)pv.getPageSize());
        return new Page(pageNum, pageSize);
    }

    protected Page<?> preparePage(Integer pageNum, Integer pageSize) {
        return new Page((long)pageNum, (long)pageSize);
    }

    protected AjaxPageResult<?> createPageResult(TableDataInfo tableDataInfo) {
        Long current = 0L;
        Long pageSize = 0L;
        return (new AjaxPageResult()).page(current, pageSize, tableDataInfo.getTotal()).withData(tableDataInfo.getRows());
    }

    protected AjaxPageResult<?> createPageResult(IPage ipage, Object data) {
        return (new AjaxPageResult()).page(ipage.getCurrent(), ipage.getSize(), ipage.getTotal()).withData(data);
    }

    protected AjaxPageResult<?> createPageResult(IPage ipage) {
        return (new AjaxPageResult()).page(ipage.getCurrent(), ipage.getSize(), ipage.getTotal()).withData(ipage.getRecords());
    }
}
