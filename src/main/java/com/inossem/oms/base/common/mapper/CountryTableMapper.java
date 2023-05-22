package com.inossem.oms.base.common.mapper;

import com.inossem.oms.base.common.domain.CountryTable;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author shigf
 * @date 2022-11-04
 */
public interface CountryTableMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public CountryTable selectCountryTableById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param countryTable 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<CountryTable> selectCountryTableList(CountryTable countryTable);

    /**
     * 新增【请填写功能名称】
     * 
     * @param countryTable 【请填写功能名称】
     * @return 结果
     */
    public int insertCountryTable(CountryTable countryTable);

    /**
     * 修改【请填写功能名称】
     * 
     * @param countryTable 【请填写功能名称】
     * @return 结果
     */
    public int updateCountryTable(CountryTable countryTable);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteCountryTableById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCountryTableByIds(Long[] ids);
}
