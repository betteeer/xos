package com.inossem.oms.base.svc.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.inossem.oms.base.svc.domain.SoBillHeader;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 【开票】Mapper接口
 * 
 * @author guoh
 * @date 2022-11-20
 */
public interface SoBillHeaderMapper extends MPJBaseMapper<SoBillHeader>
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    SoBillHeader selectSoBillHeaderById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param soBillHeader 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    List<SoBillHeader> selectSoBillHeaderList(SoBillHeader soBillHeader);

    /**
     * 新增【请填写功能名称】
     * 
     * @param soBillHeader 【请填写功能名称】
     * @return 结果
     */
    int insertSoBillHeader(SoBillHeader soBillHeader);

    /**
     * 修改【请填写功能名称】
     * 
     * @param soBillHeader 【请填写功能名称】
     * @return 结果
     */
    int updateSoBillHeader(SoBillHeader soBillHeader);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    int deleteSoBillHeaderById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteSoBillHeaderByIds(Long[] ids);

    SoBillHeader selectBillHeaderInfo(@Param("billNumber") String billNumber, @Param("companyCode") String companyCode);
}
