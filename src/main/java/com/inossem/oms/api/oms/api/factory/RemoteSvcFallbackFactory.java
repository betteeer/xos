package com.inossem.oms.api.oms.api.factory;


import com.inossem.oms.api.oms.api.remote.RemoteSvcService;
import com.inossem.oms.base.svc.domain.*;
import com.inossem.sco.common.core.domain.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 文件服务降级处理
 *
 * @author shigf
 */
@Component
public class RemoteSvcFallbackFactory implements FallbackFactory<RemoteSvcService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteSvcFallbackFactory.class);

    @Override
    public RemoteSvcService create(Throwable throwable) {
        log.error("Svc服务调用失败:{}", throwable.getMessage());
        return new RemoteSvcService() {
            @Override
            public R<SoHeader> updateBpName(String companyCode, String bpNumber, String bpName) {
                return R.fail("更新so order Header BpName失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> check(String wareHouseCode, String companyCode) {
                return R.fail("查询wareHouse是否存在未发运接口失败:" + throwable.getMessage());
            }

            @Override
            public R<List<ConditionTable>> conditionTableList(String companyCode, String type) {
                return R.fail("查询conditionTable list error:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> checkSku(String skuCode, String companyCode) {
                return R.fail("查询sku是否占用 error:" + throwable.getMessage());
            }

            @Override
            public List<SystemConnect> connectLists(SystemConnect systemConnect) {
                return null;
            }

            @Override
            public List<SystemConnect> connectList(SystemConnect systemConnect) {
                return null;
            }

        };
    }
}
