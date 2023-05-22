package com.inossem.oms.common.task;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.inossem.oms.base.svc.domain.CurrencyExchange;
import com.inossem.oms.base.svc.domain.SystemConnect;
import com.inossem.oms.base.svc.mapper.CurrencyExchangeMapper;
import com.inossem.oms.base.utils.HttpParamsUtils;
import com.inossem.oms.api.bk.api.BookKeepingService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Title: ExchangeRateTask
 * @Description: 获取汇率定时任务
 * @Author: guoh
 * @Create: 2023/3/30 11:10
 **/
@Component
@Slf4j
public class ExchangeRateTask {

    @Resource
    private BookKeepingService bookKeepingService;

    @Resource
    private CurrencyExchangeMapper currencyExchangeMapper;

    /**
     * @description: 每天凌晨2点获取汇率存储到currency_exchange表中
     * @author guoh
     * @time 2023/3/30 11:16
     */
    @Scheduled(cron = "0 0 2 * * ?")
//    @Scheduled(cron = "0 * * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void getExchangeTask() throws IOException {
        log.info(">>> 定时任务获取汇率,开始执行,执行时间:{}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        SystemConnect connect = bookKeepingService.getConnect("3002");
        log.info(">>> 汇率查询,获取到的连接信息为:{}", connect.toString());

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        String url = connect.getApiUrl() + "/system-preferences/api/v1/currency-exchange";

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("currency_fr", "USD");
        paramsMap.put("currency_to", "CAD");
        paramsMap.put("rate_date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        url += HttpParamsUtils.getBodyParams(paramsMap);

        log.info(">>> 请求地址:{}", url);

        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", bookKeepingService.getToken(connect))
                .build();

        Response response = client.newCall(request).execute();
        String responseData = response.body().string();
        log.info(">>> response:{}" + responseData);
        JSONObject responseDataJson = JSONObject.parseObject(responseData);
        log.info(">>> responseDataJson:{}", responseDataJson);
        JSONArray data = responseDataJson.getJSONArray("data");
        if (!CollectionUtils.isEmpty(data)) {
            try {
                currencyExchangeMapper.deleteAll();
                log.info(">>>> 汇率Data:{}", data);
                List<CurrencyExchange> currencyExchanges = JSON.parseArray(String.valueOf(data), CurrencyExchange.class);
                currencyExchangeMapper.insertBatch(currencyExchanges);
            } catch (Exception e) {
                throw new RuntimeException(">>> 执行汇率查询错误!");
            }
        } else {
            log.info(">>> 查询汇率,未获取到数据");
        }
    }

}
