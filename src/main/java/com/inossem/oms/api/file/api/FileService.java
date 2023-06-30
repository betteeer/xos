package com.inossem.oms.api.file.api;

import com.alibaba.fastjson2.JSONObject;
import com.inossem.oms.api.bk.api.ConnectionUtils;
import com.inossem.oms.base.svc.domain.SystemConnect;
import com.inossem.oms.utils.InnerInterfaceCall;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    public JSONObject upload(String companyCodeEx, MultipartFile multipartFile) throws IOException {
        boolean isInner = InnerInterfaceCall.isInner();
        SystemConnect connect = isInner ? null : ConnectionUtils.getConnection(companyCodeEx);

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
        String url =isInner ? "http://filestorage-service/upload" : (connect.getApiUrl() + "/filestorage/upload");
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", isInner ? null: ConnectionUtils.getToken(connect))
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
