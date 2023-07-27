package com.inossem.oms.base.svc.domain.DTO;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @author zhenglu
 * @date 2023/07/10
 **/

@Data
@ApiModel("bkp_bp对照实体")
public class BkpBusinessPartnerDto {

    private Long id;

    private String company_id;

    private Integer company_code;

    private String contact_name;

    private String gl_account;

    private String tel;

    private String email;

    private String office_receiver;

    private String contact_id;

    private String office_street;

    private String office_city;

    private String office_province;

    private String office_country;

    private String office_postal_code;

    private String billing_street;

    private String billing_city;

    private String billing_province;

    private String billing_country;

    private String billing_postal_code;

    private List<BkpAdditionalContactDto> additional_contact;

    private List<BkpShippingAddressDto> shipping_address;

}
