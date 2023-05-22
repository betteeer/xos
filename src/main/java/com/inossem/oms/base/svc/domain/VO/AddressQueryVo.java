package com.inossem.oms.base.svc.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author kgh
 * @date 2022-11-06 13:33
 */
@Data
@ApiModel(description = "AddressQueryVo")
public class AddressQueryVo {

    @ApiModelProperty(value = "type",name = "type")
    private String type;

    @ApiModelProperty(value = "subType",name = "subType")
    private String subType;

    @ApiModelProperty(value = "key",name = "key")
    private String key;

    @ApiModelProperty(value = "companyCode",name = "companyCode")
    private String companyCode;

}
