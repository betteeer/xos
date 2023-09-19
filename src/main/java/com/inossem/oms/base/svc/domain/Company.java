package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.inossem.oms.api.kyc.model.KycCompany;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.List;

@Data
@ApiModel("公司表")
@TableName("company")
@AllArgsConstructor
@NoArgsConstructor
public class Company {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司编号",name = "company_code")
    private String companyCode;

    @ApiModelProperty(value = "BK orgid",name = "orgidEx")
    @Length(max = 45,message = "orgidEx Max Length is 45")
    private String orgidEx;

    @ApiModelProperty(value = "company code external system, like bookkeeping",name = "companyCodeEx")
    @Length(max = 10,message = "companyCodeEx Max Length is 10")
    private String companyCodeEx;

    @ApiModelProperty(value = "公司名称",name = "name")
    @Length(max = 45,message = "name Max Length is 45")
    private String name;

    @ApiModelProperty(value = "公司描述",name = "description")
    @Length(max = 255,message = "description Max Length is 255")
    private String description;

    @ApiModelProperty(value = "log地址",name = "logo_url")
    private String logoUrl;

    @ApiModelProperty(value = "状态",name = "status")
    private int status;

    @ApiModelProperty(value = "currency_code",name = "currencyCode")
    @Length(max = 3,message = "currencyCode Max Length is 3")
    private String currencyCode;

    @ApiModelProperty(value = "语言编码",name = "languageCode")
    @Length(max = 2,message = "languageCode Max Length is 2")
    private String languageCode;

    @ApiModelProperty(value = "时区",name = "timeZone")
    @Length(max = 10,message = "timeZone Max Length is 10")
    private String timeZone;

    @ApiModelProperty(value = "companyEmail",name = "companyEmail")
    private String companyEmail;

    @ApiModelProperty(value = "gstHstTaxCode",name = "gstHstTaxCode")
    private String gstHstTaxCode;

    @ApiModelProperty(value = "qstTaxCode",name = "qstTaxCode")
    private String qstTaxCode;

    @ApiModelProperty(value = "pstBcTaxCode",name = "pstBcTaxCode")
    private String pstBcTaxCode;

    @ApiModelProperty(value = "pstSkTaxCode",name = "pstSkTaxCode")
    private String pstSkTaxCode;

    @ApiModelProperty(value = "deptid",name = "deptid")
    private String deptid;

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

    @ApiModelProperty(value = "地址id",name = "addressId")
    @TableField(exist = false)
    private Long addressId;

    @ApiModelProperty(value = "地址",name = "street")
    @TableField(exist = false)
    private String street;

    @ApiModelProperty(value = "城市",name = "city")
    @TableField(exist = false)
    private String city;

    @ApiModelProperty(value = "省份",name = "province")
    @TableField(exist = false)
    private String province;

    @ApiModelProperty(value = "国家",name = "country")
    @TableField(exist = false)
    private String country;

    @ApiModelProperty(value = "邮编",name = "postCode")
    @TableField(exist = false)
    private String postCode;

    @TableField(exist = false)
    private List<KycCompany.TaxItem> taxCharts;
}