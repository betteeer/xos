package com.inossem.oms.svc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inossem.oms.base.svc.domain.PoHeader;
import com.inossem.oms.base.svc.domain.SoHeader;
import com.inossem.oms.base.svc.domain.StockBalance;
import com.inossem.oms.base.svc.mapper.PoHeaderMapper;
import com.inossem.oms.base.svc.mapper.SoHeaderMapper;
import com.inossem.oms.base.svc.mapper.StockBalanceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class SvcFeignService {

    @Resource
    private SoHeaderMapper soHeaderMapper;

    @Resource
    private PoHeaderMapper poHeaderMapper;

    @Resource
    private StockBalanceMapper stockBalanceMapper;

    public Boolean checkSku(String skuCode, String companyCode) {
        List<SoHeader> soHeaderList = soHeaderMapper.checkSku(skuCode, companyCode);
        if (soHeaderList.size() > 0) {
            return false;
        }
        List<PoHeader> poHeaderList = poHeaderMapper.checkSku(skuCode, companyCode);
        if (poHeaderList.size() > 0) {
            return false;
        }
        List<StockBalance> stockBalanceList = stockBalanceMapper.selectList(new LambdaQueryWrapper<StockBalance>()
                .eq(StockBalance::getSkuNumber, skuCode)
                .eq(StockBalance::getCompanyCode, companyCode)
                .eq(StockBalance::getIsDeleted, 0)
        );
        return stockBalanceList.size() <= 0;
    }


}
