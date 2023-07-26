package com.inossem.oms.svc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.inossem.oms.base.common.constant.ModuleConstant;
import com.inossem.oms.base.svc.domain.DTO.StoFormDTO;
import com.inossem.oms.base.svc.domain.DTO.StoSearchFormDTO;
import com.inossem.oms.base.svc.domain.*;
import com.inossem.oms.base.svc.domain.VO.PreMaterialDocVo;
import com.inossem.oms.base.svc.domain.VO.SimpleStockBalanceVo;
import com.inossem.oms.base.svc.enums.StoStatus;
import com.inossem.oms.base.svc.mapper.StoHeaderMapper;
import com.inossem.oms.base.svc.mapper.StoItemMapper;
import com.inossem.oms.utils.ChainSetValue;
import com.inossem.sco.common.core.exception.ServiceException;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StoHeaderService {
    @Resource
    private StoHeaderMapper stoHeaderMapper;

    @Resource
    private StoItemService stoItemService;

    @Resource
    private StockBalanceNewService stockBalanceNewService;

    @Resource
    private MaterialDocNewService materialDocNewService;

    @Transactional(rollbackFor = Exception.class)
    public StoHeader getStoHeaderByNumber(String stoNumber) {
        LambdaQueryWrapper<StoHeader> wrapper = Wrappers.lambdaQuery(StoHeader.class).eq(StoHeader::getStoNumber, stoNumber);
        StoHeader stoHeader = stoHeaderMapper.selectOne(wrapper);
        if (StringUtils.isNull(stoHeader)) {
            throw new ServiceException("sto number: " + stoNumber + " does not exist");
        }
        return stoHeader;
    }
    /**
     * 获取列表
     * @param form
     * @return
     */
    public List<StoHeader> getList(StoSearchFormDTO form) {
        log.info(">>>查询sto列表，入参：[{}]", form);
        MPJLambdaWrapper<StoHeader> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(StoHeader.class)
                // 关联warehouse表，查询fromWarehouseName和toWarehouseName
                .leftJoin(Warehouse.class, Warehouse::getWarehouseCode, StoHeader::getFromWarehouseCode, ext -> ext.selectAs(Warehouse::getName, StoHeader::getFromWarehouseName))
                .leftJoin(Warehouse.class, Warehouse::getWarehouseCode, StoHeader::getToWarehouseCode, ext -> ext.selectAs(Warehouse::getName, StoHeader::getToWarehouseName));
        // 指定 company code数据范围
        wrapper.eq(StoHeader::getCompanyCode, form.getCompanyCode());
        // 拼接 status
        wrapper.in(StringUtils.isNotEmpty(form.getStatus()), StoHeader::getOrderStatus, form.getStatus());
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

    /**
     * 获取单个详情
     *
     * @param stoNumber
     * @param companyCode
     * @return
     */
    public StoHeader getDetail(String stoNumber, String companyCode) {
        MPJLambdaWrapper<StoHeader> wrapper = new MPJLambdaWrapper<StoHeader>()
                .selectAll(StoHeader.class)
                .eq(StoHeader::getStoNumber, stoNumber)
                .eq(StoHeader::getCompanyCode, companyCode)
                .selectCollection(StoItem.class, StoHeader::getItems, ext ->
                                ext.association(SkuMaster.class, StoItem::getSkuName, c -> c.result(SkuMaster::getSkuName))
//                                .association(SkuMaster.class, StoItem::getIsKitting, c -> c.result(SkuMaster::getIsKitting))
                )
                .leftJoin(StoItem.class, StoItem::getStoNumber, StoHeader::getStoNumber)
                .leftJoin(Warehouse.class, Warehouse::getWarehouseCode, StoHeader::getFromWarehouseCode, ext -> ext.selectAs(Warehouse::getName, StoHeader::getFromWarehouseName))
                .leftJoin(Warehouse.class, Warehouse::getWarehouseCode, StoHeader::getToWarehouseCode, ext -> ext.selectAs(Warehouse::getName, StoHeader::getToWarehouseName))
                .leftJoin(SkuMaster.class, SkuMaster::getSkuNumber, StoItem::getSkuNumber);
        StoHeader stoHeader = stoHeaderMapper.selectJoinOne(StoHeader.class, wrapper);
        return stoHeader;
    }

    /**
     * 保存open状态
     *
     * @param stoFormDTO
     * @return
     */
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
            StoHeader stoHeader = stoHeaderMapper.selectOne(Wrappers.lambdaQuery(StoHeader.class).eq(StoHeader::getId, id));
            if (StringUtils.isEmpty(stoHeader.getOrderStatus()) || !stoHeader.getOrderStatus().equals(StoStatus.OPEN.getStatus())) {
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
        if (StringUtils.isNull(stoFormDTO.getCreateDate())) {
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            Date date = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
            stoHeader.setCreateDate(date);
        } else {
            stoHeader.setCreateDate(stoFormDTO.getCreateDate());
        }
        stoHeader.setCompanyCode(stoFormDTO.getCompanyCode());
        stoHeader.setOrderType("wsto");
        stoHeader.setStoNumber(this.getMaxNumber(stoFormDTO.getCompanyCode()));
        stoHeader.setFromWarehouseCode(stoFormDTO.getFromWarehouseCode());
        stoHeader.setToWarehouseCode(stoFormDTO.getToWarehouseCode());
        stoHeader.setReferenceNumber(stoFormDTO.getReferenceNumber());
        stoHeader.setTrackingNumber(stoFormDTO.getTrackingNumber());
        stoHeader.setOrderStatus(StoStatus.OPEN.getStatus());
        stoHeader.setShipoutDate(stoFormDTO.getShipoutDate());
        stoHeader.setGmtCreate(new Date());
        stoHeader.setGmtModified(new Date());
        stoHeader.setStoNotes(stoFormDTO.getStoNotes());
        return stoHeader;
    }

    /**
     * 获取sto number编号
     *
     * @param companyCode
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    String getMaxNumber(String companyCode) {
        LambdaQueryWrapper<StoHeader> wrapper = Wrappers.lambdaQuery(StoHeader.class)
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
        stoHeader.setCreateDate(stoFormDTO.getCreateDate());
        stoHeader.setToWarehouseCode(stoFormDTO.getToWarehouseCode());
        stoHeader.setReferenceNumber(stoFormDTO.getReferenceNumber());
        stoHeader.setFromWarehouseCode(stoFormDTO.getFromWarehouseCode());
        stoHeader.setTrackingNumber(stoFormDTO.getTrackingNumber());
        stoHeader.setStoNotes(stoFormDTO.getStoNotes());
        stoHeader.setShipoutDate(stoFormDTO.getShipoutDate());
        stoHeader.setGmtModified(new Date());
        return stoHeader;
    }

    /**
     * 二次保存，组装新的sto items
     *
     * @param stoFormDTO
     * @param stoNumber
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
                        i.setSkuNumber(item.getSkuNumber());
                        i.setBasicTransferQty(item.getBasicTransferQty());
                        i.setBasicUom(item.getBasicUom());
                        if (item.getIsDeleted() == 1) {
                            i.setIsDeleted(1);
                        }
                    }
                    i.setGmtModified(new Date());
                    result.add(i);
                });
            }
        }
        return result;
    }

    /**
     * open状态下取消
     *
     * @param stoNumber
     * @return
     */
    public StoHeader cancelOrder(String stoNumber) {
        log.info(">>> 开始取消sto, {}", stoNumber);
        StoHeader stoHeader = this.getStoHeaderByNumber(stoNumber);
        if (!stoHeader.getOrderStatus().equals(StoStatus.OPEN.getStatus())) {
            throw new ServiceException("Only open order can be cancelled");
        }
        stoHeader.setOrderStatus(StoStatus.CANCELED.getStatus());
        stoHeader.setIsDeleted(1);
        stoHeaderMapper.updateById(stoHeader);
        log.info(">>> 取消sto成功, {}", stoNumber);
        return stoHeader;
    }

    /**
     * Open或者新建的order 状态转变为intransit
     * transfer order
     *
     * @param stoFormDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public StoHeader transferOrder(StoFormDTO stoFormDTO) {
        log.info(">>>开始transfer sto，但是先要save");
        StoHeader stoHeader = this.saveOrder(stoFormDTO);
        // 只保留有用的sto item
        List<StoItem> items = stoHeader.getItems().stream().filter(item -> item.getIsDeleted() == 0).collect(Collectors.toList());
        String fromWarehouse = stoHeader.getFromWarehouseCode();
        String toWarehouse = stoHeader.getToWarehouseCode();
        String companyCode = stoHeader.getCompanyCode();
        // 查询库存,并转为skuNumber-onHandQty的map
        List<SimpleStockBalanceVo> stockBalances = stockBalanceNewService.getSkuStockInWarehouse(items.stream().map(StoItem::getSkuNumber).collect(Collectors.toList()), fromWarehouse, companyCode);
        Map<String, BigDecimal> stockMap = stockBalances.stream().collect(Collectors.toMap(SimpleStockBalanceVo::getSkuNumber, SimpleStockBalanceVo::getTotalOnhandQty));
        // 校验库存和要求转移的数量
        Optional<StoItem> lackQuantityItem = items.stream().filter(v -> v.getBasicTransferQty().compareTo(stockMap.get(v.getSkuNumber())) == 1).findFirst();
        if (lackQuantityItem.isPresent()) {
            StoItem lackItem = lackQuantityItem.get();
            throw new ServiceException("Sku Number: " + lackItem.getSkuNumber() + " does not have sufficient available quantity");
        }
        if (StringUtils.isNull(stoFormDTO.getShipoutDate())) {
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            Date date = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
            stoHeader.setShipoutDate(date);
        }
        Date shipoutDate = stoHeader.getShipoutDate();
        List<PreMaterialDocVo> preMaterialDocVos = new ArrayList<>();
        items.forEach(item -> {
            stockBalances.stream().filter(v -> v.getSkuNumber().equals(item.getSkuNumber())).findFirst().ifPresent(b -> {
                PreMaterialDocVo preMaterialDocVo = new PreMaterialDocVo();
                preMaterialDocVo.setMovementType(ModuleConstant.MOVEMENT_TYPE.Transfer_Shipout);
                preMaterialDocVo.setStoNumber(stoHeader.getStoNumber());
                preMaterialDocVo.setCompanyCode(companyCode);
                preMaterialDocVo.setWarehouseCode(fromWarehouse);
                preMaterialDocVo.setToWarehouseCode(toWarehouse);
                preMaterialDocVo.setAveragePrice(b.getAveragePrice());
                preMaterialDocVo.setCurrencyCode(b.getCurrencyCode());
                preMaterialDocVo.setSkuNumber(item.getSkuNumber());
                preMaterialDocVo.setSkuQty(item.getBasicTransferQty());
                preMaterialDocVo.setBasicUom(item.getBasicUom());
                preMaterialDocVo.setReferenceType("WSTO");
                preMaterialDocVo.setReferenceNumber(stoHeader.getStoNumber());
                preMaterialDocVo.setReferenceItem(item.getStoItem());
                preMaterialDocVo.setStockStatus("T");
                preMaterialDocVo.setPostingDate(shipoutDate);
                preMaterialDocVos.add(preMaterialDocVo);
            });
        });
        log.info(">>> 预构建的doc对象为：{}", preMaterialDocVos);
        List<MaterialDoc> materialDocs = materialDocNewService.generateMaterialDoc(companyCode, preMaterialDocVos);
        stockBalanceNewService.updateBalanceByMaterialDocsWhenTransfer(materialDocs);
        //更改header的状态
        stoHeader.setOrderStatus(StoStatus.IN_TRANSIT.getStatus());
        stoHeader.setGmtModified(new Date());
        stoHeader.setShipoutMaterialDoc(materialDocs.get(0).getDocNumber());
        stoHeaderMapper.updateById(stoHeader);
        log.info(">>> transfer sto transfer完成，stoNumber = {}", stoHeader.getStoNumber());
        return stoHeader;
    }

    @Transactional(rollbackFor = Exception.class)
    public Object receiveOrder(StoFormDTO stoFormDTO) {
        String stoNumber = stoFormDTO.getStoNumber();
        StoHeader stoHeader = this.getStoHeaderByNumber(stoNumber);
        List<StoItem> stoItems = stoItemService.getBaseMapper().selectList(new LambdaQueryWrapper<StoItem>().eq(StoItem::getStoNumber, stoNumber));
        List<StoItem> items = stoItems.stream().filter(item -> item.getIsDeleted() == 0).collect(Collectors.toList());
        String fromWarehouse = stoHeader.getFromWarehouseCode();
        String toWarehouse = stoHeader.getToWarehouseCode();
        String companyCode = stoHeader.getCompanyCode();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        Date date = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        if (StringUtils.isNull(stoFormDTO.getReceiveDate())) {
            stoHeader.setReceiveDate(date);
        }
        Date receiveDate = stoHeader.getReceiveDate();
        // 查询库存,并转为skuNumber-onHandQty的map
        List<SimpleStockBalanceVo> stockBalances = stockBalanceNewService.getSkuStockInWarehouse(items.stream().map(StoItem::getSkuNumber).collect(Collectors.toList()), toWarehouse, companyCode);
        Map<String, BigDecimal> stockMap = stockBalances.stream().collect(Collectors.toMap(SimpleStockBalanceVo::getSkuNumber, SimpleStockBalanceVo::getTotalTransferQty));
        // 校验库存和要求转移的数量
        Optional<StoItem> lackQuantityItem = items.stream().filter(v -> v.getBasicTransferQty().compareTo(stockMap.get(v.getSkuNumber())) == 1).findFirst();
        if (lackQuantityItem.isPresent()) {
            StoItem lackItem = lackQuantityItem.get();
            throw new ServiceException("Sku Number: " + lackItem.getSkuNumber() + " does not have sufficient transfer quantity");
        }
        List<PreMaterialDocVo> preMaterialDocVos = new ArrayList<>();
        items.forEach(item -> {
            stockBalances.stream().filter(v -> v.getSkuNumber().equals(item.getSkuNumber())).findFirst().ifPresent(b -> {
                PreMaterialDocVo preMaterialDocVo = new PreMaterialDocVo();
                preMaterialDocVo.setMovementType(ModuleConstant.MOVEMENT_TYPE.Transfer_Receive);
                preMaterialDocVo.setStoNumber(stoHeader.getStoNumber());
                preMaterialDocVo.setCompanyCode(companyCode);
                preMaterialDocVo.setWarehouseCode(toWarehouse);
                // receive只是单个库内部从transfer转移到了available，不存在toWarehouse
                preMaterialDocVo.setToWarehouseCode("");
                preMaterialDocVo.setAveragePrice(b.getAveragePrice());
                preMaterialDocVo.setCurrencyCode(b.getCurrencyCode());
                preMaterialDocVo.setSkuNumber(item.getSkuNumber());
                preMaterialDocVo.setSkuQty(item.getBasicTransferQty());
                preMaterialDocVo.setBasicUom(item.getBasicUom());
                preMaterialDocVo.setReferenceType("WSTO");
                preMaterialDocVo.setReferenceNumber(stoHeader.getStoNumber());
                preMaterialDocVo.setReferenceItem(item.getStoItem());
                preMaterialDocVo.setStockStatus("A");
                preMaterialDocVo.setPostingDate(receiveDate);
                preMaterialDocVos.add(preMaterialDocVo);
            });
        });
        log.info(">>> 预构建的doc对象为：{}", preMaterialDocVos);
        List<MaterialDoc> materialDocs = materialDocNewService.generateMaterialDoc(companyCode, preMaterialDocVos);
        stockBalanceNewService.updateBalanceByMaterialDocsWhenReceive(materialDocs);
        //  更新stoHeader状态
        stoHeader.setOrderStatus(StoStatus.RECEIVED.getStatus());
        stoHeader.setReceiveDate(receiveDate);
        stoHeader.setGmtModified(new Date());
        stoHeader.setReceiveMaterialDoc(materialDocs.get(0).getDocNumber());
        stoHeaderMapper.updateById(stoHeader);
        stoHeader.setItems(stoItems);
        log.info(">>> transfer sto receive完成，stoNumber = {}", stoHeader.getStoNumber());
        return stoHeader;
    }

    /**
     * 将intransit的状态 revert 到open状态
     * @param stoFormDTO
     * @return
     */
    public StoHeader revertToOpen(StoFormDTO stoFormDTO) {
        StoHeader stoHeader = this.getStoHeaderByNumber(stoFormDTO.getStoNumber());
        List<StoItem> stoItems = stoItemService.getBaseMapper().selectList(new LambdaQueryWrapper<StoItem>().eq(StoItem::getStoNumber, stoFormDTO.getStoNumber()));
        if (!stoHeader.getOrderStatus().equals(StoStatus.IN_TRANSIT.getStatus())) {
            throw new ServiceException("only in transit sto can be revert to open");
        }
        String shipoutMaterialDoc = stoHeader.getShipoutMaterialDoc();
        List<MaterialDoc> reverseDocs = materialDocNewService.reverseShipoutMaterialDoc(shipoutMaterialDoc);
        stockBalanceNewService.updateBalanceByMaterialDocsWhenRevertTransfer(reverseDocs);

        stoHeader.setOrderStatus(StoStatus.OPEN.getStatus());
        stoHeader.setShipoutMaterialDoc("");
        stoHeader.setShipoutDate(null);
        stoHeader.setGmtModified(new Date());
        stoHeader.setItems(stoItems);
        stoHeaderMapper.updateById(stoHeader);
        log.info(">>> transfer sto revert transfer完成，stoNumber = {}", stoHeader.getStoNumber());
        return stoHeader;
    }

    /**
     * 将received的状态 revert 到intransit状态
     * @param stoFormDTO
     * @return
     */
    public StoHeader revertToIntransit(StoFormDTO stoFormDTO) {
        StoHeader stoHeader = this.getStoHeaderByNumber(stoFormDTO.getStoNumber());
        List<StoItem> stoItems = stoItemService.getBaseMapper().selectList(new LambdaQueryWrapper<StoItem>().eq(StoItem::getStoNumber, stoFormDTO.getStoNumber()));
        if (!stoHeader.getOrderStatus().equals(StoStatus.RECEIVED.getStatus())) {
            throw new ServiceException("only received sto can be revert to in transit");
        }
        String receiveMaterialDoc = stoHeader.getReceiveMaterialDoc();
        List<MaterialDoc> reverseDocs = materialDocNewService.reverseReceiveMaterialDoc(receiveMaterialDoc);
        stockBalanceNewService.updateBalanceByMaterialDocsWhenRevertReceive(reverseDocs);

        stoHeader.setOrderStatus(StoStatus.IN_TRANSIT.getStatus());
        stoHeader.setReceiveMaterialDoc("");
        stoHeader.setReceiveDate(null);
        stoHeader.setGmtModified(new Date());
        stoHeader.setItems(stoItems);
        stoHeaderMapper.updateById(stoHeader);
        log.info(">>> transfer sto revert received完成，stoNumber = {}", stoHeader.getStoNumber());
        return stoHeader;
    }

    public Boolean saveFreeField(StoFormDTO stoFormDTO) {
        StoHeader stoHeader = this.getStoHeaderByNumber(stoFormDTO.getStoNumber());
        // 按非空修改三个字段值，仅仅处理referenceNumber,trackingNumber,stoNotes
        stoHeader = ChainSetValue.of(stoHeader)
                .updateIfNotNull(stoHeader::setReferenceNumber, stoFormDTO.getReferenceNumber())
                .updateIfNotNull(stoHeader::setTrackingNumber, stoFormDTO.getTrackingNumber())
                .updateIfNotNull(stoHeader::setStoNotes, stoFormDTO.getStoNotes())
                .val();
        int i = stoHeaderMapper.updateById(stoHeader);
        return i > 0;
    }
}
