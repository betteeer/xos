package com.inossem.oms.svc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.inossem.oms.base.svc.domain.ConditionType;
import com.inossem.oms.base.svc.domain.MovementType;
import com.inossem.oms.base.svc.mapper.ConditionTypeMapper;
import com.inossem.oms.base.svc.mapper.MovementTypeMapper;
import com.inossem.oms.base.svc.vo.MovementAndOrderTypeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MovementTypeService {
    @Resource
    private MovementTypeMapper movementTypeMapper;

    @Resource
    private ConditionTypeMapper conditionTypeMapper;
    public List<MovementAndOrderTypeVo> getListByType(String type, String companyCode) {
        List<MovementAndOrderTypeVo> movementAndOrderTypeVos = new ArrayList<>();
        if (StringUtils.isEmpty(type) || "2".equals(type)) {
            LambdaQueryWrapper<MovementType> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(MovementType::getIsDeleted, 0);
            List<MovementType> movementTypes = movementTypeMapper.selectList(queryWrapper);
            List<MovementAndOrderTypeVo> movementTypeParts = movementTypes.stream().map(m ->
                            new MovementAndOrderTypeVo("2", m.getMovementType(), m.getMovementDescription(), companyCode))
                    .collect(Collectors.toList());
            movementAndOrderTypeVos.addAll(movementTypeParts);
        }
        if (StringUtils.isEmpty(type) || "3".equals(type)) {
            List<ConditionType> conditionTypes = conditionTypeMapper.selectList(null);
            List<MovementAndOrderTypeVo> conditionTypesParts = conditionTypes.stream().map(c ->
                            new MovementAndOrderTypeVo("3", c.getConditionType(), c.getConditionDescription(), companyCode))
                    .collect(Collectors.toList());
            movementAndOrderTypeVos.addAll(conditionTypesParts);
        }
        return movementAndOrderTypeVos;
    }
}
