package com.inossem.oms.svc.service;

import com.inossem.oms.base.svc.domain.PoInvoiceItem;
import com.inossem.oms.base.svc.mapper.PoInvoiceItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2022-12-09
 */
@Service
@Slf4j
public class PoInvoiceItemService {

    @Resource
    private PoInvoiceItemMapper poInvoiceItemMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */

    public PoInvoiceItem selectPoInvoiceItemById(Long id) {
        return poInvoiceItemMapper.selectPoInvoiceItemById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param poInvoiceItem 【请填写功能名称】
     * @return 【请填写功能名称】
     */

    public List<PoInvoiceItem> selectPoInvoiceItemList(PoInvoiceItem poInvoiceItem) {
        return poInvoiceItemMapper.selectPoInvoiceItemList(poInvoiceItem);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param poInvoiceItem 【请填写功能名称】
     * @return 结果
     */

    public int insertPoInvoiceItem(PoInvoiceItem poInvoiceItem) {
        return poInvoiceItemMapper.insertPoInvoiceItem(poInvoiceItem);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param poInvoiceItem 【请填写功能名称】
     * @return 结果
     */

    public int updatePoInvoiceItem(PoInvoiceItem poInvoiceItem) {
        return poInvoiceItemMapper.updatePoInvoiceItem(poInvoiceItem);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */

    public int deletePoInvoiceItemByIds(Long[] ids) {
        return poInvoiceItemMapper.deletePoInvoiceItemByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】主键
     * @return 结果
     */

    public int deletePoInvoiceItemById(Long id) {
        return poInvoiceItemMapper.deletePoInvoiceItemById(id);
    }
}
