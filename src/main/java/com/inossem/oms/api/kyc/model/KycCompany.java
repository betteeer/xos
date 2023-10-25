package com.inossem.oms.api.kyc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class KycCompany {

    private String addressLine1;

    private String billAddress;

    private String billCity;

    private String billContact;

    private String billCountry;

    private String billCountryId;

    private String billEmail;

    private String billPostalCode;

    private String billRegion;

    private String billRegionId;

    private String billTel;

    private String bookkeeper;

    private String businessNum;

    private String city;

    private String code;

    private String contactPerson;

    private String country;

    private String countryId;

    private String createSystem;

    private Date createTime;

    private String currency;

    private String customerType;

    private Date deleteTime;

    private String desc;

    private String email;

    private String financialYear;

    private Integer ftsStatus;

    private String id;

    private String indicator;

    private String industryCategory;

    private String language;

    private Integer matchable;

    private String name;

    private String partner;

    private String payMethod;

    private String phone;

    private String postCode;

    private String region;

    private String regionId;

    private String respEmail;

    private String respPerson;

    private String respTitle;

    private String specialist;

    private String stripeCusId;

    private String template;

    private String timezone;

    private String updateId;

    private Date updateTime;

    private String website;

    private List<TaxItem> taxChart;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @ToString
    public static class TaxItem {
        private String name;
        private String value;
        private String taxCode;
    }

    public String getConcatAddress() {
        return addressLine1 + " " + city + " " + region + " " + country + " " + postCode;
    }
}
