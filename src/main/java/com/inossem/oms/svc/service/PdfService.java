package com.inossem.oms.svc.service;

import com.inossem.oms.api.bk.api.BookKeepingService;
import com.inossem.oms.api.kyc.api.KycCommonService;
import com.inossem.oms.api.kyc.model.KycCompany;
import com.inossem.oms.base.common.domain.SpecialConfig;
import com.inossem.oms.base.svc.domain.DTO.PdfPoFormDTO;
import com.inossem.oms.base.svc.domain.DTO.PdfSoFormDTO;
import com.inossem.oms.common.service.SpecialConfigService;
import com.inossem.sco.common.core.exception.ServiceException;
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
import java.rmi.ServerException;
import java.util.ArrayList;
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

    @Resource
    private BookKeepingService bookKeepingService;

    public ResponseEntity<byte[]> generateSoPdf(PdfSoFormDTO so) throws IOException, IllegalAccessException {
        Context context = new Context();
        Map<String, Object> map = convertUsingReflection(so);
//        List<PdfSoFormDTO.Sku> skus = new ArrayList<>();
//        for (int i = 0; i < 31; i++) {
//            PdfSoFormDTO.Sku sku = new PdfSoFormDTO.Sku();
//            // 拷贝需要的属性
//            BeanUtils.copyProperties(so.getSkus().get(0), sku);
//            sku.setOrder(String.valueOf(i + 1) );
//            skus.add(sku);
//        }
//        map.put("skus", skus);
        SpecialConfig specialConfig = specialConfigService.findOne(so.getCompanyCode());
        if (specialConfig != null) {
            map.put("ending", specialConfig.getSoPdfEndingText());
        }
        map.put("companyCode", so.getCompanyCode());
        setCompanyInfo(map, so.getCompanyCode());
        context.setVariables(map);
        return generator(context, "so");
    }
    public ResponseEntity<byte[]> generatePoPdf(PdfPoFormDTO po) throws IOException, IllegalAccessException {
        Context context = new Context();
        Map<String, Object> map = convertUsingReflection(po);
        SpecialConfig specialConfig = specialConfigService.findOne(po.getCompanyCode());
        if (specialConfig != null) {
            map.put("ending", specialConfig.getPoPdfEndingText());
        }
        map.put("companyCode", po.getCompanyCode());
        setCompanyInfo(map, po.getCompanyCode());
        context.setVariables(map);
        return generator(context, "po");
    }


    private void setCompanyInfo(Map<String, Object> map, String companyCode) throws IOException {
        String companyLogo = bookKeepingService.getCompanyLogo(companyCode);
        KycCompany company = kycCommonService.getCompanyByCode(companyCode);
        map.put("name", company.getName());
        map.put("address", company.getConcatAddress());
        map.put("email", company.getEmail());
        map.put("tel", company.getPhone());
        map.put("logo", companyLogo);
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

    private Map<String, Object> convertUsingReflection(Object object) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field: fields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(object));
        }
        return map;
    }

    public ResponseEntity<byte[]> getCompany(String companyCode) throws IOException {
        String companyLogo = "";
        try {
            companyLogo = bookKeepingService.getCompanyLogo(companyCode);
        } catch (Exception e) {
            throw new ServiceException("获取公司logo出错");
        }
        KycCompany company;
        try {
            company = kycCommonService.getCompanyByCode(companyCode);
        } catch (Exception e) {
            throw new ServiceException("获取公司信息出错");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("name", company.getName());
        map.put("address", company.getConcatAddress());
        map.put("email", company.getEmail());
        map.put("tel", company.getPhone());
        map.put("logo", companyLogo);

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        TemplateEngine templateEngine = new TemplateEngine();
        try {
            templateResolver.setPrefix("/templates/");
            templateResolver.setSuffix(".html");
            templateResolver.setTemplateMode(TemplateMode.HTML);
            templateResolver.setCharacterEncoding("UTF-8"); // 设置字符集为UTF-8
        } catch (Exception e) {
            throw new ServiceException("解析templateResolver出错");
        }
        try {
            templateEngine.setTemplateResolver(templateResolver);
        } catch (Exception e) {
            throw new ServiceException("解析templateEngine出错");
        }
        map.put("skus", new ArrayList<>());
        Context context = new Context();
        context.setVariables(map);
        String html = "";
        try {
            html = templateEngine.process("so", context);
        } catch (Exception e) {
            throw new ServiceException("解析html出错");
        }

        PdfRendererBuilder builder = new PdfRendererBuilder();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpHeaders headers = new HttpHeaders();
        try {
            builder.useFastMode();
            builder.useFont(new File(this.getClass().getClassLoader().getResource("fonts/WeiRuanYaHei.ttf").getFile()), "WeiRuanYaHei");
            builder.useFont(new File(this.getClass().getClassLoader().getResource("fonts/Lato.ttf").getFile()), "Lato");
            builder.withHtmlContent(html, "");
            builder.toStream(outputStream);
            builder.run();
            headers.setContentType(MediaType.APPLICATION_PDF);
        } catch (Exception e) {
            throw new ServerException("解析pdf出错");
        }
        ResponseEntity<byte[]> body;
        try {
            body = ResponseEntity.ok().headers(headers).body(outputStream.toByteArray());
        } catch (Exception e) {
            throw new ServerException("解析pdf stream出错");
        }
        return body;
    }
}
