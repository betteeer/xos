package com.inossem.oms.base.svc.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class RemoteBkGlSubListV2 {

    private String itemNo;

    private String description;

    @JSONField(name = "gl_account")
    private String glAccount;

    @JSONField(name = "neg_posting")
    private Boolean negPosting;

    @JSONField(name = "amount_tc")
    private BigDecimal amountTc;

    @JSONField(name = "amount_lc")
    private BigDecimal amountLc;

    @JSONField(name = "dr_cr")
    private String drCr;
}
