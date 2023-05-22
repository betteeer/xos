package com.inossem.oms.base.svc.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * @author zoutong
 * @date 2022/10/25
 **/
@Data
@ApiModel(description = "SoAddressVO")
public class PoAddressVO {

    @ApiModelProperty(value = "referenceKey", name = "referenceKey")
    @Length(max = 10,message = "address referenceKey Max Length is 10")
    private String referenceKey;

    @ApiModelProperty(value = "CompanyCode", name = "CompanyCode")
    private String CompanyCode;

    @ApiModelProperty(value = "BillingAddressList",name = "BillingAddressList")
    List<AddressVO> BillingAddressList;

    @ApiModelProperty(value = "ShippingAddressList",name = "ShippingAddressList")
    List<AddressVO> ShippingAddressList;
}
