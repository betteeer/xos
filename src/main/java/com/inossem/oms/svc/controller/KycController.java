package com.inossem.oms.svc.controller;

import com.alibaba.fastjson2.JSONObject;
import com.inossem.oms.api.kyc.api.KycCommonService;
import com.inossem.sco.common.core.web.controller.BaseController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("/svc/kyc")
public class KycController extends BaseController {
    @Resource
    private KycCommonService kycCommonService;
    @GetMapping("/subscription/validSubs")
    public JSONObject validSubs(@RequestParam("companyCode") String companyCode) throws IOException {
        JSONObject validSubscribe = kycCommonService.getValidSubscribe(companyCode, true);
        return validSubscribe;
    }
    @GetMapping("/menuConfig/menu")
    public JSONObject getMenu(@RequestParam() String account) throws IOException {
        JSONObject validSubscribe = kycCommonService.getMenu(account);
        return validSubscribe;
    }
}
