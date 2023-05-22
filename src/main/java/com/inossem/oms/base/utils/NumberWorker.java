package com.inossem.oms.base.utils;

import com.inossem.oms.base.common.constant.ModuleConstant;
import com.inossem.oms.base.svc.mapper.MaterialDocMapper;
import com.inossem.sco.common.core.exception.ServiceException;
import com.inossem.sco.common.core.utils.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 实体的编号生成器
 */
@Component
public class NumberWorker {

    private static NumberWorker numberWorker = null;

    static{
        numberWorker = new NumberWorker();
    }

    private NumberWorker() {}

    @Resource
    private MaterialDocMapper materialDocMapper;


    /**
     * 生成ID
     * @param orderNumberType orderNumber的类型
     * @param args  对于SALES_DELIVERY_NUMBER和PURCHASE_DELIVERY_NUMBER类型，需要传入对应的OrderNumber
     * @return
     */
    public synchronized long generateId(String companyCode,String orderNumberType,Long... args) {
        long number = 0L;
        //查询当前最大值,并累加上起始值
        switch (orderNumberType){
            case ModuleConstant.ORDER_NUMBER_TYPE.MATERIAL_DOC:
                Long maxNumber = materialDocMapper.getMaxNumber(companyCode);
                if(StringUtils.isNull(maxNumber)){
                    maxNumber =  ModuleConstant.ORDER_NUMBER_START.MATERIAL_DOC;
                }
                number = maxNumber + 1;
                break;
            default:
                throw new ServiceException("OrderNumber类型不存在");
        }
        return number;
    }
}
