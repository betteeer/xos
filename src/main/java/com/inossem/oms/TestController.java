package com.inossem.oms;

import com.alibaba.fastjson2.JSONObject;
import com.inossem.oms.api.file.api.FileService;
import com.inossem.oms.selftest.AppVersion;
import com.inossem.oms.selftest.AppVersionMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    FileService fileService;

    @Resource
    AppVersionMapper appVersionMapper;
    @PostMapping(value="/file")
    public JSONObject test(@RequestPart("file") MultipartFile file) throws IOException {

//        System.out.println(file.getOriginalFilename());
        JSONObject upload = fileService.upload("3002", file);
        System.out.println(upload.getString("url"));
        return upload;

    }

    @GetMapping(value="appVersions")
    public List<AppVersion> getAppVersions() {
        List<AppVersion> appVersions = appVersionMapper.selectList(null);
        return appVersions;
    }
}
