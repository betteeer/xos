package com.inossem.oms.api.bk.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONException;
import com.inossem.oms.api.bk.model.BkCoaMappingModel;
import com.inossem.oms.base.svc.domain.Company;
import com.inossem.oms.base.svc.domain.SystemConnect;
import com.inossem.oms.mdm.service.CompanyService;
import com.inossem.oms.utils.InnerInterfaceCall;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class BkCoaMappingService {
    private static final Logger logger = LoggerFactory.getLogger(BkCoaMappingService.class);

    @Resource
    private CompanyService companyService;
    public BkCoaMappingModel getOrderTypeMapping(String companyCode, String code) throws IOException {
        Company company = companyService.getCompany(companyCode);

        boolean inner = InnerInterfaceCall.isInner();
        SystemConnect connect = inner ? null : ConnectionUtils.getConnection(companyCode);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/json");

        logger.info("调用bk v2 coa mapping");
        String url =  (inner ? "http://system-preferences-service:3030" : (connect.getApiUrl() + "/system-preferences")) + "/api/v1/coa-rel";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder()
//            .addQueryParameter("company_id", company.getOrgidEx())
            .addQueryParameter("company_code", companyCode)
            .addQueryParameter("type", String.valueOf(3))
            .addQueryParameter("$limit", String.valueOf(-1))
            .addQueryParameter("code", code);
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .method("GET", null)
                .addHeader("Authorization", inner ? "" : ConnectionUtils.getToken(connect))
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200) {
            throw new RuntimeException("调用BK接口失败");
        }
        String responseBody = response.body().string();
        logger.info("接收到的数据为：{}", responseBody);
        try {
            List<BkCoaMappingModel> models = JSON.parseArray(responseBody, BkCoaMappingModel.class);
            return models.get(0);
        } catch (JSONException e) {
            e.printStackTrace();
            logger.error("parse bkp coa mapping error,company:"+ companyCode + "code:" + code);
            throw new RuntimeException("parse bkp coa mapping error");
        }
    }

}
