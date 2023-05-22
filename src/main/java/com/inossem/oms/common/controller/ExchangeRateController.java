package com.inossem.oms.common.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inossem.oms.base.svc.domain.CurrencyExchange;
import com.inossem.oms.base.svc.mapper.CurrencyExchangeMapper;
import com.inossem.sco.common.core.domain.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Title: ExchangeRateController
 * @Description: 获取汇率定时任务
 * @Author: guoh
 * @Create: 2023/4/2 17:10
 **/
@RestController
@RequestMapping("/common/exchange_rate")
public class ExchangeRateController {

    @Resource
    private CurrencyExchangeMapper currencyExchangeMapper;

    /**
     * 根据currencyCode获取rate
     *
     * @param currencyCode
     * @return
     */
    @GetMapping("/get/{currencyCode}")
    public R<CurrencyExchange> getExchageRate(@PathVariable("currencyCode") String currencyCode) {
        LambdaQueryWrapper<CurrencyExchange> currencyExchangeLambdaQueryWrapper = new LambdaQueryWrapper<CurrencyExchange>()
                .eq(CurrencyExchange::getCurrencyFr, currencyCode);
        return R.ok(currencyExchangeMapper.selectOne(currencyExchangeLambdaQueryWrapper));
    }

}
