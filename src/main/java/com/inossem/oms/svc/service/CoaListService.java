package com.inossem.oms.svc.service;

import com.inossem.oms.base.svc.domain.CoaList;
import com.inossem.oms.base.svc.mapper.CoaListMapper;
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
public class CoaListService
{
    @Resource
    private CoaListMapper coaListMapper;

    /**
     * 查询system connect info
     * 
     * @param id system connect info主键
     * @return system connect info
     */
    public CoaList selectCoaListById(Long id)
    {
        return coaListMapper.selectCoaListById(id);
    }

    /**
     * 查询system connect info列表
     * 
     * @param coaList system connect info
     * @return system connect info
     */
    public List<CoaList> selectCoaListList(CoaList coaList)
    {
        return coaListMapper.selectCoaListList(coaList);
    }

    /**
     * 新增system connect info
     * 
     * @param coaList system connect info
     * @return 结果
     */
    public int insertCoaList(CoaList coaList)
    {
        return coaListMapper.insertCoaList(coaList);
    }

    /**
     * 修改system connect info
     * 
     * @param coaList system connect info
     * @return 结果
     */
    public int updateCoaList(CoaList coaList)
    {
        return coaListMapper.updateCoaList(coaList);
    }

    /**
     * 批量删除system connect info
     * 
     * @param ids 需要删除的system connect info主键
     * @return 结果
     */
    public int deleteCoaListByIds(Long[] ids)
    {
        return coaListMapper.deleteCoaListByIds(ids);
    }

    /**
     * 删除system connect info信息
     * 
     * @param id system connect info主键
     * @return 结果
     */
    public int deleteCoaListById(Long id)
    {
        return coaListMapper.deleteCoaListById(id);
    }
}
