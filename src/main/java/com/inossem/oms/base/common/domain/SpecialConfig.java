package com.inossem.oms.base.common.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@TableName("special_config")
@ApiModel("用于配置特殊字段的表")
@AllArgsConstructor
@NoArgsConstructor
public class SpecialConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key", name = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公司代码", name = "companyCode")
    private String companyCode;

    @ApiModelProperty(value = "so pdf模板的结束语", name = "soPdfEndingText")
    private String soPdfEndingText;

    @ApiModelProperty(value = "po pdf模板的结束语", name = "poPdfEndingText")
    private String poPdfEndingText;
}
