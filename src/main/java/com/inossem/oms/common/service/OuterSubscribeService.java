package com.inossem.oms.common.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inossem.oms.api.kyc.api.KycCommonService;
import com.inossem.oms.base.svc.domain.MaterialDoc;
import com.inossem.oms.base.svc.domain.SystemConnect;
import com.inossem.oms.base.svc.mapper.MaterialDocMapper;
import com.inossem.oms.svc.service.SystemConnectService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OuterSubscribeService {
    @Resource
    private MaterialDocMapper materialDocMapper;

    @Resource
    private SystemConnectService systemConnectService;
    @Resource
    private KycCommonService kycCommonService;

    public String getOmsSubscribeIndicator(String companyCode, String time) throws IOException {
        boolean existMaterialDoc = hasMaterialDoc(companyCode, time);
        if (existMaterialDoc) {
            return "A";
        } else {
            SystemConnect connect = new SystemConnect();
            connect.setCompanyCodeEx(companyCode);
            connect.setExSystem("bk");
            List<SystemConnect> connects = systemConnectService.selectSyctemConectList(connect);
            if (connects == null || connects.isEmpty()) {
                return "Z";
            }
            boolean hasSubscribe = hasSubscribeInKyc(companyCode);
            return hasSubscribe ? "Y" : "Z";
        }
    }

    private boolean hasSubscribeInKyc(String companyCode) throws IOException {
        JSONObject validSubscribe = kycCommonService.getValidSubscribe(companyCode);
        if (validSubscribe == null) return false;
        Set<String> subSets = validSubscribe.keySet();
        Optional<String> om = subSets.stream().filter(s -> s.contains("OM")).findFirst();
        return om.isPresent();
    }

    public boolean hasMaterialDoc(String companyCode, String time) {
        // 获取月份的第一天
        LocalDate firstDay = LocalDate.parse(time + "-01");
        // 获取月份的最后一天
        LocalDate lastDay = firstDay.with(TemporalAdjusters.lastDayOfMonth());
        // 构造第一秒和最后一秒
        LocalDateTime firstSecond = LocalDateTime.of(firstDay, LocalTime.MIN);
        LocalDateTime lastSecond = LocalDateTime.of(lastDay, LocalTime.MAX);
        // 格式化输出
        String firstTime = firstSecond.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String lastTime = lastSecond.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        LambdaQueryWrapper<MaterialDoc> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialDoc::getCompanyCode, companyCode);
        wrapper.between(MaterialDoc::getGmtCreate, firstTime, lastTime);
        wrapper.last("limit 1");
        MaterialDoc materialDoc = materialDocMapper.selectOne(wrapper);
        return materialDoc != null;
    }
}
