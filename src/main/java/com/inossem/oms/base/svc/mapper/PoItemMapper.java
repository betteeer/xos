package com.inossem.oms.base.svc.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.inossem.oms.base.svc.domain.PoItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author shigf
 * @date 2022-11-04
 */
public interface PoItemMapper  extends MPJBaseMapper<PoItem> {
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    PoItem selectPoItemById(Long id);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param poItem 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    List<PoItem> selectPoItemList(PoItem poItem);

    /**
     * 新增【请填写功能名称】
     *
     * @param poItem 【请填写功能名称】
     * @return 结果
     */
    int insertPoItem(PoItem poItem);

    /**
     * 修改【请填写功能名称】
     *
     * @param poItem 【请填写功能名称】
     * @return 结果
     */
    int updatePoItem(PoItem poItem);

    /**
     * 删除【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    int deletePoItemById(Long id);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deletePoItemByIds(Long[] ids);

    /**
     * 查询指定Po_header的item
     *
     * @param companyCode
     * @param poNumber
     * @return
     */
    List<PoItem> selectPoItemByPoNumber(
      @Param("companyCode") String companyCode,
      @Param("poNumber") String poNumber);

    int insertBatch(@Param("list")List<PoItem> list);
}
