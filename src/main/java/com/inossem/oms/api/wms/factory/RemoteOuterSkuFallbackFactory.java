package com.inossem.oms.api.wms.factory;

import com.inossem.oms.api.wms.api.RemoteOuterSkuService;
import com.inossem.oms.base.svc.domain.VO.SkuListReqVO;
import com.inossem.oms.base.svc.domain.VO.SkuVO;
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
public class RemoteOuterSkuFallbackFactory implements FallbackFactory<RemoteOuterSkuService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteOuterSkuFallbackFactory.class);

    @Override
    public RemoteOuterSkuService create(Throwable cause) {
        log.error("外部调用Mdm-sku-api服务失败:{}", cause.getMessage());
        return new RemoteOuterSkuService() {

            @Override
            public TableDataInfo list(SkuListReqVO skuListReqVO, Integer pageNum, Integer pageSize) {
                return new TableDataInfo(null, 0);
            }

            @Override
            public AjaxResult create(SkuVO skuVO) {
                return AjaxResult.error("外部sku创建接口服务错误!");
            }

            @Override
            public AjaxResult modifySku(SkuVO skuVO) {
                return AjaxResult.error("外部sku创建接口服务错误!");
            }
        };
    }
}
