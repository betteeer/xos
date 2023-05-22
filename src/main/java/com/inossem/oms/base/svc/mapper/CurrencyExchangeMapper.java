package com.inossem.oms.base.svc.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inossem.oms.base.svc.domain.CurrencyExchange;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CurrencyExchangeMapper extends BaseMapper<CurrencyExchange> {

    int insertBatch(@Param("list") List<CurrencyExchange> list);

    @InterceptorIgnore(blockAttack = "true")
    @Delete("DELETE FROM currency_exchange")
    int deleteAll();

}
