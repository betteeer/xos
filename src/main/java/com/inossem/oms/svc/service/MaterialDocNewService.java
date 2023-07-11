package com.inossem.oms.svc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inossem.oms.base.common.constant.ModuleConstant;
import com.inossem.oms.base.svc.domain.MaterialDoc;
import com.inossem.oms.base.svc.domain.MovementType;
import com.inossem.oms.base.svc.domain.VO.PreMaterialDocVo;
import com.inossem.oms.base.svc.mapper.MaterialDocMapper;
import com.inossem.oms.base.svc.mapper.MovementTypeMapper;
import com.inossem.oms.base.utils.NumberWorker;
import com.inossem.sco.common.core.exception.ServiceException;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

interface IMaterialDocService extends IService<MaterialDoc> {

}
@Service
@Slf4j
public class MaterialDocNewService extends ServiceImpl<MaterialDocMapper, MaterialDoc> implements IMaterialDocService {
    @Resource
    private MaterialDocMapper materialDocMapper;
    @Resource
    private NumberWorker numberWorker;
    @Resource
    private MovementTypeMapper movementTypeMapper;

    public List<MaterialDoc> generateMaterialDoc(String companyCode, List<PreMaterialDocVo> preMaterialDocVos) {
        if (StringUtils.isEmpty(preMaterialDocVos)) {
            throw new ServiceException("no item pass to generate material doc");
        }
        log.info(">>> 开始生成material doc");
        String type = preMaterialDocVos.get(0).getMovementType();
        LambdaQueryWrapper<MovementType> wrapper = new LambdaQueryWrapper<MovementType>().eq(MovementType::getMovementType, type);
        MovementType movementType = movementTypeMapper.selectOne(wrapper);
        if (StringUtils.isNull(movementType)) {
            throw new ServiceException("movement type does not exist");
        }
        long docNumber = numberWorker.generateId(companyCode, ModuleConstant.ORDER_NUMBER_TYPE.MATERIAL_DOC);
        List<MaterialDoc> res = new ArrayList<>();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        Date date = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        for (int i = 0; i < preMaterialDocVos.size(); i++) {
            PreMaterialDocVo p = preMaterialDocVos.get(i);
            MaterialDoc m = new MaterialDoc();
            BeanUtils.copyProperties(p, m);
            m.setDocNumber(String.valueOf(docNumber));
            m.setDocItem(String.valueOf(i+1));
            m.setPostingDate(date);
            m.setInOut(movementType.getInOut());
            m.setTotalAmount(p.getAveragePrice().multiply(p.getSkuQty()).setScale(2, BigDecimal.ROUND_HALF_UP));
            m.setIsReversed(0);
            m.setGmtCreate(new Date());
            m.setGmtModified(new Date());
            m.setIsDeleted(0);
            res.add(m);
        }
        log.info(">>> 生成的material doc对象为：{}", res);
        saveBatch(res);
        return res;
    }
}
