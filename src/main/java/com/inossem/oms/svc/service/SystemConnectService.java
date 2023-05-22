package com.inossem.oms.svc.service;

import com.inossem.oms.base.svc.mapper.SystemConnectMapper;
import com.inossem.oms.base.svc.domain.SystemConnect;
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
public class SystemConnectService
{
    @Resource
    private SystemConnectMapper systemConnectMapper;

    /**
     * 查询system connect info
     * 
     * @param id system connect info主键
     * @return system connect info
     */
    public SystemConnect selectSyctemConectById(Long id)
    {
        return systemConnectMapper.selectSyctemConectById(id);
    }

    /**
     * 查询system connect info列表
     * 
     * @param systemConnect system connect info
     * @return system connect info
     */
    public List<SystemConnect> selectSyctemConectList(SystemConnect systemConnect)
    {
        return systemConnectMapper.selectSyctemConectList(systemConnect);
    }

    /**
     * 新增system connect info
     * 
     * @param systemConnect system connect info
     * @return 结果
     */
    public int insertSyctemConect(SystemConnect systemConnect)
    {
        return systemConnectMapper.insertSyctemConect(systemConnect);
    }

    /**
     * 修改system connect info
     * 
     * @param systemConnect system connect info
     * @return 结果
     */
    public int updateSyctemConect(SystemConnect systemConnect)
    {
        return systemConnectMapper.updateSyctemConect(systemConnect);
    }

    /**
     * 批量删除system connect info
     * 
     * @param ids 需要删除的system connect info主键
     * @return 结果
     */
    public int deleteSyctemConectByIds(Long[] ids)
    {
        return systemConnectMapper.deleteSyctemConectByIds(ids);
    }

    /**
     * 删除system connect info信息
     * 
     * @param id system connect info主键
     * @return 结果
     */
    public int deleteSyctemConectById(Long id)
    {
        return systemConnectMapper.deleteSyctemConectById(id);
    }
}
