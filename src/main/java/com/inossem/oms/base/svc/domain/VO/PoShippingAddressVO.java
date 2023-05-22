package com.inossem.oms.base.svc.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shigf
 * @date 2022/10/25
 **/
@Data
@ApiModel(description = "PoShippingAddressVO")
public class PoShippingAddressVO {

    @ApiModelProperty(value = "CompanyCode", name = "CompanyCode")
    private String CompanyCode;

    @ApiModelProperty(value = "PoKey", name = "PoKey")
    private String PoKey;

    @ApiModelProperty(value = "DeliveryKey", name = "DeliveryKey")
    private String DeliveryKey;
}
