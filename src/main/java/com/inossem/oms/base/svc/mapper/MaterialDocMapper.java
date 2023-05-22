package com.inossem.oms.base.svc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
public interface MaterialDocMapper extends BaseMapper<MaterialDoc> {

    List<QueryMaterialDocResVo> selectListByQueryParam(QueryMaterialDocListVo queryMaterialDocListVo);

    Long getMaxNumber(String companyCode);
}
