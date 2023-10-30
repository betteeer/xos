package com.inossem.oms.svc.service;

import com.inossem.oms.api.bk.api.BookKeepingService;
import com.inossem.oms.api.kyc.api.KycCommonService;
import com.inossem.oms.api.kyc.model.KycCompany;
import com.inossem.oms.base.common.domain.SpecialConfig;
import com.inossem.oms.base.svc.domain.DTO.PdfPoFormDTO;
import com.inossem.oms.base.svc.domain.DTO.PdfSoFormDTO;
import com.inossem.oms.common.service.SpecialConfigService;
import com.inossem.sco.common.core.exception.ServiceException;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.layout.font.FontProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
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
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
//        for (int i = 0; i < 10; i++) {
//            PdfSoFormDTO.Sku sku = new PdfSoFormDTO.Sku();
//            // 拷贝需要的属性
//            BeanUtils.copyProperties(so.getSkus().get(0), sku);
//            sku.setOrder(String.valueOf(i + 1) );
//            skus.add(sku);
//        }
//        map.put("skus", skus);
//        so.setSkus(skus);
        SpecialConfig specialConfig = specialConfigService.findOne(so.getCompanyCode());
        if (specialConfig != null) {
            map.put("ending", specialConfig.getSoPdfEndingText());
        }
        map.put("companyCode", so.getCompanyCode());
        setCompanyInfo(map, so.getCompanyCode());
        setSplitStartEndFlag(map, so.getSkus() != null ? so.getSkus().size() : 0, 12);
        context.setVariables(map);
        String html = generatorHtml(context, "so");
        return convertToPdf(html);
    }

    public ResponseEntity<byte[]> generatePoPdf(PdfPoFormDTO po) throws IOException, IllegalAccessException {
        Context context = new Context();
        Map<String, Object> map = convertUsingReflection(po);
//        List<PdfPoFormDTO.Sku> skus = new ArrayList<>();
//        for (int i = 0; i < 56; i++) {
//            PdfPoFormDTO.Sku sku = new PdfPoFormDTO.Sku();
//            // 拷贝需要的属性
//            BeanUtils.copyProperties(po.getSkus().get(0), sku);
//            sku.setOrder(String.valueOf(i + 1));
//            skus.add(sku);
//        }
//        map.put("skus", skus);
//        po.setSkus(skus);
        SpecialConfig specialConfig = specialConfigService.findOne(po.getCompanyCode());
        if (specialConfig != null) {
            map.put("ending", specialConfig.getPoPdfEndingText());
        }
        map.put("companyCode", po.getCompanyCode());
        setCompanyInfo(map, po.getCompanyCode());
        setSplitStartEndFlag(map, po.getSkus() != null ? po.getSkus().size() : 0, 11);
        context.setVariables(map);
        String html = generatorHtml(context, "po");
        return convertToPdf(html);
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

    private String generatorHtml(Context context, String templateName) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8"); // 设置字符集为UTF-8
        // 创建Thymeleaf模板引擎
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        String html = templateEngine.process(templateName, context);
        return html;
    }

    private ResponseEntity<byte[]> convertToPdf(String html) {
        try {
            ConverterProperties converterProperties = new ConverterProperties();
            setFontProvider(converterProperties);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(html, outputStream, converterProperties);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            return ResponseEntity.ok().headers(headers).body(outputStream.toByteArray());
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }
    public void setFontProvider(ConverterProperties converterProperties) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        FontProvider fontProvider = new FontProvider();
        String[] fonts = {"fonts/WeiRuanYaHei.ttf", "fonts/Lato.ttf"};
        for (String font : fonts) {
            InputStream resourceAsStream = classLoader.getResourceAsStream(font);
            assert resourceAsStream != null;
            byte[] fontData = IOUtils.toByteArray(resourceAsStream);
            fontProvider.addFont(fontData);
        }
        converterProperties.setFontProvider(fontProvider);
    }
    /**
     * @param map context上下文需要的数据
     * @param size skus的size
     * @param firstPageCeil 第一页可以容纳的条数，实际上需要 减去1，比如这边传12，实际上最多可以容纳11条, 也就是第二页的第一条的序号
     */
    private void setSplitStartEndFlag(Map<String, Object> map, int size, int firstPageCeil) {
        List<Integer[]> list = new ArrayList<>();
        list.add(new Integer[]{1, firstPageCeil});
        int pageSize = 19;
        int start = firstPageCeil;
        int end = firstPageCeil + pageSize;
        while (start <= size) {
            list.add(new Integer[]{start, end});
            start += pageSize;
            end += pageSize;
        }
        map.put("splitStartEndFlag", list);
    }

    private Map<String, Object> convertUsingReflection(Object object) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(object));
        }
        return map;
    }
}
