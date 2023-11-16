package com.inossem.oms.svc.service;

import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.inossem.oms.api.bk.api.BookKeepingService;
import com.inossem.oms.api.bk.model.UserModel;
import com.inossem.oms.base.svc.domain.SoHeader;
import com.inossem.oms.base.svc.mapper.SoHeaderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    @Resource
    private BookKeepingService bkService;

    @Resource
    private SoHeaderMapper soHeaderMapper;
    public List<UserModel> getSoUsers(String companyCode) throws IOException {
        MPJLambdaWrapper<SoHeader> wrapper = JoinWrappers.lambda(SoHeader.class)
            .select("distinct create_by").eq(SoHeader::getCompanyCode, companyCode);
        MPJLambdaWrapper<SoHeader> wrapper1 = JoinWrappers.lambda(SoHeader.class)
                .select("distinct modified_by as create_by").eq(SoHeader::getCompanyCode, companyCode);
        wrapper.union(wrapper1);
        List<String> list = wrapper.list().stream().map(SoHeader::getCreateBy).collect(Collectors.toList());
        return bkService.getUserList(companyCode, list);
    }
}
