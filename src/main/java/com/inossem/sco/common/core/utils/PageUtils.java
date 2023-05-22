package com.inossem.sco.common.core.utils;

import com.github.pagehelper.PageHelper;
import com.inossem.sco.common.core.utils.sql.SqlUtil;
import com.inossem.sco.common.core.web.page.PageDomain;
import com.inossem.sco.common.core.web.page.TableSupport;

public class PageUtils extends PageHelper {
    public PageUtils() {
    }

    public static void startPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        Boolean reasonable = pageDomain.getReasonable();
        PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(reasonable);
    }

    public static void clearPage() {
        PageHelper.clearPage();
    }
}
