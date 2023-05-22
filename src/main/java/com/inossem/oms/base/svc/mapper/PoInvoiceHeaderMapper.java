package com.inossem.oms.base.svc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inossem.oms.base.svc.domain.PoInvoiceHeader;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2022-12-09
 */
public interface PoInvoiceHeaderMapper extends BaseMapper<PoInvoiceHeader>
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    PoInvoiceHeader selectPoInvoiceHeaderById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param poInvoiceHeader 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    List<PoInvoiceHeader> selectPoInvoiceHeaderList(PoInvoiceHeader poInvoiceHeader);

    /**
     * 新增【请填写功能名称】
     * 
     * @param poInvoiceHeader 【请填写功能名称】
     * @return 结果
     */
    int insertPoInvoiceHeader(PoInvoiceHeader poInvoiceHeader);

    /**
     * 修改【请填写功能名称】
     * 
     * @param poInvoiceHeader 【请填写功能名称】
     * @return 结果
     */
    int updatePoInvoiceHeader(PoInvoiceHeader poInvoiceHeader);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    int deletePoInvoiceHeaderById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deletePoInvoiceHeaderByIds(Long[] ids);
}
