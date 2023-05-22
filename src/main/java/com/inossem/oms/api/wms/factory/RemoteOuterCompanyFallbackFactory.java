package com.inossem.oms.api.wms.factory;

import com.inossem.oms.api.wms.api.RemoteOuterCompanyService;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 用户服务降级处理
 */
@Component
public class RemoteOuterCompanyFallbackFactory implements FallbackFactory<RemoteOuterCompanyService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteOuterCompanyFallbackFactory.class);

    @Override
    public RemoteOuterCompanyService create(Throwable cause) {
        log.error("外部调用Mdm-company-api服务失败:{}", cause.getMessage());
        return new RemoteOuterCompanyService() {

            @Override
            public AjaxResult list() {
                return AjaxResult.error("外部company接口服务错误!");
            }

            @Override
            public AjaxResult getCompany(String code) {
                return AjaxResult.error("外部company接口服务错误!");
            }
        };
    }
}
