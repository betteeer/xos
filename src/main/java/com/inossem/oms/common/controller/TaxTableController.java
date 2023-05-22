package com.inossem.oms.common.controller;


import com.inossem.oms.base.svc.domain.VO.TaxCalculateResp;
import com.inossem.oms.base.svc.domain.VO.TaxTableCalculate;
import com.inossem.sco.common.core.domain.R;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import com.inossem.oms.common.service.ITaxTableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * 【税费计算】Controller
 *
 * @author shigf
 * @date 2022-10-19
 */
@RestController
@Slf4j
@Api(tags = {"税费计算相关接口"})
@RequestMapping("/common")
public class TaxTableController extends BaseController {

    @Resource
    private ITaxTableService taxTableService;

    /**
     * SO & PO 计算税费接口
     *
     * @param taxTableCalculate
     * @return
     */
    @ApiOperation(value = "税费计算", notes = "税费计算")
    @PostMapping("/tax_calculation")
    public AjaxResult<TaxCalculateResp> taxCaculation(@RequestBody TaxTableCalculate taxTableCalculate) {
        if (taxTableCalculate == null ||
                StringUtils.isEmpty(taxTableCalculate.getProvinceCode()) ||
                taxTableCalculate.getTaxTableItemsList().size() == 0) {
            return AjaxResult.error("params error");
        }
        return AjaxResult.success().withData(taxTableService.taxCaculation(taxTableCalculate));
    }


    /**
     * SO & PO 计算税费接口
     *
     * @param taxTableCalculate
     * @return
     */
    @PostMapping("/tax_calculationByCode")
    public R<TaxCalculateResp> taxCaculationByCode(@RequestBody TaxTableCalculate taxTableCalculate) {
        return R.ok(taxTableService.taxCaculation(taxTableCalculate));
    }


}
