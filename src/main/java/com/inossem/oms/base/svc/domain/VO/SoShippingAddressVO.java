package com.inossem.oms.base.svc.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zoutong
 * @date 2022/10/25
 **/
@Data
@ApiModel(description = "SoShippingAddressVO")
public class SoShippingAddressVO {

    @ApiModelProperty(value = "CompanyCode", name = "CompanyCode")
//    private String CompanyCode;
    private String companyCode;

    @ApiModelProperty(value = "SoKey", name = "SoKey")
//    private String SoKey;
    private String soKey;

    @ApiModelProperty(value = "DeliveryKey", name = "DeliveryKey")
//    private String DeliveryKey;
    private String deliveryKey;
}
