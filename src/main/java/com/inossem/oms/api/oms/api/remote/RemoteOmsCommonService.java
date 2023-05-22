package com.inossem.oms.api.oms.api.remote;

import com.inossem.oms.base.common.constant.ServiceNameConstants;
import com.inossem.oms.base.svc.domain.CurrencyExchange;
import com.inossem.oms.base.svc.domain.VO.TaxCalculateResp;
import com.inossem.oms.base.svc.domain.VO.TaxTableCalculate;
import com.inossem.sco.common.core.domain.R;
import com.inossem.oms.api.oms.api.factory.RemoteOmsCommonFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * OMS COMMON feign服务
 *
 * @author shigf
 */
@FeignClient(contextId = "remoteOmsCommonService", value = ServiceNameConstants.OMS_COMMON_SERVICE, fallbackFactory = RemoteOmsCommonFallbackFactory.class)
public interface RemoteOmsCommonService {

    @PostMapping("/common/tax_calculationByCode")
    R<TaxCalculateResp> taxCaculationByCode(@RequestBody TaxTableCalculate taxTableCalculate);

    @GetMapping("/common/exchange_rate/get/{currencyCode}")
    R<CurrencyExchange> getExchageRate(@PathVariable("currencyCode") String currencyCode);

}
