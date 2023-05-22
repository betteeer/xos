package com.inossem.oms.base.svc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inossem.oms.base.svc.domain.BkCoaRel;

import java.util.List;

/**
 * @author shigf
 * @date 2022/12/8
 **/
public interface BkCoaRelMapper extends BaseMapper<BkCoaRel> {
    /**
     * 查询FI COA mapping
     *
     * @param id FI COA mapping主键
     * @return FI COA mapping
     */
    public BkCoaRel selectBkCoaRelById(Long id);

    /**
     * 查询FI COA mapping列表
     *
     * @param bkCoaRel FI COA mapping
     * @return FI COA mapping集合
     */
    public List<BkCoaRel> selectBkCoaRelList(BkCoaRel bkCoaRel);

    /**
     * 新增FI COA mapping
     *
     * @param bkCoaRel FI COA mapping
     * @return 结果
     */
    public int insertBkCoaRel(BkCoaRel bkCoaRel);

    /**
     * 修改FI COA mapping
     *
     * @param bkCoaRel FI COA mapping
     * @return 结果
     */
    public int updateBkCoaRel(BkCoaRel bkCoaRel);

    /**
     * 删除FI COA mapping
     *
     * @param id FI COA mapping主键
     * @return 结果
     */
    public int deleteBkCoaRelById(Long id);

    /**
     * 批量删除FI COA mapping
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBkCoaRelByIds(Long[] ids);
}
