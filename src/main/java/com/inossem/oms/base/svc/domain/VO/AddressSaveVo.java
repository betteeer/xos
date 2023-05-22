package com.inossem.oms.base.svc.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author kgh
 * @date 2022-11-05 16:23
 */
@Data
@ApiModel(description = "address vo")
public class AddressSaveVo extends AddressVO {

    @ApiModelProperty(value = "companyCode",name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "type",name = "type")
    private String type;

    @ApiModelProperty(value = "subType",name = "subType")
    private String subType;

    @ApiModelProperty(value = "key",name = "key")
    private String key;


}
