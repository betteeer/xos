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

/**
 * @author zoutong
 * @date 2022/10/17
 **/

@Data
@ApiModel("sku与bp关联表")
@TableName("sku_bp")
@AllArgsConstructor
@NoArgsConstructor
public class SkuBp {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司编号",name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "sku编号",name = "skuNumber")
    private String skuNumber;

    @ApiModelProperty(value = "bp编号",name = "bpNumber")
    private String bpNumber;

    @ApiModelProperty(value = "ref_code",name = "refCode")
    private String refCode;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间",name = "createTime")
    private Date createTime;

    @ApiModelProperty(value = "创建者",name = "createBy")
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间",name = "updateTime")
    private Date updateTime;

    @ApiModelProperty(value = "修改者",name = "updateBy")
    private String updateBy;

    @ApiModelProperty(value = "是否删除",name = "isDeleted")
    private int isDeleted;
}
