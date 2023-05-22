//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.inossem.sco.common.core.web.page;

import com.inossem.sco.common.core.text.Convert;
import com.inossem.sco.common.core.utils.ServletUtils;

public class TableSupport {
    public static final String PAGE_NUM = "pageNum";
    public static final String PAGE_SIZE = "pageSize";
    public static final String ORDER_BY_COLUMN = "orderByColumn";
    public static final String IS_ASC = "isAsc";
    public static final String REASONABLE = "reasonable";

    public TableSupport() {
    }

    public static PageDomain getPageDomain() {
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(Convert.toInt(ServletUtils.getParameter("pageNum"), 1));
        pageDomain.setPageSize(Convert.toInt(ServletUtils.getParameter("pageSize"), 10));
        pageDomain.setOrderByColumn(ServletUtils.getParameter("orderByColumn"));
        pageDomain.setIsAsc(ServletUtils.getParameter("isAsc"));
        pageDomain.setReasonable(ServletUtils.getParameterToBool("reasonable"));
        return pageDomain;
    }

    public static PageDomain buildPageRequest() {
        return getPageDomain();
    }
}
