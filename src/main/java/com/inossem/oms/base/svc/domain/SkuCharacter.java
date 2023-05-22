package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@ApiModel("sku与character关联表")
@TableName("sku_character")
@AllArgsConstructor
@NoArgsConstructor
public class SkuCharacter {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司编号",name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "sku编号",name = "skuNumber")
    private String skuNumber;

    @ApiModelProperty(value = "特征编号",name = "characterCode")
    @JsonProperty("chCode")
    private String characterCode = "";

    @ApiModelProperty(value = "特征值",name = "characterValue")
    @JsonProperty("chValue")
    private String characterValue;

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
