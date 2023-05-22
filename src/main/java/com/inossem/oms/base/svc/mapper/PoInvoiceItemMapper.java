package com.inossem.oms.base.svc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inossem.oms.base.svc.domain.PoInvoiceItem;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2022-12-09
 */
public interface PoInvoiceItemMapper extends BaseMapper<PoInvoiceItem>
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    PoInvoiceItem selectPoInvoiceItemById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param poInvoiceItem 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    List<PoInvoiceItem> selectPoInvoiceItemList(PoInvoiceItem poInvoiceItem);

    /**
     * 新增【请填写功能名称】
     * 
     * @param poInvoiceItem 【请填写功能名称】
     * @return 结果
     */
    int insertPoInvoiceItem(PoInvoiceItem poInvoiceItem);

    /**
     * 修改【请填写功能名称】
     * 
     * @param poInvoiceItem 【请填写功能名称】
     * @return 结果
     */
    int updatePoInvoiceItem(PoInvoiceItem poInvoiceItem);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    int deletePoInvoiceItemById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deletePoInvoiceItemByIds(Long[] ids);
}
