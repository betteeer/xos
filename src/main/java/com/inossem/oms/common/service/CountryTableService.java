package com.inossem.oms.common.service;

import com.inossem.oms.base.common.domain.CountryTable;
import com.inossem.oms.base.common.mapper.CountryTableMapper;
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
public class CountryTableService
{
    @Resource
    private CountryTableMapper countryTableMapper;


    /**
     * 查询【请填写功能名称】列表
     * 
     * @param countryTable 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    public List<CountryTable> selectCountryTableList(CountryTable countryTable)
    {
        return countryTableMapper.selectCountryTableList(countryTable);
    }
}
