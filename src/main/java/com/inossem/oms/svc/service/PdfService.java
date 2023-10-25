package com.inossem.oms.svc.service;

import com.inossem.oms.api.kyc.api.KycCommonService;
import com.inossem.oms.api.kyc.model.KycCompany;
import com.inossem.oms.base.common.domain.SpecialConfig;
import com.inossem.oms.base.svc.domain.DTO.PdfSoFormDTO;
import com.inossem.oms.common.service.SpecialConfigService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PdfService {
    @Resource
    private ResourceLoader resourceLoader;

    @Resource
    private SpecialConfigService specialConfigService;

    @Resource
    private KycCommonService kycCommonService;

    public ResponseEntity<byte[]> generateSoPdf(PdfSoFormDTO so) throws IOException, InvocationTargetException, IllegalAccessException {
        Context context = new Context();
        Map<String, Object> map = convertUsingReflection(so);
        SpecialConfig specialConfig = specialConfigService.findOne(so.getCompanyCode());
        if (specialConfig != null) {
            map.put("ending", specialConfig.getSoPdfEndingText());
        }
        KycCompany company = kycCommonService.getCompanyByCode(so.getCompanyCode());
        map.put("name", company.getName());
        map.put("address", company.getConcatAddress());
        map.put("email", company.getEmail());
        map.put("tel", company.getPhone());
        context.setVariables(map);
        return generator(context, "so");
    }

    private Map<String, Object> convertUsingReflection(Object object) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field: fields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(object));
        }
        return map;
    }
    public ResponseEntity<byte[]> generatePoPdf(PdfSoFormDTO form) throws IOException {
        Context context = new Context();
        context.setVariable("name", "哈哈哈");
        return generator(context, "po");
    }

    private ResponseEntity<byte[]> generator(Context context, String templateName) throws IOException {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8"); // 设置字符集为UTF-8
        // 创建Thymeleaf模板引擎
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        String html = templateEngine.process(templateName, context);
        System.out.println(html);
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.useFont(new File(this.getClass().getClassLoader().getResource("fonts/WeiRuanYaHei.ttf").getFile()), "WeiRuanYaHei");
        builder.useFont(new File(this.getClass().getClassLoader().getResource("fonts/Lato.ttf").getFile()), "Lato");
        builder.withHtmlContent(html, "");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        builder.toStream(outputStream);
        builder.run();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        return ResponseEntity.ok().headers(headers).body(outputStream.toByteArray());
    }
}
