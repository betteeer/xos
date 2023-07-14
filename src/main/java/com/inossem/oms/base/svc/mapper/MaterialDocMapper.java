package com.inossem.oms.base.svc.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.inossem.oms.base.svc.domain.MaterialDoc;
import com.inossem.oms.base.svc.vo.QueryMaterialDocListVo;
import com.inossem.oms.base.svc.vo.QueryMaterialDocResVo;

import java.util.List;

/**
 * 物料凭证Mapper接口
 * 
 * @author shigf
 * @date 2022-10-11
 */
public interface MaterialDocMapper extends MPJBaseMapper<MaterialDoc> {

    List<QueryMaterialDocResVo> selectListByQueryParam(QueryMaterialDocListVo queryMaterialDocListVo);

    Long getMaxNumber(String companyCode);
}
