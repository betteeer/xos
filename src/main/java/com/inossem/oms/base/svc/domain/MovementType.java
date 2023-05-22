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
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * movementType对象 movement_type
 * 
 * @author shigf
 * @date 2022-10-13
 */
@Data
@TableName("movement_type")
@ApiModel("移动类型表")
@AllArgsConstructor
@NoArgsConstructor
public class MovementType
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "移动类型",name = "movementType")
    @TableField(value = "movement_type")
    private String movementType;

    @ApiModelProperty(value = "移动类型描述",name = "movementDescription")
    @TableField(value = "movement_description")
    private String movementDescription;

    @ApiModelProperty(value = "进库出库类型",name = "inOut")
    @TableField(value = "in_out")
    private Integer inOut;

    @ApiModelProperty(value = "是否冻结",name = "isBlockAllowed")
    @TableField(value = "is_block_allowed")
    private Integer isBlockAllowed;

    @ApiModelProperty(value = "存货变动",name = "inventoryChange")
    @TableField(value = "inventory_change")
    private Long inventoryChange;

    @ApiModelProperty(value = "配对的反向操作",name = "reverseMovType")
    @TableField(value = "reverse_mov_type")
    private String reverseMovType;

    @ApiModelProperty(value = "是否删除 1-未删除 1-已删除",name = "isDeleted")
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("movementType", getMovementType())
            .append("movementDescription", getMovementDescription())
            .append("inOut", getInOut())
            .append("isBlockAllowed", getIsBlockAllowed())
            .append("inventoryChange", getInventoryChange())
            .append("reverseMovType", getReverseMovType())
            .append("isDeleted", getIsDeleted())
            .toString();
    }
}
