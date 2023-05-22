package com.inossem.oms.common.controller;


import com.inossem.oms.base.svc.domain.LoginParams;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * 【请填写功能名称】Controller
 *
 * @author guoh
 * @date 2023-03-09
 */
@RestController
@RequestMapping
@Api(tags = {"登录相关接口"})
@Slf4j
public class LoginController {

    @PostMapping("/common/login")
    public AjaxResult<?> innerLogin(@RequestBody LoginParams param) {
        log.info(">>>>>oms登录接口,无验证码获取token,入参为:{}", param.toString());
//        LoginBody form = new LoginBody();
//        form.setUsername(param.getName());
//        form.setPassword(param.getPassword());
//        form.setFrom("oms");
//        log.info(">>>>>oms登录接口,参数封装完,form:{}", form);
//        R<?> r = remoteInnerLoginService.innerLogin(form);
//        log.info(">>>>oms登录接口,返回结果,code={},msg={},data={}", r.getCode(), r.getMsg(), r.getData());
//        if (r.getCode() != 200) {
//            log.info(">>>>oms登录接口,获取token失败,失败原因:{}", r.getMsg());
//            throw new RuntimeException(">>>>oms登录接口,获取token失败,失败原因:" + r.getMsg());
//        }
        HashMap<String, String> map = new HashMap<>();
        map.put("access_token", "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjo2NCwidXNlcl9rZXkiOiI3YjZkMTkxMi1jYzg5LTQ4MmYtODMzZC0xZTcyMTcyOGQ0MTYiLCJ1c2VybmFtZSI6Inh1ZWZlaSJ9.3oll0lewjEn3OS9Gk2DqkTzPaTvW79TmctBBoIVQKeXHpAZdoacA1YLMiEZQl4ZYRsKYWhM2MGSlHE_bQKv9vA");
        map.put("expires_in", "720");
        return AjaxResult.success(map);
    }


}
