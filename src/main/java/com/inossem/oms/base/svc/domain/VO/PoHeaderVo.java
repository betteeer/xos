package com.inossem.oms.base.svc.domain.VO;

import com.inossem.oms.base.svc.domain.PoHeader;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("po header vo")
public class PoHeaderVo extends PoHeader {

    @ApiModelProperty(name = "bp name", value = "bp name")
    private String bpName;
}
