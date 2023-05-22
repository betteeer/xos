package com.inossem.oms.common.service;

import com.inossem.oms.base.common.domain.ConfigUom;
import com.inossem.oms.base.common.mapper.ConfigUomMapper;
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
public class ConfigUomService
{
    @Resource
    private ConfigUomMapper configUomMapper;

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param configUom 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    public List<ConfigUom> selectConfigUomList(ConfigUom configUom)
    {
        return configUomMapper.selectConfigUomList(configUom);
    }
}
