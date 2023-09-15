package com.inossem.oms.api.kyc.api;

import com.alibaba.fastjson2.JSONObject;
import com.inossem.oms.utils.ConfigReader;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
@Service
public class KycCommonService {
    private static final Logger logger = LoggerFactory.getLogger(KycCommonService.class);

    public JSONObject getValidSubscribe(String companyCode) throws IOException {


        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        logger.info("调用kyc valid subscribe");
        String url =  ConfigReader.getConfig("KYC.baseUrl") + ConfigReader.getConfig("KYC.functions.validSubs");

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
}
