package com.inossem.oms.selftest;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@ApiModel("版本信息")
@TableName("app_version")
@AllArgsConstructor
@NoArgsConstructor
public class AppVersion {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "primary key",name = "id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    private String versionCode;

    private String codeStr;

    private Boolean forceUpdate;

    private String info;

}
