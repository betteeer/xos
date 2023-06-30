package com.inossem.oms.api.bk.api;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.inossem.oms.base.svc.domain.SystemConnect;
import com.inossem.oms.svc.service.SystemConnectService;
import com.inossem.sco.common.core.utils.StringUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ConnectionUtils {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionUtils.class);

    private static SystemConnectService systemConnectService;

    @Resource
    public void setSystemConnectService(SystemConnectService systemConnectService) {
        ConnectionUtils.systemConnectService = systemConnectService;
    }
    public static SystemConnect getConnection(String companyCode) {
        SystemConnect connect = new SystemConnect();
        connect.setCompanyCodeEx(Long.parseLong(companyCode));
        connect.setExSystem("bk");
        List<SystemConnect> connects = systemConnectService.selectSyctemConectList(connect);
        if (connects == null || connects.isEmpty()) {
            logger.error(companyCode + " 未查到bk账号信息。");
            throw new RuntimeException(companyCode + " 未查到bk账号信息。");
        }
        return connects.get(0);
    }

    public static String getToken(SystemConnect connect) throws IOException {
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

}
