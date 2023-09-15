package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * system connect info对象 syctem_conect
 *
 * @author shigf
 * @date 2022-12-10
 */
@Data
@TableName("system_connect")
@ApiModel("system_connect表")
@AllArgsConstructor
@NoArgsConstructor
public class SystemConnect
{
    private static final long serialVersionUID = 1L;

    /** PK */
    private Long id;

    /** OMS company code */
    private String companyCode;

    /** 3rd party system:\nk,instock,pg\n */
    private String exSystem;

    /** company id */
    private String companyCodeEx;

    /** user name for external system */
    private String userNameEx;

    /** external system password */
    private String passwordEx;

    /** 3rd party code */
    private String tokenEx;

    /** type of api */
    private String apiName;

    /** api url */
    private String apiUrl;

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

    private String bkCreator;

    private Boolean activeGl;
    private Boolean activeAr;
    private Boolean activeAp;

}
