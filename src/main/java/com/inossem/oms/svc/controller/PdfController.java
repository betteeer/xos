package com.inossem.oms.svc.controller;

import com.inossem.oms.base.svc.domain.DTO.PdfPoFormDTO;
import com.inossem.oms.base.svc.domain.DTO.PdfSoFormDTO;
import com.inossem.oms.svc.service.PdfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("/svc/pdf")
@Slf4j
public class PdfController {
    @Resource
    private PdfService pdfService;
//    @PostMapping("/so")
//    public ResponseEntity<byte[]> convertHtmlToSoPdf(@RequestBody PdfSoFormDTO form) throws IOException, IllegalAccessException {
////        String htmlFilePath = "classpath:templates/so.html";
////        Resource resource = resourceLoader.getResource(htmlFilePath);
////        String html = readHtmlToString(resource);
//        return pdfService.generateSoPdf(form);
//    }
    @PostMapping("/po")
    public ResponseEntity<byte[]> convertHtmlToPoPdf(@RequestBody PdfPoFormDTO form) throws IOException, IllegalAccessException {
        return pdfService.generatePoPdf(form);
    }

    @GetMapping("/company")
    public String getCompany(@RequestParam("companyCode") String companyCode) throws IOException {
        return pdfService.getCompany(companyCode);
    }
    @PostMapping("/so")
    public ResponseEntity<byte[]> convertHtmlToSoHtml(@RequestBody PdfSoFormDTO form) throws IOException, IllegalAccessException {
//        String htmlFilePath = "classpath:templates/so.html";
//        Resource resource = resourceLoader.getResource(htmlFilePath);
//        String html = readHtmlToString(resource);
        return pdfService.generateSoHtml(form);
    }
}
