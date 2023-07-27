package com.inossem.oms.svc.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inossem.oms.base.svc.domain.StoItem;
import com.inossem.oms.base.svc.mapper.StoItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

interface IStoItemService extends IService<StoItem> {
}

@Service
@Slf4j
public class StoItemService extends ServiceImpl<StoItemMapper, StoItem> implements IStoItemService {

}

