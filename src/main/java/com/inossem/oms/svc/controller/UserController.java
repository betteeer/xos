package com.inossem.oms.svc.controller;

import com.inossem.oms.svc.service.UserService;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("/svc/user")
public class UserController extends BaseController {
    @Resource
    UserService userService;

    @GetMapping("/soHeader/records")
    public AjaxResult getSoUsers(@RequestParam String companyCode) throws IOException {
        return AjaxResult.success(userService.getSoUsers(companyCode));
    }
}
