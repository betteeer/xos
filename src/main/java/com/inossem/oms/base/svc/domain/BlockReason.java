package com.inossem.oms.base.svc.domain;

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
@ApiModel("地址表")
@TableName("block_reason_table")
@AllArgsConstructor
@NoArgsConstructor
public class BlockReason implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "类型",name = "blockType")
    private String blockType;

    @ApiModelProperty(value = "reason代码",name = "reasonCode")
    private String reasonCode;

    @ApiModelProperty(value = "reason描述",name = "reasonDes")
    private String reasonDes;
}
