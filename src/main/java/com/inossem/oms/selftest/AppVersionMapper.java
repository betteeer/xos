package com.inossem.oms.selftest;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@DS("tms")
public interface AppVersionMapper extends BaseMapper<AppVersion> {
}
