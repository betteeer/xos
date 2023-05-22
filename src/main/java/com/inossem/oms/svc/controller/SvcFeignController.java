package com.inossem.oms.svc.controller;

import com.inossem.oms.svc.service.SvcFeignService;
import com.inossem.sco.common.core.domain.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/svc/svcFeign")
@Slf4j
@Api(tags = {"SVC feign接口"})
@Validated
public class SvcFeignController {

    @Resource
    private SvcFeignService svcFeignService;


    @GetMapping("/checkSku")
    @ApiOperation(value = "查询物料凭证列表",notes = "通过sku_code,sku_name,order_num,wareHourse_code分页查询物料凭证列表")
    public R<Boolean> checkSku(@RequestParam("skuCode")String skuCode, @RequestParam("companyCode") String companyCode) {
        return R.ok(svcFeignService.checkSku(skuCode,companyCode));
    }
}
