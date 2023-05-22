package com.inossem.oms.svc.controller;


import com.inossem.oms.svc.service.WareHouseApiService;
import com.inossem.sco.common.core.domain.R;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 【仓库】Controller
 *
 * @author guoh
 * @date 2022-11-30
 */
@RestController
@RequestMapping("/svc/wareHouse")
@Slf4j
public class WareHouseApiController {

    @Resource
    private WareHouseApiService wareHouseApiService;

    /**
     * wareHouse停用查询  --->  是否还有delivery未发运  或者 so / po 未发运的订单
     * 无  true   可停用
     * 有  false  不可停用
     *
     * @param wareHouseCode
     * @param companyCode
     * @return
     */
    @GetMapping("/check/{wareHouseCode}/{companyCode}")
    public R<Boolean> check(@PathVariable("wareHouseCode") String wareHouseCode,
                            @PathVariable("companyCode") String companyCode) {
        if (StringUtils.isEmpty(wareHouseCode) || StringUtils.isEmpty(companyCode)) {
            throw new RuntimeException("查询wareHouse是否存在未发运接口入参异常");
        }
        return R.ok(wareHouseApiService.check(wareHouseCode, companyCode));
    }
}
