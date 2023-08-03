package com.inossem.oms.mdm.service;

import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.inossem.oms.base.svc.domain.SkuMaster;
import com.inossem.oms.base.svc.domain.VO.SimpleSkuMasterVO;
import com.inossem.oms.base.svc.mapper.SkuMasterMapper;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class SkuNewService {

    @Resource
    private SkuMasterMapper skuMasterMapper;
    public List<SimpleSkuMasterVO> getListFilterKitting(String companyCode, String search) {
        MPJLambdaWrapper<SkuMaster> skuMasterWrapper = new MPJLambdaWrapper<>();
        skuMasterWrapper.eq(SkuMaster::getCompanyCode, companyCode)
                .eq(SkuMaster::getSkuType, "IN")
                .eq(SkuMaster::getIsKitting, 0)
                .eq(SkuMaster::getIsDeleted, 0)
                .nested(StringUtils.isNotEmpty(search), i -> {
                    i.like(SkuMaster::getSkuName, search).or().like(SkuMaster::getSkuNumber, search);
                })
                .select(SkuMaster::getId, SkuMaster::getSkuNumber, SkuMaster::getSkuName, SkuMaster::getBasicUom);
        List<SimpleSkuMasterVO> simpleSkuMasters = skuMasterMapper.selectJoinList(SimpleSkuMasterVO.class, skuMasterWrapper);
        return simpleSkuMasters;
    }
}
