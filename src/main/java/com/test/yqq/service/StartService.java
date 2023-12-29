package com.test.yqq.service;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import cn.hutool.bloomfilter.BloomFilter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.test.yqq.domain.PhoneNumber;
import com.test.yqq.mapper.TestMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;


@Component
public class StartService {
//    private static final long MAX_SIZE = 999_0000_0000;
    BloomFilter filter = new BitMapBloomFilter(20);
    @Resource
    private TestMapper testMapper;

    @PostConstruct
    public void init() throws InterruptedException {
        LambdaQueryWrapper<PhoneNumber> queryWrapper = new LambdaQueryWrapper<>();
        int batchSize = 100_0000;
        for (int page = 1;;page++) {
            Page<PhoneNumber> p = Page.of(page, batchSize);
            System.out.println(page);
            Page<PhoneNumber> phoneNumberPage = testMapper.selectPage(p, queryWrapper);
            List<PhoneNumber> list = phoneNumberPage.getRecords();
            list.forEach(i ->{
                filter.add(i.getNumber());
            });
            if (list.size() < batchSize) {
                break;
            }
        }
    }

    public boolean isExist(String number) {
        return filter.contains(number);
    }
}
