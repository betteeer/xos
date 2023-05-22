package com.inossem.oms.base.common.mapper;

import com.inossem.oms.base.common.domain.CarrierTable;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author shigf
 * @date 2022-11-04
 */
public interface CarrierTableMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public CarrierTable selectCarrierTableById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param carrierTable 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<CarrierTable> selectCarrierTableList(CarrierTable carrierTable);

    /**
     * 新增【请填写功能名称】
     * 
     * @param carrierTable 【请填写功能名称】
     * @return 结果
     */
    public int insertCarrierTable(CarrierTable carrierTable);

    /**
     * 修改【请填写功能名称】
     * 
     * @param carrierTable 【请填写功能名称】
     * @return 结果
     */
    public int updateCarrierTable(CarrierTable carrierTable);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteCarrierTableById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCarrierTableByIds(Long[] ids);
}
