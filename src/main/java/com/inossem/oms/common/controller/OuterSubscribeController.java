package com.inossem.oms.common.controller;

import com.inossem.oms.base.common.domain.dto.SubscribeFormDTO;
import com.inossem.oms.common.service.OuterSubscribeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("/statistics")
@Slf4j
public class OuterSubscribeController {
    @Resource
    private OuterSubscribeService outerSubscribeService;

    @PostMapping("/customer-indicator-oms")
    public String getOmsSubscribeIndicator(@RequestBody SubscribeFormDTO form) throws IOException {
        return outerSubscribeService.getOmsSubscribeIndicator(form.getCompany_code(), form.getTime());
    }
}
