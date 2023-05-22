package com.inossem.sco.common.core.web.domain;

public class PageVO {
    private Integer pageNum;
    private Integer pageSize;

    public PageVO() {
    }

    public Integer getPageNum() {
        return this.pageNum;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageNum(final Integer pageNum) {
        this.pageNum = pageNum;
    }

    public void setPageSize(final Integer pageSize) {
        this.pageSize = pageSize;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PageVO)) {
            return false;
        } else {
            PageVO other = (PageVO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$pageNum = this.getPageNum();
                Object other$pageNum = other.getPageNum();
                if (this$pageNum == null) {
                    if (other$pageNum != null) {
                        return false;
                    }
                } else if (!this$pageNum.equals(other$pageNum)) {
                    return false;
                }

                Object this$pageSize = this.getPageSize();
                Object other$pageSize = other.getPageSize();
                if (this$pageSize == null) {
                    if (other$pageSize != null) {
                        return false;
                    }
                } else if (!this$pageSize.equals(other$pageSize)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof PageVO;
    }

    public int hashCode() {
        int result = 1;
        Object $pageNum = this.getPageNum();
        result = result * 59 + ($pageNum == null ? 43 : $pageNum.hashCode());
        Object $pageSize = this.getPageSize();
        result = result * 59 + ($pageSize == null ? 43 : $pageSize.hashCode());
        return result;
    }

    public String toString() {
        return "PageVO(pageNum=" + this.getPageNum() + ", pageSize=" + this.getPageSize() + ")";
    }
}
