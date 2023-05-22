package com.inossem.oms.base.svc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inossem.oms.base.svc.domain.SoBillItem;

import java.util.List;

/**
 * 【开票明细】Mapper接口
 * 
 * @author guoh
 * @date 2022-11-20
 */
public interface SoBillItemMapper  extends BaseMapper<SoBillItem>
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    SoBillItem selectSoBillItemById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param soBillItem 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    List<SoBillItem> selectSoBillItemList(SoBillItem soBillItem);

    /**
     * 新增【请填写功能名称】
     * 
     * @param soBillItem 【请填写功能名称】
     * @return 结果
     */
    int insertSoBillItem(SoBillItem soBillItem);

    /**
     * 修改【请填写功能名称】
     * 
     * @param soBillItem 【请填写功能名称】
     * @return 结果
     */
    int updateSoBillItem(SoBillItem soBillItem);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    int deleteSoBillItemById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteSoBillItemByIds(Long[] ids);
}
