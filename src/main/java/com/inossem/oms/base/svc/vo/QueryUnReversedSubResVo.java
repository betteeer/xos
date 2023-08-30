package com.inossem.oms.base.svc.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.inossem.oms.base.svc.domain.MaterialDoc;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@ApiModel("unReverse物料凭证返回实体")
@Data
public class QueryUnReversedSubResVo extends MaterialDoc{

    @ApiModelProperty(value = "现有正常数量",name = "totalOnhandQty")
    @TableField(value = "total_onhand_qty")
    private BigDecimal totalOnhandQty;

    @ApiModelProperty(value = "总冻结数量",name = "totalBlockQty")
    @TableField(value = "total_block_qty")
    private BigDecimal totalBlockQty;

    @ApiModelProperty(value = "总移动数量",name = "totalTransferQty")
    @TableField(value = "total_transfer_qty")
    private BigDecimal totalTransferQty;

    @ApiModelProperty(value = "总数量",name = "totalQty")
    @TableField(value = "total_qty")
    private BigDecimal totalQty;

    @ApiModelProperty(value = "基础单位",name = "basicUom")
    @TableField(value = "basic_uom")
    private String basicUom;

    @ApiModelProperty(value = "商品名字",name = "skuName")
    private String skuName;

    @ApiModelProperty(value = "material doc上的total amount", name="materialDocTotalAmount")
    private BigDecimal materialDocTotalAmount;
}
