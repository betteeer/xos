package com.inossem.oms.base.svc.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author zoutong
 * @date 2022/10/22
 **/
@Data
@ApiModel(description = "address")
public class AddressVO {

    @ApiModelProperty(value = "primary key",name = "id")
    private Long id;

    @ApiModelProperty(value = "street",name = "street")
    @Length(max = 255,message = "street Max Length is 255")
    private String street;

    @ApiModelProperty(value = "city",name = "city")
    @Length(max = 45,message = "city Max Length is 45")
    private String city;

    @ApiModelProperty(value = "province",name = "province")
    @Length(max = 3,message = "province Max Length is 3")
    private String province;

    @ApiModelProperty(value = "country",name = "country")
    @Length(max = 3,message = "country Max Length is 3")
    private String country;

    @ApiModelProperty(value = "postCode",name = "postCode")
    @Length(max = 10,message = "postCode Max Length is 10")
    private String postCode;

    @ApiModelProperty(value = "isDefault",name = "isDefault")
    private int isDefault;
}
