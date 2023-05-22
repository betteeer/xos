package com.inossem.oms.base.svc.mapper;

import com.inossem.oms.base.svc.domain.SystemConnect;

import java.util.List;

/**
 * system connect infoMapper接口
 * 
 * @author ruoyi
 * @date 2022-12-10
 */
public interface SystemConnectMapper
{
    /**
     * 查询system connect info
     * 
     * @param id system connect info主键
     * @return system connect info
     */
    public SystemConnect selectSyctemConectById(Long id);

    /**
     * 查询system connect info列表
     * 
     * @param syctemConect system connect info
     * @return system connect info集合
     */
    public List<SystemConnect> selectSyctemConectList(SystemConnect syctemConect);

    /**
     * 新增system connect info
     * 
     * @param syctemConect system connect info
     * @return 结果
     */
    public int insertSyctemConect(SystemConnect syctemConect);

    /**
     * 修改system connect info
     * 
     * @param syctemConect system connect info
     * @return 结果
     */
    public int updateSyctemConect(SystemConnect syctemConect);

    /**
     * 删除system connect info
     * 
     * @param id system connect info主键
     * @return 结果
     */
    public int deleteSyctemConectById(Long id);

    /**
     * 批量删除system connect info
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSyctemConectByIds(Long[] ids);
}
