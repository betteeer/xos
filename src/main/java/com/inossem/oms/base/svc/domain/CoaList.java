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
 * system connect info对象 coa_list
 * 
 * @author shigf
 * @date 2022-12-10
 */
@Data
@TableName("coa_list")
@ApiModel("coa_list表")
@AllArgsConstructor
@NoArgsConstructor
public class CoaList extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** PK */
    private Long id;

    /** OMS company code */
    private String companyCode;

    /** company id */
    private String companyCodeEx;

    /** GL account id */
    private String accountId;

    /** GL account */
    private String accountName;

    /** GL account description */
    private String accountDes;

    /** GL account alias */
    private String accountAlias;

    /** create time (GMT) */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date gmtCreate;

    /** the last update time (GMT). When the record is created first time, the update_time = create_time */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date gmtModified;

    /** the user id who update the record. When the record is created first time, update_by = create_by */
    private String modifiedBy;

    /** 1 - deleted, 0 - valid */
    private Integer isDeleted;

}
