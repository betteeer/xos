package com.inossem.oms.common.service;

import com.inossem.oms.base.common.domain.PaymentTerm;
import com.inossem.oms.base.common.mapper.PaymentTermMapper;
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
public class PaymentTermService
{
    @Resource
    private PaymentTermMapper paymentTermMapper;

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param paymentTerm 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    public List<PaymentTerm> selectPaymentTermList(PaymentTerm paymentTerm)
    {
        return paymentTermMapper.selectPaymentTermList(paymentTerm);
    }

}
