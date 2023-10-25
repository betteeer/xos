package com.inossem.oms.svc.controller;

import com.inossem.oms.base.svc.domain.DTO.PdfSoFormDTO;
import com.inossem.oms.svc.service.PdfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@RestController
@RequestMapping("/svc/pdf")
@Slf4j
public class PdfController {
    @Resource
    private PdfService pdfService;
    @PostMapping("/so")
    public ResponseEntity<byte[]> convertHtmlToPdf(@RequestBody PdfSoFormDTO form) throws IOException, InvocationTargetException, IllegalAccessException {
//        String htmlFilePath = "classpath:templates/so.html";
//        Resource resource = resourceLoader.getResource(htmlFilePath);
//        String html = readHtmlToString(resource);
        return pdfService.generateSoPdf(form);
    }
}
