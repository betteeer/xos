package com.inossem.oms.base.svc.mapper;

import com.inossem.oms.base.svc.domain.CoaList;

import java.util.List;

/**
 * system connect infoMapper接口
 * 
 * @author ruoyi
 * @date 2022-12-10
 */
public interface CoaListMapper 
{
    /**
     * 查询system connect info
     * 
     * @param id system connect info主键
     * @return system connect info
     */
    public CoaList selectCoaListById(Long id);

    /**
     * 查询system connect info列表
     * 
     * @param coaList system connect info
     * @return system connect info集合
     */
    public List<CoaList> selectCoaListList(CoaList coaList);

    /**
     * 新增system connect info
     * 
     * @param coaList system connect info
     * @return 结果
     */
    public int insertCoaList(CoaList coaList);

    /**
     * 修改system connect info
     * 
     * @param coaList system connect info
     * @return 结果
     */
    public int updateCoaList(CoaList coaList);

    /**
     * 删除system connect info
     * 
     * @param id system connect info主键
     * @return 结果
     */
    public int deleteCoaListById(Long id);

    /**
     * 批量删除system connect info
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCoaListByIds(Long[] ids);
}
