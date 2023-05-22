package com.inossem.oms.base.svc.vo;

import com.inossem.oms.base.common.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ImportBusinessPartnerAddressVo {

    @Excel(name = "Street")
    private String street;

    @Excel(name = "City")
    private String city;

    @Excel(name = "Province")
    private String province;

    @Excel(name = "Country")
    private String country;

    @Excel(name = "postalCode")
    private String postalCode;
}
