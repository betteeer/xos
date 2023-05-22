package com.inossem.oms.base.svc.vo;

import com.inossem.oms.base.common.annotation.Excel;
import com.inossem.oms.base.common.annotation.Excels;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ImportBusinessPartnerVo {

    @Excels({
            @Excel(targetAttr = "partnerName",needMerge = true),
            @Excel(targetAttr = "tel",needMerge = true),
            @Excel(targetAttr = "email",needMerge = true),
            @Excel(targetAttr = "contact",needMerge = true),
    })
    @Excel(needMerge = true,name = "BasicIn formation")
    List<ImportBPBasicInformationVo> basicInformation;

    /*@Excels({
            @Excel(name = "Street",targetAttr = "street"),
            @Excel(name = "City",targetAttr = "city"),
            @Excel(name = "Province",targetAttr = "province"),
            @Excel(name = "Country",targetAttr = "country"),
            @Excel(name = "Postal Code",targetAttr = "postalCode"),
    })
   @Excel(name = "Office Address")
    List<ImportBusinessPartnerAddressVo> officeAddress;*/

   /*  @Excel(needMerge = true,name = "Billing Address")
    List<ImportBusinessPartnerAddressVo> BillingAddress;

    @Excel(needMerge = true,name = "Shipping Address")
    List<ImportBusinessPartnerAddressVo> shippingAddress;*/
}
