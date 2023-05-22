package com.inossem.oms.svc.service;

import com.inossem.oms.base.svc.domain.ConditionTable;
import com.inossem.oms.base.svc.mapper.ConditionTableMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * system connect infoService业务层处理
 * 
 * @author ruoyi
 * @date 2022-12-10
 */
@Service
public class ConditionTableService
{
    @Resource
    private ConditionTableMapper conditionTableMapper;

    /**
     * 查询system connect info
     * 
     * @param id system connect info主键
     * @return system connect info
     */
    public ConditionTable selectConditionTableById(Long id)
    {
        return conditionTableMapper.selectConditionTableById(id);
    }

    /**
     * 查询system connect info列表
     * 
     * @param conditionTable system connect info
     * @return system connect info
     */
    public List<ConditionTable> selectConditionTableList(ConditionTable conditionTable)
    {
        return conditionTableMapper.selectConditionTableList(conditionTable);
    }

    /**
     * 新增system connect info
     * 
     * @param conditionTable system connect info
     * @return 结果
     */
    public int insertConditionTable(ConditionTable conditionTable)
    {
        return conditionTableMapper.insertConditionTable(conditionTable);
    }

    /**
     * 修改system connect info
     * 
     * @param conditionTable system connect info
     * @return 结果
     */
    public int updateConditionTable(ConditionTable conditionTable)
    {
        return conditionTableMapper.updateConditionTable(conditionTable);
    }

    /**
     * 批量删除system connect info
     * 
     * @param ids 需要删除的system connect info主键
     * @return 结果
     */
    public int deleteConditionTableByIds(Long[] ids)
    {
        return conditionTableMapper.deleteConditionTableByIds(ids);
    }

    /**
     * 删除system connect info信息
     * 
     * @param id system connect info主键
     * @return 结果
     */
    public int deleteConditionTableById(Long id)
    {
        return conditionTableMapper.deleteConditionTableById(id);
    }

    public List<ConditionTable> innerList(String companyCode, String type) {
        ConditionTable table = new ConditionTable();
        table.setCompanyCode(companyCode);
        table.setIsDeleted(0);
        table.setConditionType(type);
        List<ConditionTable> list = this.selectConditionTableList(table);
        return list;
    }
}
