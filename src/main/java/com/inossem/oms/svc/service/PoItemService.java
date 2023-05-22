package com.inossem.oms.svc.service;

import com.inossem.oms.base.svc.domain.PoItem;
import com.inossem.oms.base.svc.mapper.PoItemMapper;
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
public class PoItemService
{
    @Resource
    private PoItemMapper poItemMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public PoItem selectPoItemById(Long id)
    {
        return poItemMapper.selectPoItemById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param poItem 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    public List<PoItem> selectPoItemList(PoItem poItem)
    {
        return poItemMapper.selectPoItemList(poItem);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param poItem 【请填写功能名称】
     * @return 结果
     */
    public int insertPoItem(PoItem poItem)
    {
        return poItemMapper.insertPoItem(poItem);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param poItem 【请填写功能名称】
     * @return 结果
     */
    public int updatePoItem(PoItem poItem)
    {
        return poItemMapper.updatePoItem(poItem);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    public int deletePoItemByIds(Long[] ids)
    {
        return poItemMapper.deletePoItemByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deletePoItemById(Long id)
    {
        return poItemMapper.deletePoItemById(id);
    }
}
