
package com.inossem.sco.common.core.web.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableField(
            exist = false
    )
    long scoCachedTime = 0L;
    @TableField(
            exist = false
    )
    private String searchValue;
    @TableField(
            exist = false
    )
    private Map<String, Object> params;
    @TableField("create_by")
    private String createBy;
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    @TableField("create_time")
    private Date createTime;
    @TableField("update_by")
    private String updateBy;
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    @TableField("update_time")
    private Date updateTime;
    private String remark;

    public Map<String, Object> getParams() {
        if (this.params == null) {
            this.params = new HashMap();
        }

        return this.params;
    }

    public BaseEntity() {
    }

    public long getScoCachedTime() {
        return this.scoCachedTime;
    }

    public String getSearchValue() {
        return this.searchValue;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public String getUpdateBy() {
        return this.updateBy;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setScoCachedTime(final long scoCachedTime) {
        this.scoCachedTime = scoCachedTime;
    }

    public void setSearchValue(final String searchValue) {
        this.searchValue = searchValue;
    }

    public void setParams(final Map<String, Object> params) {
        this.params = params;
    }

    public void setCreateBy(final String createBy) {
        this.createBy = createBy;
    }

    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    public void setCreateTime(final Date createTime) {
        this.createTime = createTime;
    }

    public void setUpdateBy(final String updateBy) {
        this.updateBy = updateBy;
    }

    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    public void setUpdateTime(final Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof BaseEntity)) {
            return false;
        } else {
            BaseEntity other = (BaseEntity)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getScoCachedTime() != other.getScoCachedTime()) {
                return false;
            } else {
                label97: {
                    Object this$searchValue = this.getSearchValue();
                    Object other$searchValue = other.getSearchValue();
                    if (this$searchValue == null) {
                        if (other$searchValue == null) {
                            break label97;
                        }
                    } else if (this$searchValue.equals(other$searchValue)) {
                        break label97;
                    }

                    return false;
                }

                Object this$params = this.getParams();
                Object other$params = other.getParams();
                if (this$params == null) {
                    if (other$params != null) {
                        return false;
                    }
                } else if (!this$params.equals(other$params)) {
                    return false;
                }

                Object this$createBy = this.getCreateBy();
                Object other$createBy = other.getCreateBy();
                if (this$createBy == null) {
                    if (other$createBy != null) {
                        return false;
                    }
                } else if (!this$createBy.equals(other$createBy)) {
                    return false;
                }

                label76: {
                    Object this$createTime = this.getCreateTime();
                    Object other$createTime = other.getCreateTime();
                    if (this$createTime == null) {
                        if (other$createTime == null) {
                            break label76;
                        }
                    } else if (this$createTime.equals(other$createTime)) {
                        break label76;
                    }

                    return false;
                }

                Object this$updateBy = this.getUpdateBy();
                Object other$updateBy = other.getUpdateBy();
                if (this$updateBy == null) {
                    if (other$updateBy != null) {
                        return false;
                    }
                } else if (!this$updateBy.equals(other$updateBy)) {
                    return false;
                }

                Object this$updateTime = this.getUpdateTime();
                Object other$updateTime = other.getUpdateTime();
                if (this$updateTime == null) {
                    if (other$updateTime != null) {
                        return false;
                    }
                } else if (!this$updateTime.equals(other$updateTime)) {
                    return false;
                }

                Object this$remark = this.getRemark();
                Object other$remark = other.getRemark();
                if (this$remark == null) {
                    if (other$remark != null) {
                        return false;
                    }
                } else if (!this$remark.equals(other$remark)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BaseEntity;
    }

    public int hashCode() {
        int result = 1;
        long $scoCachedTime = this.getScoCachedTime();
        result = result * 59 + (int)($scoCachedTime >>> 32 ^ $scoCachedTime);
        Object $searchValue = this.getSearchValue();
        result = result * 59 + ($searchValue == null ? 43 : $searchValue.hashCode());
        Object $params = this.getParams();
        result = result * 59 + ($params == null ? 43 : $params.hashCode());
        Object $createBy = this.getCreateBy();
        result = result * 59 + ($createBy == null ? 43 : $createBy.hashCode());
        Object $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
        Object $updateBy = this.getUpdateBy();
        result = result * 59 + ($updateBy == null ? 43 : $updateBy.hashCode());
        Object $updateTime = this.getUpdateTime();
        result = result * 59 + ($updateTime == null ? 43 : $updateTime.hashCode());
        Object $remark = this.getRemark();
        result = result * 59 + ($remark == null ? 43 : $remark.hashCode());
        return result;
    }

    public String toString() {
        return "BaseEntity(scoCachedTime=" + this.getScoCachedTime() + ", searchValue=" + this.getSearchValue() + ", params=" + this.getParams() + ", createBy=" + this.getCreateBy() + ", createTime=" + this.getCreateTime() + ", updateBy=" + this.getUpdateBy() + ", updateTime=" + this.getUpdateTime() + ", remark=" + this.getRemark() + ")";
    }
}
