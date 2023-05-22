package com.inossem.oms.base.common.mapper;

import com.inossem.oms.base.common.domain.PaymentTerm;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author shigf
 * @date 2022-11-04
 */
public interface PaymentTermMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public PaymentTerm selectPaymentTermById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param paymentTerm 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<PaymentTerm> selectPaymentTermList(PaymentTerm paymentTerm);

    /**
     * 新增【请填写功能名称】
     * 
     * @param paymentTerm 【请填写功能名称】
     * @return 结果
     */
    public int insertPaymentTerm(PaymentTerm paymentTerm);

    /**
     * 修改【请填写功能名称】
     * 
     * @param paymentTerm 【请填写功能名称】
     * @return 结果
     */
    public int updatePaymentTerm(PaymentTerm paymentTerm);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deletePaymentTermById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePaymentTermByIds(Long[] ids);
}
