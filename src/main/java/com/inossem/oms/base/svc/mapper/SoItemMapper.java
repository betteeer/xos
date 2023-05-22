package com.inossem.oms.base.svc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inossem.oms.base.svc.domain.SoItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author shigf
 * @date 2022-10-17
 */
public interface SoItemMapper extends BaseMapper<SoItem> {

    int insertBatch(@Param("list")List<SoItem> list);

}
