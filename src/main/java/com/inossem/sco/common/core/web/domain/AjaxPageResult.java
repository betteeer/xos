package com.inossem.sco.common.core.web.domain;

public class AjaxPageResult<T> extends AjaxResult<T> {
    private static final long serialVersionUID = 1L;
    long pageSize;
    long pageNum;
    long total;

    public AjaxPageResult<T> page(long pageNum, long pageSize, long total) {
        super.code = 200;
        super.msg = "SUCCESS";
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        return this;
    }

    public AjaxPageResult<T> withData(T data) {
        super.data = data;
        return this;
    }

    public AjaxPageResult<T> page_error(int code, String msg) {
        super.code = code;
        super.msg = msg;
        return this;
    }

    public AjaxPageResult<T> page_success(String msg) {
        super.code = 200;
        super.msg = msg;
        return this;
    }

    public AjaxPageResult() {
    }

    public long getPageSize() {
        return this.pageSize;
    }

    public long getPageNum() {
        return this.pageNum;
    }

    public long getTotal() {
        return this.total;
    }

    public void setPageSize(final long pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageNum(final long pageNum) {
        this.pageNum = pageNum;
    }

    public void setTotal(final long total) {
        this.total = total;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof AjaxPageResult)) {
            return false;
        } else {
            AjaxPageResult<?> other = (AjaxPageResult)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getPageSize() != other.getPageSize()) {
                return false;
            } else if (this.getPageNum() != other.getPageNum()) {
                return false;
            } else {
                return this.getTotal() == other.getTotal();
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof AjaxPageResult;
    }

    public int hashCode() {
        int result = 1;
        long $pageSize = this.getPageSize();
        result = result * 59 + (int)($pageSize >>> 32 ^ $pageSize);
        long $pageNum = this.getPageNum();
        result = result * 59 + (int)($pageNum >>> 32 ^ $pageNum);
        long $total = this.getTotal();
        result = result * 59 + (int)($total >>> 32 ^ $total);
        return result;
    }

    public String toString() {
        return "AjaxPageResult(pageSize=" + this.getPageSize() + ", pageNum=" + this.getPageNum() + ", total=" + this.getTotal() + ")";
    }
}
