package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author zoutong
 * @date 2022/10/17
 **/

@Data
@ApiModel("仓库表")
@TableName("warehouse_table")
@AllArgsConstructor
@NoArgsConstructor
public class Warehouse {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司编号",name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "仓库编号",name = "warehouseCode")
    private String warehouseCode;

    @ApiModelProperty(value = "名称",name = "name")
    private String name;

    @ApiModelProperty(value = "状态",name = "status")
    private String status;

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

    @ApiModelProperty(value = "street",name = "street")
    @TableField(exist = false)
    private String street;

    @ApiModelProperty(value = "city",name = "city")
    @TableField(exist = false)
    private String city;

    @ApiModelProperty(value = "province",name = "province")
    @TableField(exist = false)
    private String province;

    @ApiModelProperty(value = "country",name = "country")
    @TableField(exist = false)
    private String country;

    @ApiModelProperty(value = "postCode",name = "postCode")
    @TableField(exist = false)
    private String postCode;
}
