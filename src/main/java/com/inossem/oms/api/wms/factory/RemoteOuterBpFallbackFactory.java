package com.inossem.oms.api.wms.factory;

import com.inossem.oms.api.wms.api.RemoteOuterBpService;
import com.inossem.oms.base.svc.domain.BusinessPartner;
import com.inossem.oms.base.svc.domain.VO.BPListVO;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 用户服务降级处理
 */
@Component
public class RemoteOuterBpFallbackFactory implements FallbackFactory<RemoteOuterBpService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteOuterBpFallbackFactory.class);

    @Override
    public RemoteOuterBpService create(Throwable cause) {
        log.error("外部调用Mdm-bp-api服务失败:{}", cause.getMessage());
        return new RemoteOuterBpService() {
            @Override
            public TableDataInfo list(BPListVO bpListVO) {
                return new TableDataInfo(null, 0);
            }

            @Override
            public AjaxResult create(BusinessPartner businessPartner) {
                return AjaxResult.error("外部bp创建接口服务错误!");
            }

            @Override
            public AjaxResult modify(BusinessPartner businessPartner) {
                return AjaxResult.error("外部bp修改接口服务错误!");
            }
        };
    }
}
