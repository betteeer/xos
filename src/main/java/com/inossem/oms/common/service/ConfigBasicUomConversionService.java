package com.inossem.oms.common.service;

import com.inossem.oms.base.common.domain.ConfigBasicUomConversion;
import com.inossem.oms.base.common.mapper.ConfigBasicUomConversionMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author shigf
 * @date 2022-11-04
 */
@Service
public class ConfigBasicUomConversionService
{
    @Resource
    private ConfigBasicUomConversionMapper configBasicUomConversionMapper;

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param configBasicUomConversion 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    public List<ConfigBasicUomConversion> selectConfigBasicUomConversionList(ConfigBasicUomConversion configBasicUomConversion)
    {
        return configBasicUomConversionMapper.selectConfigBasicUomConversionList(configBasicUomConversion);
    }

}
