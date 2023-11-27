package com.inossem.oms.mdm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inossem.oms.base.svc.domain.SkuGroup;
import com.inossem.oms.base.svc.domain.SkuMaster;
import com.inossem.oms.base.svc.mapper.SkuGroupMapper;
import com.inossem.oms.base.svc.mapper.SkuMasterMapper;
import com.inossem.sco.common.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

interface ISkuGroupsService extends IService<SkuGroup> {
}

@Service
@Slf4j
public class SkuGroupService extends ServiceImpl<SkuGroupMapper, SkuGroup> implements ISkuGroupsService {

    @Resource
    private SkuGroupMapper skuGroupMapper;

    @Resource
    private SkuMasterMapper skuMasterMapper;

    public List<SkuGroup> getList(String companyCode, Boolean onlyEnabled) {
        LambdaQueryWrapper<SkuGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuGroup::getCompanyCode, companyCode);
        wrapper.eq(onlyEnabled, SkuGroup::getIsDeleted, 0);
        return skuGroupMapper.selectList(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean batch(List<SkuGroup> addItems, List<SkuGroup> modifyItems, String companyCode, String userId) {
        log.info(">>>Sku group,新增入参:{}", addItems.toString());
        log.info(">>>Sku group,修改入参:{}", modifyItems.toString());
        addItems.forEach(a -> {
            a.setCompanyCode(companyCode);
            a.setIsDeleted(0);
            a.setGmtCreate(new Date());
            a.setCreateBy(userId);
            a.setModifiedBy(userId);
            a.setGmtModified(new Date());
        });
        modifyItems.forEach(m -> {
            m.setCompanyCode(companyCode);
            m.setGmtModified(new Date());
            m.setModifiedBy(userId);
        });
        List<SkuGroup> joint = new ArrayList<>();
        joint.addAll(addItems);
        joint.addAll(modifyItems);
        // 被disable的sku group不需要参与重名校验
        List<SkuGroup> collect = joint.stream().filter(v -> v.getIsDeleted() != 1).collect(Collectors.toList());
        if (hasSameInSelf(collect) || hasSameInDatabase(companyCode, addItems, modifyItems)) {
            throw new ServiceException("Duplicate enabled sku groups");
        }
        if (!modifyItems.isEmpty()) {
            // todo
            // 还需判断即将被禁用的sku group有没有sku在用
            List<SkuGroup> disabledGroups = modifyItems.stream().filter(v -> v.getIsDeleted() == 1).collect(Collectors.toList());
            if (!disabledGroups.isEmpty()) {
                if (checkHasUsingSku(disabledGroups, companyCode)) {
                    throw new ServiceException("sku group is in use");
                }
            }
            updateBatchById(modifyItems);
        }
        if (!addItems.isEmpty()) {
            saveBatch(addItems);
        }
        return true;
    }

    private boolean checkHasUsingSku(List<SkuGroup> disabledGroups, String companyCode) {
        List<Long> ids = disabledGroups.stream().map(SkuGroup::getId).collect(Collectors.toList());
        LambdaQueryWrapper<SkuMaster> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuMaster::getCompanyCode, companyCode)
                .eq(SkuMaster::getIsDeleted, 0)
                .select(SkuMaster::getId)
                .in(SkuMaster::getSkuGroupId, ids)
                .last("LIMIT 1");
        SkuMaster skuMaster = skuMasterMapper.selectOne(wrapper);
        if (null == skuMaster) {
            return false;
        }
        return true;
    }

    /**
     * 判断自身里面有没有重名
     *
     * @param skuGroups
     * @return
     */
    public boolean hasSameInSelf(List<SkuGroup> skuGroups) {
        return skuGroups.stream().map(SkuGroup::getSkuGroupCode).distinct().count() != skuGroups.size();
    }

    /**
     * 判断与数据库中是否存在同名
     *
     * @param companyCode
     * @param skuGroups
     * @return
     */
    public boolean hasSameInDatabase(String companyCode, List<SkuGroup> addItems, List<SkuGroup> modifyItems) {
        LambdaQueryWrapper<SkuGroup> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SkuGroup::getCompanyCode, companyCode);
        queryWrapper.eq(SkuGroup::getIsDeleted, 0);
        List<SkuGroup> groups = skuGroupMapper.selectList(queryWrapper);
        List<String> names = groups.stream().map(SkuGroup::getSkuGroupCode).collect(Collectors.toList());
        // 判断 新增的 有没有与原来的重名
        Optional<SkuGroup> first = addItems.stream().filter(s -> names.contains(s.getSkuGroupCode())).findFirst();
        if (first.isPresent()) return true;
        // 再判断修改的 有没有重名的
        List<Long> ids = groups.stream().map(SkuGroup::getId).collect(Collectors.toList());
        for (SkuGroup modifyItem : modifyItems) {
            if (ids.contains(modifyItem.getId())) {
                groups.forEach(v -> {
                    if (v.getId().equals(modifyItem.getId())) {
                        v.setSkuGroupCode(modifyItem.getSkuGroupCode());
                    }
                });
            } else {
                groups.add(modifyItem);
            }
        }
        return hasSameInSelf(groups);
    }
}
