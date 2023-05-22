package com.inossem.oms.base.svc.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zoutong
 * @date 2022/10/17
 **/

@Data
@ApiModel(description = "business_partner")
public class BPListVO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Search Box Text(Company Name or Tel or Email)", name = "Search Box Text(Company Name or Tel or Email)")
    private String SearchText;

    @ApiModelProperty(value = "company_code",name = "company_code")
    private String companyCode;

}
