package com.inossem.oms.api.oms.api.remote;

import com.inossem.oms.api.oms.api.factory.RemoteBpFallbackFactory;
import com.inossem.oms.base.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Bp服务
 * 
 * @author shigf
 */
@FeignClient(contextId = "remoteBpService", value = ServiceNameConstants.BP_SERVICE, fallbackFactory = RemoteBpFallbackFactory.class)
public interface RemoteBpService
{


    /**
     * 注册用户信息
     *
     * @param sysUser 用户信息
     * @param source 请求来源
     * @return 结果
     */
//    @PostMapping("/user/register")
//    public R<Boolean> registerUserInfo(@RequestBody SysUser sysUser, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
