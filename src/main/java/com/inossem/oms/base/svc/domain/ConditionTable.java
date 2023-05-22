package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.inossem.sco.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * system connect info对象 condition_table
 * 
 * @author shigf
 * @date 2022-12-10
 */
@Data
@TableName("condition_table")
@ApiModel("condition_table表")
@AllArgsConstructor
@NoArgsConstructor
public class ConditionTable extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** PK */
    private Long id;

    /** OMS company code */
    private String companyCode;

    /** company id */
    private Long companyCodeEx;

    /** condition key
S001: sales
P001: purchase
S002: sales discount
P002: purchase discount */
    private String conditionType;

    /** GL account id */
    private String accountId;

    private String accountCode;

    /** GL account */
    private String accountName;

    /** the last update time (GMT). When the record is created first time, the update_time = create_time */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date gmtModified;

    /** the user id who update the record. When the record is created first time, update_by = create_by */
    private String modifiedBy;

    /** 1 - deleted, 0 - valid */
    private Integer isDeleted;

}
