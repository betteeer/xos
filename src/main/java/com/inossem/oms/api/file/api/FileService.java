package com.inossem.oms.api.file.api;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.inossem.oms.base.svc.domain.SystemConnect;
import com.inossem.oms.mdm.service.CompanyService;
import com.inossem.oms.svc.service.SystemConnectService;
import com.inossem.oms.utils.InnerInterfaceCall;
import com.inossem.sco.common.core.utils.StringUtils;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Resource
    private SystemConnectService systemConnectService;

    @Resource
    private CompanyService companyService;

    public SystemConnect getConnect(String companyCode) {
        SystemConnect connect = new SystemConnect();
        connect.setCompanyCodeEx(Long.parseLong(companyCode));
        connect.setExSystem("bk");
        List<SystemConnect> connects = systemConnectService.selectSyctemConectList(connect);
        if (connects == null || connects.isEmpty()) {
            throw new RuntimeException("获取Token失败，" + companyCode + " 未查到bk账号信息。");
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
    public JSONObject upload(String companyCodeEx, MultipartFile multipartFile) throws IOException {
        boolean isInner = InnerInterfaceCall.isInner();
        SystemConnect connect = isInner ? null : getConnect(companyCodeEx);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .build();
        // 创建临时文件

        // 将文件内容写入临时文件中
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", multipartFile.getOriginalFilename(),
                        RequestBody.create(MediaType.parse("multipart/form-data"), multipartFile.getBytes()))
                .build();
        logger.info("调用bk v2 coa mapping");
        String url =(isInner ? "http://filestorage-service" : connect.getApiUrl()) + "/filestorage/upload";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", isInner ? null: getToken(connect))
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200) {
            throw new RuntimeException("调用BK接口失败");
        }
        String responseBody = response.body().string();
        logger.info("接收到的数据为：{}", responseBody);
        JSONObject resObj = JSONObject.parseObject(responseBody);
        return resObj;
    }

}
