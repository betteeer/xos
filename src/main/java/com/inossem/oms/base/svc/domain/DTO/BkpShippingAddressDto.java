package com.inossem.oms.base.svc.domain.DTO;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author zhenglu
 * @date 2023/07/10
 **/

@Data
@ApiModel("bkp_shipping_address对照实体")
public class BkpShippingAddressDto {

    private Long id;

    private String shipping_street;

    private String shipping_city;

    private String shipping_province;

    private String shipping_country;

    private String shipping_postal_code;

    private Integer is_default;

}
