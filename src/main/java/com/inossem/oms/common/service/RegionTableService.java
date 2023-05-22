package com.inossem.oms.common.service;

import com.inossem.oms.base.common.domain.RegionTable;
import com.inossem.oms.base.common.mapper.RegionTableMapper;
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
public class RegionTableService
{
    @Resource
    private RegionTableMapper regionTableMapper;

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param regionTable 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    public List<RegionTable> selectRegionTableList(RegionTable regionTable)
    {
        return regionTableMapper.selectRegionTableList(regionTable);
    }

}
