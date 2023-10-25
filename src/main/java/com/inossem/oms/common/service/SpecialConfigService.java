package com.inossem.oms.common.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inossem.oms.base.common.domain.SpecialConfig;
import com.inossem.oms.base.common.mapper.SpecialConfigMapper;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class SpecialConfigService {
    @Resource
    private SpecialConfigMapper specialConfigMapper;

    public SpecialConfig findOne(String companyCode) {
        LambdaQueryWrapper<SpecialConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpecialConfig::getCompanyCode, companyCode);
        SpecialConfig sc = specialConfigMapper.selectOne(wrapper);
        if (StringUtils.isNull(sc)) {
            SpecialConfig s = new SpecialConfig();
            s.setCompanyCode(companyCode);
            return s;
        } else {
            return sc;
        }
    }
    public boolean update(SpecialConfig specialConfig) {
        LambdaQueryWrapper<SpecialConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpecialConfig::getCompanyCode, specialConfig.getCompanyCode());
        if (specialConfigMapper.selectOne(wrapper) == null) {
            return specialConfigMapper.insert(specialConfig) > 0;
        } else {
            return specialConfigMapper.update(specialConfig, wrapper) > 0;
        }
    }
}
