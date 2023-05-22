package com.inossem.oms.api.oms.api.factory;


import com.inossem.oms.base.svc.domain.CurrencyExchange;
import com.inossem.oms.base.svc.domain.VO.TaxCalculateResp;
import com.inossem.oms.base.svc.domain.VO.TaxTableCalculate;
import com.inossem.sco.common.core.domain.R;
import com.inossem.oms.api.oms.api.remote.RemoteOmsCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 文件服务降级处理
 *
 * @author shigf
 */
@Component
public class RemoteOmsCommonFallbackFactory implements FallbackFactory<RemoteOmsCommonService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteOmsCommonFallbackFactory.class);

    @Override
    public RemoteOmsCommonService create(Throwable throwable) {
        log.error("oms-common服务调用失败:{}", throwable.getMessage());
        return new RemoteOmsCommonService() {

            @Override
            public R<TaxCalculateResp> taxCaculationByCode(TaxTableCalculate taxTableCalculate) {
                return R.fail("更新so order Header BpName失败:" + throwable.getMessage());
            }

            @Override
            public R<CurrencyExchange> getExchageRate(String currencyCode) {
                return R.fail("获取exchange rate失败:" + throwable.getMessage());
            }
        };
    }
}
