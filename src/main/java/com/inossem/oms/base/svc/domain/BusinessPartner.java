package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.inossem.oms.base.svc.domain.VO.AddressVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * @author zoutong
 * @date 2022/10/17
 **/

@Data
@ApiModel("业务合作伙伴表")
//@TableName("business_partner")
@AllArgsConstructor
@NoArgsConstructor
public class BusinessPartner {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司编号",name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "is_customer",name = "isCustomer")
    private int isCustomer = 0;

    @ApiModelProperty(value = "isVendor",name = "isVendor")
    private int isVendor = 0;

    @ApiModelProperty(value = "bp号",name = "bp_number")
    private String bpNumber;

    @ApiModelProperty(value = "bpNumberEx", name = "bp_number_ex")
    private String bpNumberEx;

    @ApiModelProperty(value = "bp名称",name = "bpName")
    @NotEmpty(message = "Business Partner Name Not Empty")
    @Length(max = 100,message = "Business Partner Name Max Length is 100")
    private String bpName;

    @ApiModelProperty(value = "bp电话",name = "bpTel")
    @Length(max = 40,message = "Tel Max Length is 40")
    private String bpTel;

    @ApiModelProperty(value = "bp邮箱",name = "bpEmail")
    @Length(max = 40,message = "Email Max Length is 40")
    private String bpEmail;

    @ApiModelProperty(value = "bp联系人",name = "bpContact")
    @Length(max = 40,message = "Contact Person Max Length is 40")
    private String bpContact;

    @ApiModelProperty(value = "是否同步bk",name = "syncBk")
    private int syncBk = 0;

    @ApiModelProperty(value = "同步bp号",name = "bBpNumberCustomer")
    private String bkBpNumberCustomer = "";

    @ApiModelProperty(value = "同步bp号",name = "bkBpNumberVendor")
    private String bkBpNumberVendor = "";

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

    @ApiModelProperty(value = "is_block",name = "isBlock")
    private int isBlock = 0;

    @ApiModelProperty(value = "联系人列表",name = "contactList")
    @TableField(exist = false)
    private List<Contact> contactList;

    @ApiModelProperty(value = "office地址列表",name = "officeList")
    @TableField(exist = false)
    private List<AddressVO> officeList;

    @ApiModelProperty(value = "billto地址列表",name = "billtoList")
    @TableField(exist = false)
    private List<AddressVO> billtoList;

    @ApiModelProperty(value = "shipto地址列表",name = "shiptoList")
    @TableField(exist = false)
    private List<AddressVO> shiptoList;

    @ApiModelProperty(value = "default 0 if sku is for wms, 1", name = "wmsIndicator")
    private Integer wmsIndicator;

    @ApiModelProperty(value = "notes", name = "notes")
    private String notes;

  /*  @ApiModelProperty(value = "BK orgid",name = "orgidEx")
    @TableField
    private String orgidEx;

    @ApiModelProperty(value = "company code external system, like bookkeeping",name = "companyCodeEx")
    @TableField
    private String companyCodeEx;*/
}
