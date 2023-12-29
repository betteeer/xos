package com.test.yqq.controller;

import com.test.yqq.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/")
public class TestController {

    @Resource
    private TestService testService;
    @GetMapping("/test")
    public boolean isExist(@RequestParam String number) {
        return testService.isExist(number);
    }
}
