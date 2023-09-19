package com.inossem.oms.api.kyc.api;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.inossem.oms.api.kyc.model.KycCompany;
import com.inossem.oms.utils.ConfigReader;
import com.inossem.sco.common.core.utils.StringUtils;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
@Service
public class KycCommonService {
    private static final Logger logger = LoggerFactory.getLogger(KycCommonService.class);

    private String getRequestUrl(String key) {
        return ConfigReader.getConfig("KYC.baseUrl") + ConfigReader.getConfig(key);
    }
    public JSONObject getValidSubscribe(String companyCode) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        logger.info("调用kyc valid subscribe");
        String url =  getRequestUrl("KYC.functions.validSubs");
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder()
                .addQueryParameter("companyCode", companyCode);
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .method("GET", null)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        if (response.code() != 200) {
            throw new RuntimeException("调用kyc接口失败");
        }
        String responseBody = response.body().string();
        logger.info("接收到的数据为：{}", responseBody);
        return JSONObject.parseObject(responseBody).getJSONObject("body");
    }

    public KycCompany getCompanyByCode(String companyCode) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        String url = getRequestUrl("KYC.functions.getCompanyInfoByCode");
        logger.info("调用kyc getCompanyInfoByCode");
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("companyCode", companyCode);

        String params = JSONObject.toJSONString(paramsMap,
                JSONWriter.Feature.WriteNullStringAsEmpty);
        logger.info("params:{}", params);

        RequestBody body = RequestBody.Companion.create(params, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        String response = client
                .newCall(request)
                .execute().body().string();
        logger.info("调用kyc getCompanyInfoByCode 结果为: {}", response);
        JSONObject obj = JSONObject.parseObject(response);
        if (obj == null) {
            throw new RuntimeException("调用kyc获取company信息失败");
        };
        String taxChartJson = obj.getString("taxChart");
        obj.remove("taxChart");
        List<KycCompany.TaxItem> taxItems = StringUtils.isEmpty(taxChartJson) ? new ArrayList<>() : JSON.parseArray(taxChartJson, KycCompany.TaxItem.class);
        KycCompany company = JSONObject.parseObject(obj.toString(), KycCompany.class);
        company.setTaxChart(taxItems);
        return company;
    }
}
