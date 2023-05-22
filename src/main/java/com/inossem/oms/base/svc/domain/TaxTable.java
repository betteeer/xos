package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author guoh
 * @date 2022-10-19
 */
@Data
@ApiModel("TaxTable")
@TableName("tax_table")
public class TaxTable
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键", name = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "省编码", name = "provinceCode")
    private String provinceCode;

    @ApiModelProperty(value = "省名称", name = "provinceName")
    private String provinceName;

    @ApiModelProperty(value = "税费", name = "gstRate")
    private BigDecimal gstRate;

    @ApiModelProperty(value = "税费", name = "hstRate")
    private BigDecimal hstRate;

    @ApiModelProperty(value = "税费", name = "qstRate")
    private BigDecimal qstRate;

    @ApiModelProperty(value = "税费", name = "pstRate")
    private BigDecimal pstRate;

    @ApiModelProperty(value = "总计税费", name = "totalRate")
    private BigDecimal totalRate;

}
