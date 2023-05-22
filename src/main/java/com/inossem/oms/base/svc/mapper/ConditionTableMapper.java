package com.inossem.oms.base.svc.mapper;

import com.inossem.oms.base.svc.domain.ConditionTable;

import java.util.List;

/**
 * system connect infoMapper接口
 * 
 * @author ruoyi
 * @date 2022-12-10
 */
public interface ConditionTableMapper 
{
    /**
     * 查询system connect info
     * 
     * @param id system connect info主键
     * @return system connect info
     */
    public ConditionTable selectConditionTableById(Long id);

    /**
     * 查询system connect info列表
     * 
     * @param conditionTable system connect info
     * @return system connect info集合
     */
    public List<ConditionTable> selectConditionTableList(ConditionTable conditionTable);

    /**
     * 新增system connect info
     * 
     * @param conditionTable system connect info
     * @return 结果
     */
    public int insertConditionTable(ConditionTable conditionTable);

    /**
     * 修改system connect info
     * 
     * @param conditionTable system connect info
     * @return 结果
     */
    public int updateConditionTable(ConditionTable conditionTable);

    /**
     * 删除system connect info
     * 
     * @param id system connect info主键
     * @return 结果
     */
    public int deleteConditionTableById(Long id);

    /**
     * 批量删除system connect info
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteConditionTableByIds(Long[] ids);
}
