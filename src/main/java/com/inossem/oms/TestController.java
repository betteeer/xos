package com.inossem.oms;

import com.alibaba.fastjson2.JSONObject;
import com.inossem.oms.api.file.api.FileService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    FileService fileService;

    @PostMapping(value="file")
    public JSONObject test(@RequestPart("file") MultipartFile file) throws IOException {

//        System.out.println(file.getOriginalFilename());
        JSONObject upload = fileService.upload("3002", file);
        System.out.println(upload.getString("url"));
        return upload;

    }

}
