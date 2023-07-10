package com.inossem.oms.base.svc.domain;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author shigf
 * @date 2022/12/08
 **/
@Data
@ApiModel("bk的COA_REL表")
@TableName("bk_coa_rel")
@AllArgsConstructor
@NoArgsConstructor
public class BkCoaRel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司编号",name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "公司Id",name = "companyId")
    private Integer companyId;

    @ApiModelProperty(value = "公司code关联",name = "companyCodeEx")
    private String companyCodeEx;

    @ApiModelProperty(value = "类型",name = "type")
    private String type;

    @ApiModelProperty(value = "move类型",name = "code")
    private String code;

    @ApiModelProperty(value = "code类目",name = "codeCategory")
    private Integer codeCategory;

    @ApiModelProperty(value = "coa对应ID",name = "coaId")
    private Integer coaId;

    @ApiModelProperty(value = "coa对应code",name = "coaCode")
    private String coaCode;

    @ApiModelProperty(value = "coaName",name = "coaName")
    private String coaName;

    @ApiModelProperty(value = "debitCoaId",name = "debitCoaId")
    private Integer debitCoaId;

    @ApiModelProperty(value = "debitCoaCode",name = "debitCoaCode")
    private String debitCoaCode;

    @ApiModelProperty(value = "debitCoaName",name = "debitCoaName")
    private String debitCoaName;

    @ApiModelProperty(value = "创建者",name = "creator")
    private String creator;

    @ApiModelProperty(value = "创建时间",name = "createTime")
    private Date createTime;

    @ApiModelProperty(value = "更新时间",name = "updateTime")
    private Date updateTime;

    @ApiModelProperty(value = "删除标志位",name = "delFlag")
    private int delFlag;
    @Data
    public static class CoaItem {

        @JSONField(name = "skugroup")
        private String skuGroup;
        private String coaId;
        private String coaCode;
        private String coaName;
        private String debitCoaId;
        private String debitCoaCode;
        private String debitCoaName;

    }
    private List<CoaItem> coaJson;

}
