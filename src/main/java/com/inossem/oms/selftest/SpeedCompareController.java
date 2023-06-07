package com.inossem.oms.selftest;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.inossem.oms.base.svc.domain.SystemConnect;
import com.inossem.oms.base.utils.HttpParamsUtils;
import com.inossem.oms.svc.service.SystemConnectService;
import com.inossem.sco.common.core.utils.StringUtils;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/speed")
public class SpeedCompareController {

    @Resource
    private SystemConnectService systemConnectService;
    private static final Logger logger = LoggerFactory.getLogger(SpeedCompareController.class);
    public SystemConnect getConnect(String companyCode) {
        SystemConnect connect = new SystemConnect();
        connect.setCompanyCodeEx(Long.parseLong(companyCode));
        connect.setExSystem("bk");
//        List<SystemConnect> connects = remoteSvcService.connectLists(connect);
        List<SystemConnect> connects = systemConnectService.selectSyctemConectList(connect);
        if (connects == null || connects.isEmpty()) {
            throw new RuntimeException("获取TOken失败，" + companyCode + " 未查到bk账号信息。");
        }
        return connects.get(0);
    }
    public String getToken(SystemConnect connect) throws IOException {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType mediaType = MediaType.parse("application/json");

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("strategy", "local");
        paramsMap.put("account", connect.getUserNameEx());
        paramsMap.put("password", connect.getPasswordEx());

        String params = JSONObject.toJSONString(paramsMap,
                JSONWriter.Feature.WriteNullStringAsEmpty);
        logger.info("params:{}", params);

        RequestBody body = RequestBody.create(mediaType, params);

        Request request = new Request.Builder()
                .url(connect.getApiUrl() + "/users/api/authentication")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();

        String response = client
                .newCall(request)
                .execute().body().string();
        JSONObject obj = JSONObject.parseObject(response);
        logger.info("accessToken:{}", obj.getString("accessToken"));
        if (StringUtils.isNotBlank(obj.getString("accessToken"))) {
            return "Bearer " + obj.getString("accessToken");
        } else {
            throw new RuntimeException("获取Token失败，" + response);
        }
    }
    @GetMapping("/outer")
    public HashMap<String, Object> outer() throws IOException {
        long stime = System.nanoTime();
        SystemConnect connect = getConnect("3002");

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("company_id", 71);
        paramsMap.put("company_code", 3002);
        paramsMap.put("contact_name", "");

        String url = connect.getApiUrl() + "/system-preferences/api/v1/contact";

        url += HttpParamsUtils.getBodyParams(paramsMap);
        logger.info(">>> 查询bp详情,请求地址url:{}", url);

        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", getToken(connect))
                .build();

        Response response = client.newCall(request).execute();
        String bo = response.body().string();
        logger.info("接收到的数据为：{}", bo);

        JSONObject res = JSONObject.parseObject(bo);

        HashMap<String, Object> map = new HashMap<>();
        long etime = System.nanoTime();
        map.put("data", res);
        map.put("time", (etime - stime) / Math.pow(10, 9));
        return map;
    }

    @GetMapping("/inner")
    public HashMap<String, Object> inner() throws IOException {
        long stime = System.nanoTime();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
//        Map<String, Object> paramsMap = new HashMap<>();
//        paramsMap.put("company_id", 71);
//        paramsMap.put("company_code", 3002);
//        paramsMap.put("contact_name", "");
        String url = "http://system-preferences-service:3030" + "/api/v1/coa-rel?company_id=71&company_code=3002&type=2&$limit=-1";
//      String url = "http://system-preferences-service:3030/api/v1/contact";
//        url += HttpParamsUtils.getBodyParams(paramsMap);
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        String bo = response.body().string();
        logger.info("接收到的数据为：{}", bo);

        JSONObject res = JSONObject.parseObject(bo);

        HashMap<String, Object> map = new HashMap<>();
        long etime = System.nanoTime();
        map.put("data", res);
        map.put("time", (etime - stime) / Math.pow(10, 9));
        return map;
    }

}
