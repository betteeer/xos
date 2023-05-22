package com.inossem.sco.common.core.web.domain;

import com.baomidou.mybatisplus.annotation.TableField;

public class BaseVO {
    private static final long serialVersionUID = 1L;
    @TableField(
            exist = false
    )
    long scoCachedTime = 0L;

    public BaseVO() {
    }

    public long getScoCachedTime() {
        return this.scoCachedTime;
    }

    public void setScoCachedTime(long scoCachedTime) {
        this.scoCachedTime = scoCachedTime;
    }
}
