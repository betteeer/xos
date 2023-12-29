package com.test.yqq.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.test.yqq.domain.PhoneNumber;
import com.test.yqq.mapper.TestMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TestService {
    @Resource
    private TestMapper testMapper;

    @Resource
    private StartService startService;

    public List<PhoneNumber> getList() {
        LambdaQueryWrapper<PhoneNumber> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PhoneNumber::getId, 1);
        return testMapper.selectList(queryWrapper);
    }

    public boolean isExist(String number) {
        return startService.isExist(number);
    }
}
