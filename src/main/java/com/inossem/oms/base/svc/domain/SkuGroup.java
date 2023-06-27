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
import lombok.ToString;

import java.util.Date;

@Data
@TableName("sku_group")
@ApiModel("Sku group表")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SkuGroup {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    @ApiModelProperty(value = "所属公司",name = "companyCode")
    private String companyCode;
    @ApiModelProperty(value = "sku分组",name = "skuGroupCode")
    private String skuGroupCode;
    @ApiModelProperty(value = "sku分组描述",name = "skuGroupName")
    private String skuGroupName;

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
    private int isDeleted;
}
