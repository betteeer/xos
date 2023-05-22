package com.inossem.oms.common.service;

import com.inossem.oms.base.common.domain.CarrierTable;
import com.inossem.oms.base.common.mapper.CarrierTableMapper;
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
public class CarrierTableService
{

    @Resource
    private CarrierTableMapper carrierTableMapper;

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param carrierTable 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    public List<CarrierTable> selectCarrierTableList(CarrierTable carrierTable)
    {
        return carrierTableMapper.selectCarrierTableList(carrierTable);
    }

}
