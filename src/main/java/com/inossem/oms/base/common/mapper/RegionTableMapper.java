package com.inossem.oms.base.common.mapper;

import com.inossem.oms.base.common.domain.RegionTable;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author shigf
 * @date 2022-11-04
 */
public interface RegionTableMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public RegionTable selectRegionTableById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param regionTable 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<RegionTable> selectRegionTableList(RegionTable regionTable);

    /**
     * 新增【请填写功能名称】
     * 
     * @param regionTable 【请填写功能名称】
     * @return 结果
     */
    public int insertRegionTable(RegionTable regionTable);

    /**
     * 修改【请填写功能名称】
     * 
     * @param regionTable 【请填写功能名称】
     * @return 结果
     */
    public int updateRegionTable(RegionTable regionTable);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteRegionTableById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRegionTableByIds(Long[] ids);
}
