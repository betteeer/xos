package com.inossem.sco.common.core.web.page;

import java.io.Serializable;
import java.util.List;

public class TableDataInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private long total;
    private List<?> rows;
    private int code;
    private String msg;

    public TableDataInfo() {
    }

    public TableDataInfo(List<?> list, int total) {
        this.rows = list;
        this.total = (long)total;
    }

    public long getTotal() {
        return this.total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<?> getRows() {
        return this.rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
