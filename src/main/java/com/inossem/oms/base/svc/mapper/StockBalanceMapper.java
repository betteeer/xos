package com.inossem.oms.base.svc.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.inossem.oms.base.svc.domain.StockBalance;
import com.inossem.oms.base.svc.vo.QueryStockBalanceResVo;
import com.inossem.oms.base.svc.vo.QueryStockBySkuVo;
import com.inossem.oms.base.svc.vo.QueryStockListVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author shigf
 * @date 2022-10-11
 */
public interface StockBalanceMapper  extends MPJBaseMapper<StockBalance>
{
    List<QueryStockBalanceResVo> selectListByQueryParam(QueryStockListVo queryStockListVo);

    BigDecimal selectSkuTotalQty(@Param("skuNumber") String skuNumber,@Param("companyCode") String companyCode);

    QueryStockBalanceResVo selectStockBySkuAndCompany(QueryStockBySkuVo queryStockListVo);
}
