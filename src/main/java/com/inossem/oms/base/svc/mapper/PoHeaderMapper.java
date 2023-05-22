package com.inossem.oms.base.svc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inossem.oms.base.svc.domain.PoHeader;
import com.inossem.oms.base.svc.domain.VO.PoListVo;
import com.inossem.oms.base.svc.domain.VO.PoOrderHeaderResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author shigf
 * @date 2022-11-04
 */
public interface PoHeaderMapper  extends BaseMapper<PoHeader> {
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    PoHeader selectPoHeaderById(Long id);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param po 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    List<PoHeader> selectPoHeaderList(PoListVo po);

    /**
     * 新增【请填写功能名称】
     *
     * @param poHeader 【请填写功能名称】
     * @return 结果
     */
    int insertPoHeader(PoHeader poHeader);

    /**
     * 修改【请填写功能名称】
     *
     * @param poHeader 【请填写功能名称】
     * @return 结果
     */
    int updatePoHeader(PoHeader poHeader);

    /**
     * 删除【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    int deletePoHeaderById(Long id);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deletePoHeaderByIds(Long[] ids);

    /**
     * 根据公司代码和number查询po信息
     *
     * @param companyCode
     * @param poNumber
     * @return
     */
    PoHeader selectPoHeaderByCompanyAndNumber(@Param("companyCode") String companyCode,
                                              @Param("poNumber") String poNumber);

    /**
     * 查询无发运记录deliveryHeader
     * @param poNumber
     * @param companyCode
     * @return
     */
    PoOrderHeaderResp getOrderHeader(@Param("poNumber") String poNumber, @Param("companyCode") String companyCode);

    /**
     * 查询无发运记录deliveryHeader
     * @param poNumber
     * @param companyCode
     * @return
     */
    PoOrderHeaderResp getOrderHeaders(@Param("poNumber") String poNumber, @Param("companyCode") String companyCode);

    /**
     * wareHouseCode 查询po_item中是否占用该仓库的单子  有  则查询po_header  不为完全发运则 不允许修改
     * @param wareHouseCode
     * @param companyCode
     * @return
     */
    List<PoHeader> checkWareHoseCode(@Param("wareHouseCode")String wareHouseCode, @Param("companyCode") String companyCode);

    List<PoHeader> checkSku(@Param("skuCode") String skuCode, @Param("companyCode") String companyCode);

}
