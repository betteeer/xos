package com.inossem.oms.base.svc.domain.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zoutong
 * @date 2022/10/15
 **/
@Data
@ApiModel
public class SkuListReqVO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "company_code",name = "company_code")
    private String companyCode;

    @ApiModelProperty(value = "Search Box Text(SKU Name or SKU Code)", name = "SearchText")
    private String SearchText;

    @ApiModelProperty(value = "SkuType(IN or SE)", name = "SkuType")
    private String SkuType;

    @ApiModelProperty(value = "is kitting(0 or 1)",notes = "0 - not for kitting; 1 - for kitting", example = "0")
    private int IsKitting;

    @ApiModelProperty(value = "SkuName",name = "SkuName")
    private String SkuName;

    @ApiModelProperty(value = "skuNumberEx",name = "skuNumberEx")
    private String skuNumberEx;

    @ApiModelProperty(value = "wmsIndicator",name = "wmsIndicator")
    private String wmsIndicator;

    @ApiModelProperty(value = "skus",name = "skus")
    private List<SkuListQueryVo> skus;

    public static class SkuListQueryVo {
        public SkuListQueryVo() {}
        public SkuListQueryVo(String skuNumberEx, String companyCode) {
            this.skuNumberEx = skuNumberEx;
            this.companyCode = companyCode;
        }

        private String skuNumberEx;
        private String companyCode;

        public String getSkuNumberEx() {
            return skuNumberEx;
        }

        public void setSkuNumberEx(String skuNumberEx) {
            this.skuNumberEx = skuNumberEx;
        }

        public String getCompanyCode() {
            return companyCode;
        }

        public void setCompanyCode(String companyCode) {
            this.companyCode = companyCode;
        }
    }
}
