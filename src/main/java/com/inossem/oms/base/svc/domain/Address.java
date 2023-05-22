package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@ApiModel("地址表")
@TableName("address_table")
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司编号",name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "地址类型",name = "type")
    private String type;

    @ApiModelProperty(value = "地址子类型",name = "subType")
    private String subType;

    @ApiModelProperty(value = "关联key",name = "referenceKey")
    private String referenceKey;

    @ApiModelProperty(value = "地址1",name = "address1")
    private String address1 = "";

    @ApiModelProperty(value = "地址2",name = "address2")
    private String address2 = "";

    @ApiModelProperty(value = "手机号",name = "phone")
    private String phone = "";

    @ApiModelProperty(value = "联系人",name = "contactPerson")
    private String contactPerson = "";

    @ApiModelProperty(value = "城市",name = "city")
    private String city = "";

    @ApiModelProperty(value = "省份编号",name = "regionCode")
    private String regionCode = "";

    @ApiModelProperty(value = "国家编号",name = "countryCode")
    private String countryCode = "";

    @ApiModelProperty(value = "邮编",name = "postalCode")
    private String postalCode = "";

    @ApiModelProperty(value = "是否默认地址",name = "isDefault")
    private int isDefault = 0;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间",name = "gmtCreate")
    private Date gmtCreate;

    @ApiModelProperty(value = "创建者",name = "createBy")
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间",name = "gmtModified")
    private Date gmtModified;

    @ApiModelProperty(value = "修改者",name = "modifiedBy")
    private String modifiedBy;

    @ApiModelProperty(value = "是否删除",name = "isDeleted")
    private int isDeleted = 0;

}
