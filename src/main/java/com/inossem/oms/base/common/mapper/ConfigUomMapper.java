package com.inossem.oms.base.common.mapper;

import com.inossem.oms.base.common.domain.ConfigUom;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author shigf
 * @date 2022-11-04
 */
public interface ConfigUomMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public ConfigUom selectConfigUomById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param configUom 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<ConfigUom> selectConfigUomList(ConfigUom configUom);

    /**
     * 新增【请填写功能名称】
     * 
     * @param configUom 【请填写功能名称】
     * @return 结果
     */
    public int insertConfigUom(ConfigUom configUom);

    /**
     * 修改【请填写功能名称】
     * 
     * @param configUom 【请填写功能名称】
     * @return 结果
     */
    public int updateConfigUom(ConfigUom configUom);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteConfigUomById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteConfigUomByIds(Long[] ids);
}
