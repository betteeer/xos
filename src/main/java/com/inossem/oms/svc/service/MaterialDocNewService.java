package com.inossem.oms.svc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.inossem.oms.base.common.constant.ModuleConstant;
import com.inossem.oms.base.svc.domain.MaterialDoc;
import com.inossem.oms.base.svc.domain.MovementType;
import com.inossem.oms.base.svc.domain.SkuMaster;
import com.inossem.oms.base.svc.domain.VO.PreMaterialDocVo;
import com.inossem.oms.base.svc.domain.Warehouse;
import com.inossem.oms.base.svc.mapper.MaterialDocMapper;
import com.inossem.oms.base.svc.mapper.MovementTypeMapper;
import com.inossem.oms.base.svc.vo.QueryMaterialDocListVo;
import com.inossem.oms.base.svc.vo.QueryMaterialDocResVo;
import com.inossem.oms.base.utils.NumberWorker;
import com.inossem.sco.common.core.exception.ServiceException;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional(rollbackFor = Exception.class)
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
//        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
//        Date date = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        for (int i = 0; i < preMaterialDocVos.size(); i++) {
            PreMaterialDocVo p = preMaterialDocVos.get(i);
            MaterialDoc m = new MaterialDoc();
            BeanUtils.copyProperties(p, m);
            m.setDocNumber(String.valueOf(docNumber));
            m.setDocItem(String.valueOf(i+1));
            m.setPostingDate(p.getPostingDate());
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

    public List<MaterialDoc> reverseShipoutMaterialDoc(String shipoutMaterialDoc) {
        // 获取正向的material doc
        List<MaterialDoc> materialDocs = this.getMaterialDocs(shipoutMaterialDoc);
        String movementType = materialDocs.get(0).getMovementType();
        // 获取反向的movementType
        MovementType reverseMovementType = this.getReverseMovementType(movementType);
        long docNumber = numberWorker.generateId(materialDocs.get(0).getCompanyCode(), ModuleConstant.ORDER_NUMBER_TYPE.MATERIAL_DOC);
        // 构建reverse的material doc
        List<MaterialDoc> reverseResult = new ArrayList<>();
        List<MaterialDoc> result = new ArrayList<>();
        for (MaterialDoc m : materialDocs) {
            MaterialDoc rm = new MaterialDoc();
            BeanUtils.copyProperties(m, rm);
            rm.setId(null);
            rm.setDocNumber(String.valueOf(docNumber));
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            Date date = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
            rm.setPostingDate(date);
            rm.setMovementType(reverseMovementType.getMovementType());
            rm.setWarehouseCode(m.getToWarehouseCode());
            rm.setToWarehouseCode(m.getWarehouseCode());
            rm.setInOut(reverseMovementType.getInOut());
            rm.setStockStatus("A");
            rm.setReferenceNumber(m.getDocNumber());
            rm.setReferenceItem(m.getDocItem());
            rm.setIsReversed(0);
            rm.setGmtCreate(new Date());
            rm.setGmtModified(new Date());
            // 把原始的doc reverse掉
            m.setIsReversed(1);
            m.setGmtModified(new Date());
            reverseResult.add(rm);
            result.add(m);
        }
        result.addAll(reverseResult);
        saveOrUpdateBatch(result);
        return reverseResult;
    }

    public List<MaterialDoc> getMaterialDocs(String docNumber) {
        List<MaterialDoc> docs = materialDocMapper.selectList(Wrappers.lambdaQuery(MaterialDoc.class).eq(MaterialDoc::getDocNumber, docNumber));
        if (StringUtils.isEmpty(docs)) {
            throw new ServiceException("DocNumber: " + docNumber + " does not have material docs");
        }
        return docs;
    }
    public MovementType getReverseMovementType(String movementType) {
        //查正向的movementType
        MovementType mt = movementTypeMapper.selectOne(Wrappers.lambdaQuery(MovementType.class).eq(MovementType::getMovementType, movementType));
        if (StringUtils.isNull(mt)) {
            throw new ServiceException("MovementType: " + movementType + " no config");
        }
        String reverseType = mt.getReverseMovType();
        MovementType rmt = movementTypeMapper.selectOne(Wrappers.lambdaQuery(MovementType.class).eq(MovementType::getMovementType, reverseType));
        if (StringUtils.isNull(rmt)) {
            throw new ServiceException("Reverse Movement Type: " + movementType + " no config");
        }
        return rmt;
    }

    public List<MaterialDoc> reverseReceiveMaterialDoc(String receiveMaterialDoc) {
        // 获取正向的material doc
        List<MaterialDoc> materialDocs = this.getMaterialDocs(receiveMaterialDoc);
        String movementType = materialDocs.get(0).getMovementType();
        // 获取反向的movementType
        MovementType reverseMovementType = this.getReverseMovementType(movementType);
        long docNumber = numberWorker.generateId(materialDocs.get(0).getCompanyCode(), ModuleConstant.ORDER_NUMBER_TYPE.MATERIAL_DOC);
        // 构建reverse的material doc
        List<MaterialDoc> reverseResult = new ArrayList<>();
        List<MaterialDoc> result = new ArrayList<>();
        for (MaterialDoc m : materialDocs) {
            MaterialDoc rm = new MaterialDoc();
            BeanUtils.copyProperties(m, rm);
            rm.setId(null);
            rm.setDocNumber(String.valueOf(docNumber));
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            Date date = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
            rm.setPostingDate(date);
            rm.setMovementType(reverseMovementType.getMovementType());
            rm.setWarehouseCode(m.getWarehouseCode());
            rm.setToWarehouseCode("");
            rm.setInOut(reverseMovementType.getInOut());
            rm.setStockStatus("T");
            rm.setReferenceNumber(m.getDocNumber());
            rm.setReferenceItem(m.getDocItem());
            rm.setIsReversed(0);
            rm.setGmtCreate(new Date());
            rm.setGmtModified(new Date());
            // 把原始的doc reverse掉
            m.setIsReversed(1);
            m.setGmtModified(new Date());
            reverseResult.add(rm);
            result.add(m);
        }
        result.addAll(reverseResult);
        saveOrUpdateBatch(result);
        return reverseResult;
    }

    public List<QueryMaterialDocResVo> getDocList(QueryMaterialDocListVo queryVo) {
        log.info("开始查询物料凭证,传入参数:[{}]", queryVo);
        MPJLambdaWrapper<MaterialDoc> wrapper = new MPJLambdaWrapper<MaterialDoc>()
                .selectAll(MaterialDoc.class)
                .leftJoin(SkuMaster.class, SkuMaster::getSkuNumber, MaterialDoc::getSkuNumber,
                        ext -> ext.selectAs(SkuMaster::getSkuName, QueryMaterialDocResVo::getSkuName))
                .leftJoin(MovementType.class, MovementType::getMovementType, MaterialDoc::getMovementType,
                        ext -> ext.selectAs(MovementType::getMovementDescription, QueryMaterialDocResVo::getTransactionType))
                .leftJoin(Warehouse.class, Warehouse::getWarehouseCode, MaterialDoc::getWarehouseCode,
                        ext -> ext.selectAs(Warehouse::getName, QueryMaterialDocResVo::getWarehouseName))
                .eq(MaterialDoc::getCompanyCode, queryVo.getCompanyCode())
                .eq(StringUtils.isNotEmpty(queryVo.getWarehouseCode()), MaterialDoc::getWarehouseCode, queryVo.getWarehouseCode())
                .between(StringUtils.isNotEmpty(queryVo.getStartPostingDate()), MaterialDoc::getPostingDate, queryVo.getStartPostingDate(),queryVo.getEndPostingDate())
                .nested(StringUtils.isNotEmpty(queryVo.getSearchText()), i -> {
                    i.like(MaterialDoc::getSkuNumber, queryVo.getSearchText()).or().like(MaterialDoc::getReferenceNumber, queryVo.getSearchText());
                })
                .orderByDesc(MaterialDoc::getGmtCreate);
        List<QueryMaterialDocResVo> queryMaterialDocResVos = materialDocMapper.selectJoinList(QueryMaterialDocResVo.class, wrapper);
        return queryMaterialDocResVos;
    }
}
