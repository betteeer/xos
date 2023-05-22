package com.inossem.oms.base.svc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Title: CurrencyExchange
 * @Description: <Describe this class>
 * @Author: guoh
 * @Create: 2023/3/30 15:28
 **/
@Data
@ApiModel("CurrencyExchange")
@TableName("currency_exchange")
public class CurrencyExchange {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ApiModelProperty(value = "主键", name = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * currency_fr
     */
    @ApiModelProperty(value = "currencyFr", name = "currencyFr")
    @JsonProperty("currency_fr")
    private String currencyFr;

    /**
     * currency_to
     */
    @ApiModelProperty(value = "currencyTo", name = "currencyTo")
    @JsonProperty("currency_to")
    private String currencyTo;

    /**
     * rate
     */
    @ApiModelProperty(value = "rate", name = "rate")
    private BigDecimal rate;

    /**
     * rate_date
     */
    @ApiModelProperty(value = "rateDate", name = "rateDate")
    @JsonProperty("rate_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date rateDate;

    /**
     * create_time
     */
    @ApiModelProperty(value = "createTime", name = "createTime")
    @JsonProperty("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * update_time
     */
    @ApiModelProperty(value = "updateTime", name = "updateTime")
    @JsonProperty("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


    /**
     * delete_time
     */
    @ApiModelProperty(value = "deleteTime", name = "deleteTime")
    @JsonProperty("delete_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;


    /**
     * time_stamp
     */
    @ApiModelProperty(value = "timeStamp", name = "timeStamp")
    @JsonProperty("time_stamp")
    private Long timeStamp;


}
