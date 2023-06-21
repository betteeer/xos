
package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * condition_type对象
 */
@Data
@TableName("condition_type_table")
@ApiModel("condition类型表")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ConditionType
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "类型",name = "condition_type")
    @TableField(value = "condition_type")
    private String conditionType;

    @ApiModelProperty(value = "描述",name = "conditionDescription")
    @TableField(value = "condition_description")
    private String conditionDescription;
}
