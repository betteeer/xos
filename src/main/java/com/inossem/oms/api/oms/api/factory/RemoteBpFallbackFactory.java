package com.inossem.oms.api.oms.api.factory;

import com.inossem.oms.api.oms.api.remote.RemoteBpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 用户服务降级处理
 * 
 * @author shigf
 */
@Component
public class RemoteBpFallbackFactory implements FallbackFactory<RemoteBpService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteBpFallbackFactory.class);

    @Override
    public RemoteBpService create(Throwable throwable)
    {
        log.error("Bp服务调用失败:{}", throwable.getMessage());
        return new RemoteBpService()
        {
//            @Override
//            public R<LoginUser> getUserInfo(String username, String source)
//            {
//                return R.fail("获取用户失败:" + throwable.getMessage());
//            }
//
//            @Override
//            public R<Boolean> registerUserInfo(SysUser sysUser, String source)
//            {
//                return R.fail("注册用户失败:" + throwable.getMessage());
//            }
        };
    }
}
