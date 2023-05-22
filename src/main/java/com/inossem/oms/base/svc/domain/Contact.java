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
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * @author zoutong
 * @date 2022/10/17
 **/
@Data
@ApiModel("联系人表")
@TableName("contact_table")
@AllArgsConstructor
@NoArgsConstructor
public class Contact {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司编号",name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "bp编号",name = "bpNumber")
    private String bpNumber;

    @ApiModelProperty(value = "联系人类型",name = "contactType")
    @Length(max = 20,message = "Additional Contact Contact Type Max Length is 20")
    private String contactType;

    @ApiModelProperty(value = "联系人",name = "contactPerson")
    @Length(max = 40,message = "Additional Contact Contact Person Max Length is 40")
    private String contactPerson = "";

    @ApiModelProperty(value = "联系人手机号",name = "contactTel")
    @Length(max = 40,message = "Additional Contact Tel Max Length is 40")
    private String contactTel = "";

    @ApiModelProperty(value = "联系人邮箱",name = "contactEmail")
    @Length(max = 40,message = "Additional Contact Email Max Length is 40")
    private String contactEmail = "";

    @ApiModelProperty(value = "联系人注释",name = "contactNote")
    @Length(max = 40,message = "Additional Contact Note Max Length is 40")
    private String contactNote = "";

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
