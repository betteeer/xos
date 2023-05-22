package com.inossem.oms.base.svc.domain.VO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zoutong
 * @date 2022/10/17
 **/

@Data
@ApiModel("My Warehouse")
public class WarehouseVO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "company_code",name = "company_code")
    private String companyCode;

    @ApiModelProperty(value = "warehouseCode",name = "warehouseCode")
    private String warehouseCode;

    @ApiModelProperty(value = "name",name = "name")
    private String name;

    @ApiModelProperty(value = "status",name = "status")
    private String status;

    @ApiModelProperty(value = "street",name = "street")
    private String street;

    @ApiModelProperty(value = "city",name = "city")
    private String city;

    @ApiModelProperty(value = "province",name = "province")
    private String province;

    @ApiModelProperty(value = "country",name = "country")
    private String country;

    @ApiModelProperty(value = "postCode",name = "postCode")
    private String postCode;
}
