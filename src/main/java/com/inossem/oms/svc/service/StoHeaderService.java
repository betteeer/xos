package com.inossem.oms.svc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.inossem.oms.base.svc.domain.DTO.StoFormDTO;
import com.inossem.oms.base.svc.domain.DTO.StoSearchFormDTO;
import com.inossem.oms.base.svc.domain.StoHeader;
import com.inossem.oms.base.svc.domain.StoItem;
import com.inossem.oms.base.svc.domain.Warehouse;
import com.inossem.oms.base.svc.enums.StoStatus;
import com.inossem.oms.base.svc.mapper.StoHeaderMapper;
import com.inossem.oms.base.svc.mapper.StoItemMapper;
import com.inossem.sco.common.core.exception.ServiceException;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class StoHeaderService {
    @Resource
    private StoHeaderMapper stoHeaderMapper;

    @Resource
    private StoItemService stoItemService;

    public List<StoHeader> getList(StoSearchFormDTO form) {
        log.info(">>>查询sto列表，入参：[{}]", form);
        MPJLambdaWrapper<StoHeader> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(StoHeader.class)
                // 关联warehouse表，查询fromWarehouseName和toWarehouseName
                .leftJoin(Warehouse.class, Warehouse::getWarehouseCode, StoHeader::getFromWarehouseCode, ext -> ext.selectAs(Warehouse::getName,StoHeader::getFromWarehouseName))
                .leftJoin(Warehouse.class, Warehouse::getWarehouseCode, StoHeader::getToWarehouseCode, ext -> ext.selectAs(Warehouse::getName,StoHeader::getToWarehouseName));
        // 指定 company code数据范围
        wrapper.eq(StoHeader::getCompanyCode, form.getCompanyCode());
        // 拼接 status
        wrapper.in(StringUtils.isNotEmpty(form.getStatus()),StoHeader::getOrderStatus, form.getStatus());
        // 拼接 warehouse
        wrapper.in(StringUtils.isNotEmpty(form.getFromWarehouse()), StoHeader::getFromWarehouseCode, form.getFromWarehouse());
        wrapper.in(StringUtils.isNotEmpty(form.getToWarehouse()), StoHeader::getToWarehouseCode, form.getToWarehouse());
        // 拼接create date 查询
        wrapper.between(StringUtils.isNotNull(form.getCreateDateStart()), StoHeader::getCreateDate, form.getCreateDateStart(), form.getCreateDateEnd());
        // 拼接shipout date 查询
        wrapper.between(StringUtils.isNotNull(form.getShipoutDateStart()), StoHeader::getShipoutDate, form.getShipoutDateStart(), form.getShipoutDateEnd());
        // 拼接receive date 查询
        wrapper.between(StringUtils.isNotNull(form.getReceiveDateStart()), StoHeader::getReceiveDate, form.getReceiveDateStart(), form.getReceiveDateEnd());
        // 拼接search
        wrapper.nested(StringUtils.isNotEmpty(form.getSearchText()), i -> {
            i.like(StoHeader::getStoNumber, form.getSearchText()).or().like(StoHeader::getTrackingNumber, form.getSearchText());
        });
        // 拼接order by
        wrapper.orderBy(StringUtils.isNotNull(form.getOrderBy()), form.getIsAsc(), StringUtils.toUnderScoreCase(form.getOrderBy()));
        List<StoHeader> stoHeaders = stoHeaderMapper.selectJoinList(StoHeader.class, wrapper);
        return stoHeaders;
    }

    @Transactional(rollbackFor = Exception.class)
    public StoHeader saveOrder(StoFormDTO stoFormDTO) {
        Long id = stoFormDTO.getId();
        log.info("开始处理StoHeaderId为" + id + "的数据：[{}]", stoFormDTO);
        // 新增
        if (id == null) {
            StoHeader stoHeader = this.packingStoHeaderInfoWhenSave(stoFormDTO);
            log.info("构建的stoheader： [{}]", stoHeader);
            List<StoItem> items = this.packingStoItemInfoWhenSave(stoFormDTO, stoHeader.getStoNumber());
            log.info("构建的stoitems： [{}]", items);
            stoHeaderMapper.insert(stoHeader);
            stoItemService.saveBatch(items);
            stoHeader.setItems(items);
            return stoHeader;
        } else { // 之前已经save过了，重新save算做修改
            LambdaQueryWrapper<StoHeader> wrapper = new LambdaQueryWrapper<StoHeader>()
                    .eq(StoHeader::getId, id);
            StoHeader stoHeader = stoHeaderMapper.selectOne(wrapper);
            if (StringUtils.isEmpty(stoHeader.getOrderStatus())) {
                throw new ServiceException("only open order can be saved");
            }
            stoHeader = this.packingStoHeaderInfoWhenReSave(stoFormDTO, stoHeader);
            log.info("构建的stoheader： [{}]", stoHeader);
            stoHeaderMapper.updateById(stoHeader);
            // 更新items
            List<StoItem> stoItems = this.packingStoItemInfoWhenReSave(stoFormDTO, stoHeader.getStoNumber());
            log.info("构建的stoitems： [{}]", stoItems);
            stoItemService.saveOrUpdateBatch(stoItems);
            stoHeader.setItems(stoItems);
            return stoHeader;
        }
    }

    /**
     * 第一次保存时 组装sto header
     *
     * @param stoFormDTO
     * @return
     */
    private StoHeader packingStoHeaderInfoWhenSave(StoFormDTO stoFormDTO) {
        StoHeader stoHeader = new StoHeader();
        stoHeader.setCompanyCode(stoFormDTO.getCompanyCode());
        stoHeader.setOrderType("wsto");
        stoHeader.setStoNumber(this.getMaxNumber(stoFormDTO.getCompanyCode()));
        stoHeader.setFromWarehouseCode(stoFormDTO.getFromWarehouseCode());
        stoHeader.setToWarehouseCode(stoFormDTO.getToWarehouseCode());
        stoHeader.setReferenceNumber(stoFormDTO.getReferenceNumber());
        stoHeader.setOrderStatus(StoStatus.OPEN.getStatus());
        stoHeader.setCreateDate(new Date());
        stoHeader.setGmtCreate(new Date());
        stoHeader.setGmtModified(new Date());
        stoHeader.setStoNotes(stoFormDTO.getStoNotes());
        return stoHeader;
    }

    @Transactional(rollbackFor = Exception.class)
    String getMaxNumber(String companyCode) {
        LambdaQueryWrapper<StoHeader> wrapper = new LambdaQueryWrapper<StoHeader>()
                .eq(StoHeader::getCompanyCode, companyCode)
                .orderByDesc(StoHeader::getId)
                .last("limit 1");
        StoHeader stoHeader = stoHeaderMapper.selectOne(wrapper);
        String no = "80000001";
        if (stoHeader != null) {
            no = BigDecimal.ONE.add(new BigDecimal(stoHeader.getStoNumber())).toString();
        }
        return no;
    }

    /**
     * 第一次保存时 组装sto items
     *
     * @param stoFormDTO
     * @param stoNumber
     * @return
     */
    private List<StoItem> packingStoItemInfoWhenSave(StoFormDTO stoFormDTO, String stoNumber) {
        List<StoItem> items = stoFormDTO.getItems();
        List<StoItem> result = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            StoItem si = new StoItem();
            si.setCompanyCode(stoFormDTO.getCompanyCode());
            si.setStoNumber(stoNumber);
            si.setStoItem(String.valueOf(i + 1));
            si.setSkuNumber(items.get(i).getSkuNumber());
            si.setFromWarehouseCode(stoFormDTO.getFromWarehouseCode());
            si.setToWarehouseCode(stoFormDTO.getToWarehouseCode());
            si.setBasicTransferQty(items.get(i).getBasicTransferQty());
            si.setBasicUom(items.get(i).getBasicUom());
            si.setGmtCreate(new Date());
            si.setGmtModified(new Date());
            result.add(si);
        }
        return result;
    }

    /**
     * 二次保存，根据用户传入的表单和数据库中查询出的数据组装新的sto header
     *
     * @param stoFormDTO
     * @param stoHeader
     * @return
     */
    private StoHeader packingStoHeaderInfoWhenReSave(StoFormDTO stoFormDTO, StoHeader stoHeader) {
        stoHeader.setFromWarehouseCode(stoFormDTO.getFromWarehouseCode());
        stoHeader.setToWarehouseCode(stoHeader.getToWarehouseCode());
        stoHeader.setReferenceNumber(stoHeader.getReferenceNumber());
        stoHeader.setGmtModified(new Date());
        return stoHeader;
    }

    /**
     * 二次保存，组装新的sto items
     *
     * @param stoFormDTO
     * @param stoItems
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    List<StoItem> packingStoItemInfoWhenReSave(StoFormDTO stoFormDTO, String stoNumber) {
        StoItemMapper mapper = stoItemService.getBaseMapper();
        // 老的一堆items
        List<StoItem> originStoItems = mapper.selectList(new LambdaQueryWrapper<StoItem>().eq(StoItem::getStoNumber, stoNumber));
        // 新传入的一堆items
        List<StoItem> items = stoFormDTO.getItems();
        List<StoItem> result = new ArrayList<>();
        Integer index = originStoItems.size();
        for (StoItem item : items) {
            if (item.getId() == null) {
                StoItem si = new StoItem();
                si.setCompanyCode(stoFormDTO.getCompanyCode());
                si.setStoNumber(stoNumber);
                // 新的sto item 从老的基础上开始加
                si.setStoItem(String.valueOf(++index));
                si.setSkuNumber(item.getSkuNumber());
                si.setFromWarehouseCode(stoFormDTO.getFromWarehouseCode());
                si.setToWarehouseCode(stoFormDTO.getToWarehouseCode());
                si.setBasicTransferQty(item.getBasicTransferQty());
                si.setBasicUom(item.getBasicUom());
                si.setGmtCreate(new Date());
                si.setGmtModified(new Date());
                if (item.getIsDeleted() == 1) {
                    si.setIsDeleted(1);
                }
                result.add(si);
            } else {
                originStoItems.stream().filter(v -> v.getId().equals(item.getId())).findFirst().ifPresent(i -> {
                    boolean same = i.getFromWarehouseCode().equals(stoFormDTO.getFromWarehouseCode()) && i.getToWarehouseCode().equals(stoFormDTO.getToWarehouseCode());
                    // 切换了from/to 导致老的item与外部的仓库不匹配，直接设为删除态
                    if (!same) {
                        i.setIsDeleted(1);
                    } else {
                        // 更改了老item的一些字段值
                        i.setBasicTransferQty(item.getBasicTransferQty());
                        i.setBasicUom(item.getBasicUom());
                        i.setIsDeleted(item.getIsDeleted());
                    }
                    i.setGmtModified(new Date());
                    result.add(i);
                });
            }
        }
        return result;
    }

    public StoHeader getDetail(String stoNumber, String companyCode) {
        MPJLambdaWrapper<StoHeader> wrapper = new MPJLambdaWrapper<StoHeader>()
                .eq(StoHeader::getStoNumber, stoNumber)
                .eq(StoHeader::getCompanyCode, companyCode)
                .selectCollection(StoItem.class, StoHeader::getItems)
                .leftJoin(StoItem.class, StoItem::getStoNumber, StoHeader::getStoNumber);
        StoHeader stoHeader = stoHeaderMapper.selectJoinOne(StoHeader.class, wrapper);
        return stoHeader;
    }
}
