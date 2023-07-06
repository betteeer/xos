package com.inossem.oms.mdm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inossem.oms.base.svc.domain.SkuMaster;
import com.inossem.oms.base.svc.mapper.SkuMasterMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class SkuNewService {

    @Resource
    private SkuMasterMapper skuMasterMapper;
    public List<SkuMaster> getListFilterKitting(String companyCode) {
        LambdaQueryWrapper<SkuMaster> skuMasterWrapper = new LambdaQueryWrapper<>();
        skuMasterWrapper.eq(SkuMaster::getCompanyCode, companyCode)
                .eq(SkuMaster::getSkuType, "IN")
                .eq(SkuMaster::getIsKitting, 0)
                .eq(SkuMaster::getIsDeleted, 0);
        List<SkuMaster> skuMasters = skuMasterMapper.selectList(skuMasterWrapper);
        return skuMasters;
    }
}
