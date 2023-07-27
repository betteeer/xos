package com.inossem.oms.base.svc.domain.DTO;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

/**
 * @author zhenglu
 * @date 2023/07/10
 **/

@Data
@ApiModel("bkp_additional_contact对照实体")
public class BkpAdditionalContactDto {

    private Long id;

    private String contact_id;

    private String contact_type;

    private String contact_name;

    private String tel;

    private String email;

    private String note;

    private Date delete_time;

}
