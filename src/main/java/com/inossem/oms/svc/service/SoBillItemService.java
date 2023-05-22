package com.inossem.oms.svc.service;


import com.inossem.oms.base.svc.domain.SoBillItem;
import com.inossem.oms.base.svc.mapper.SoBillItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * 【开票明细】Service业务层处理
 * 
 * @author guoh
 * @date 2022-11-20
 */
@Service
@Slf4j
public class SoBillItemService
{
    @Resource
    private SoBillItemMapper soBillItemMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public SoBillItem selectSoBillItemById(Long id)
    {
        return soBillItemMapper.selectSoBillItemById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param soBillItem 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    public List<SoBillItem> selectSoBillItemList(SoBillItem soBillItem)
    {
        return soBillItemMapper.selectSoBillItemList(soBillItem);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param soBillItem 【请填写功能名称】
     * @return 结果
     */
    public int insertSoBillItem(SoBillItem soBillItem)
    {
        return soBillItemMapper.insertSoBillItem(soBillItem);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param soBillItem 【请填写功能名称】
     * @return 结果
     */
    public int updateSoBillItem(SoBillItem soBillItem)
    {
        return soBillItemMapper.updateSoBillItem(soBillItem);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    public int deleteSoBillItemByIds(Long[] ids)
    {
        return soBillItemMapper.deleteSoBillItemByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteSoBillItemById(Long id)
    {
        return soBillItemMapper.deleteSoBillItemById(id);
    }
}
