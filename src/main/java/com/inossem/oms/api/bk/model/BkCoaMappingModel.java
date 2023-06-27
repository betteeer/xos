package com.inossem.oms.api.bk.model;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 从bkp获取的coa mapping需要转化为的model
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BkCoaMappingModel {
    private Integer id;
    private Integer companyId;
    private String companyCode;
    private String type;
    private String code;
    private Long codeCategory;
    private String coaId;
    private String coaCode;
    private String coaName;
    private String debitCoaId;
    private String debitCoaCode;
    private String debitCoaName;
    private List<CoaItem> coaJson;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
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
}