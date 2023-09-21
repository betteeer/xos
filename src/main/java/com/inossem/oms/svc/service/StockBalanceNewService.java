package com.inossem.oms.svc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.inossem.oms.base.svc.domain.*;
import com.inossem.oms.base.svc.domain.DTO.BalanceSearchFormDTO;
import com.inossem.oms.base.svc.domain.VO.SimpleStockBalanceVo;
import com.inossem.oms.base.svc.mapper.StockBalanceMapper;
import com.inossem.oms.base.svc.vo.QueryStockBalanceResVo;
import com.inossem.oms.base.svc.vo.QueryStockListVo;
import com.inossem.oms.mdm.service.CompanyService;
import com.inossem.sco.common.core.exception.ServiceException;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

interface IStockBalanceService extends IService<StockBalance> {
}
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class StockBalanceNewService extends ServiceImpl<StockBalanceMapper, StockBalance> implements IStockBalanceService {
    /**                                    from            to
     * 1. open -> transfer             reduceOnHand      存在addOnTransfer/不存在buildNoneStockBalanceBaseOnFromStock
     * 2. transfer -> open[revert]     addOnHand         reduceOnTransfer
     * 3. transfer -> receive                -           transferToOnHand
     * 4. receive -> transfer[revert]        -           onHandToTransfer
     */
    @Resource
    private StockBalanceMapper stockBalanceMapper;
    @Resource
    private CompanyService companyService;

    public List<SimpleStockBalanceVo> getSkuStockInWarehouse(List<String> skuNumbers, String warehouseCode, String companyCode) {
        return this.getSkuStockInWarehouse(skuNumbers, Arrays.asList(warehouseCode), companyCode);
    }
    public List<SimpleStockBalanceVo> getSkuStockInWarehouse(List<String> skuNumbers, List<String> warehouseCodes, String companyCode) {
        LambdaQueryWrapper<StockBalance> stockWrapper = new LambdaQueryWrapper<>();
        stockWrapper.eq(StockBalance::getCompanyCode, companyCode)
                .in(StockBalance::getSkuNumber, skuNumbers)
                .in(StockBalance::getWarehouseCode, warehouseCodes);
        // 查询所有满足条件的库存
        List<StockBalance> stockBalances = stockBalanceMapper.selectList(stockWrapper);
        List<SimpleStockBalanceVo> result = new ArrayList();
        String currencyCode = "";
        // 遍历传入的仓库code，简化stock balance对象 并且将没有查到库存的warehouse code填充默认值。
        for (String warehouseCode : warehouseCodes) {
            for (String skuNumber : skuNumbers) {
                Optional<StockBalance> first = stockBalances.stream().filter(v -> v.getWarehouseCode().equals(warehouseCode) && v.getSkuNumber().equals(skuNumber)).findFirst();
                SimpleStockBalanceVo vo = new SimpleStockBalanceVo();
                if (first.isPresent()) {
                    BeanUtils.copyProperties(first.get(), vo);
                } else {
                    if (currencyCode.equals("")) {
                       currencyCode = companyService.getCurrencyCodeByCompanyCode(companyCode);
                    }
                    vo.setWarehouseCode(warehouseCode);
                    vo.setSkuNumber(skuNumber);
                    vo.setCompanyCode(companyCode);
                    vo.setTotalQty(BigDecimal.ZERO);
                    vo.setTotalOnhandQty(BigDecimal.ZERO);
                    vo.setTotalBlockQty(BigDecimal.ZERO);
                    vo.setTotalTransferQty(BigDecimal.ZERO);
                    vo.setAveragePrice(BigDecimal.ZERO);
                    vo.setCurrencyCode(currencyCode);
                    vo.setId(null);
                }
                result.add(vo);
            }
        }
        return result;
    }

    public boolean updateBalanceByMaterialDocsWhenTransfer(List<MaterialDoc> materialDocs) {
        if (StringUtils.isEmpty(materialDocs)) {
            throw new ServiceException("no item pass to change stock balance");
        }
        List<String> skuNumbers = materialDocs.stream().map(MaterialDoc::getSkuNumber).collect(Collectors.toList());
        String[] warehouseCodes = { materialDocs.get(0).getWarehouseCode(), materialDocs.get(0).getToWarehouseCode()};
        String companyCode = materialDocs.get(0).getCompanyCode();
        LambdaQueryWrapper<StockBalance> stockWrapper = new LambdaQueryWrapper<>();
        stockWrapper.eq(StockBalance::getCompanyCode, companyCode)
                .in(StockBalance::getSkuNumber, skuNumbers)
                .in(StockBalance::getWarehouseCode, warehouseCodes);
        // 查询所有满足条件的库存
        List<StockBalance> stockBalances = stockBalanceMapper.selectList(stockWrapper);
        List<StockBalance> result = new ArrayList<>();
        for (String warehouseCode : warehouseCodes) {
            for (String skuNumber : skuNumbers) {
                Optional<StockBalance> first = stockBalances.stream().filter(v -> v.getWarehouseCode().equals(warehouseCode) && v.getSkuNumber().equals(skuNumber)).findFirst();
                MaterialDoc materialDoc = materialDocs.stream().filter(v -> v.getSkuNumber().equals(skuNumber)).findFirst().orElse(null);
                if (StringUtils.isNull(materialDoc)) {
                    throw new ServiceException("sku number:" +skuNumber + " does not have material doc");
                }
                if (first.isPresent()) {
                    StockBalance s = first.get();
                    StockBalance b;
                    if (warehouseCode.equals(warehouseCodes[0])) {
                        b = reduceOnHand(s, materialDoc);
                    } else {
                        b = addOnTransfer(s, materialDoc);
                    }
                    result.add(b);
                } else { // 没找到，说明to warehouse没查到库存，那就先取from warehouse的
                    if (warehouseCode.equals(warehouseCodes[0])) {
                        throw new ServiceException("warehouse:"+ warehouseCode + ",skuNumber: "+ skuNumber +" does not have stock balance");
                    } else {
                        StockBalance fromStock = stockBalances.stream().filter(v -> v.getWarehouseCode().equals(warehouseCodes[0]) && v.getSkuNumber().equals(skuNumber)).findFirst().orElse(null);
                        StockBalance s = buildNoneStockBalanceBaseOnFromStock(fromStock, materialDoc);
                        result.add(s);
                    }
                }
            }
        }
        log.info(">>> 库存变动StockBalance为：{}", result);
        return saveOrUpdateBatch(result);
    }


    /**
     * transfer时 减少from warehouse的库存
     * @param s
     * @param materialDoc
     * @return
     */
    private StockBalance reduceOnHand(StockBalance s, MaterialDoc materialDoc) {
        StockBalance b = new StockBalance();
        BeanUtils.copyProperties(s, b);
        if (s.getTotalOnhandQty().compareTo(materialDoc.getSkuQty()) == -1) {
            throw new ServiceException("warehouse:" + s.getWarehouseCode() + ",sku number:" + s.getSkuNumber() +" does not have sufficient onHand balance");
        }
        b.setTotalOnhandQty(s.getTotalOnhandQty().subtract(materialDoc.getSkuQty()));
        b.setTotalAmount(s.getTotalAmount().subtract(materialDoc.getTotalAmount()));
        b.setTotalQty(s.getTotalQty().subtract(materialDoc.getSkuQty()));
        b.setGmtModified(new Date());
        return b;
    }

    /**
     * transfer时 增加to warehouse的transfer
     * @param s
     * @param materialDoc
     * @return
     */
    private StockBalance addOnTransfer(StockBalance s, MaterialDoc materialDoc) {
        StockBalance b = new StockBalance();
        BeanUtils.copyProperties(s, b);
        b.setTotalAmount(s.getTotalAmount().add(materialDoc.getTotalAmount()));
        b.setTotalTransferQty(s.getTotalTransferQty().add(materialDoc.getSkuQty()));
        b.setTotalQty(s.getTotalQty().add(materialDoc.getSkuQty()));
        b.setGmtModified(new Date());
        return b;
    }

    /**
     * 在转移过程中，如果 to warehouse 没有库存记录，那么要根据传入的from warehouse的库存，构建StockBalance对象
     * @param fromStock
     * @return
     */
    private StockBalance buildNoneStockBalanceBaseOnFromStock(StockBalance fromStock, MaterialDoc materialDoc) {
        StockBalance toBalance = new StockBalance();
        // companyCode skuNumber averagePrice currencyCode basicUom isDeleted
        BeanUtils.copyProperties(fromStock, toBalance);
        toBalance.setId(null);
        toBalance.setCompanyCode(fromStock.getCompanyCode());
        toBalance.setWarehouseCode(materialDoc.getToWarehouseCode());
        toBalance.setTotalAmount(materialDoc.getTotalAmount());
        toBalance.setTotalOnhandQty(BigDecimal.ZERO);
        toBalance.setTotalBlockQty(BigDecimal.ZERO);
        toBalance.setTotalTransferQty(materialDoc.getSkuQty());
        toBalance.setTotalQty(materialDoc.getSkuQty());
        toBalance.setGmtCreate(new Date());
        toBalance.setGmtModified(new Date());
        return toBalance;
    }

    public boolean updateBalanceByMaterialDocsWhenReceive(List<MaterialDoc> materialDocs) {
        if (StringUtils.isEmpty(materialDocs)) {
            throw new ServiceException("no item pass to change stock balance");
        }
        List<String> skuNumbers = materialDocs.stream().map(MaterialDoc::getSkuNumber).collect(Collectors.toList());
        String[] warehouseCodes = { materialDocs.get(0).getWarehouseCode()};
        String companyCode = materialDocs.get(0).getCompanyCode();
        LambdaQueryWrapper<StockBalance> stockWrapper = new LambdaQueryWrapper<>();
        stockWrapper.eq(StockBalance::getCompanyCode, companyCode)
                .in(StockBalance::getSkuNumber, skuNumbers)
                .in(StockBalance::getWarehouseCode, warehouseCodes);
        // 查询所有满足条件的库存
        List<StockBalance> stockBalances = stockBalanceMapper.selectList(stockWrapper);
        List<StockBalance> result = new ArrayList<>();
        for (String warehouseCode : warehouseCodes) {
            for (String skuNumber : skuNumbers) {
                Optional<StockBalance> first = stockBalances.stream().filter(v -> v.getWarehouseCode().equals(warehouseCode) && v.getSkuNumber().equals(skuNumber)).findFirst();
                MaterialDoc materialDoc = materialDocs.stream().filter(v -> v.getSkuNumber().equals(skuNumber)).findFirst().orElse(null);
                if (StringUtils.isNull(materialDoc)) {
                    throw new ServiceException("sku number:" +skuNumber + " does not have material doc");
                }
                if (first.isPresent()) {
                    StockBalance s = first.get();
                    StockBalance b = transferToOnHand(s, materialDoc);
                    result.add(b);
                } else { // 没找到，说明to warehouse没查到库存，那就先取from warehouse的
                    throw new ServiceException("warehouse:"+ warehouseCode + ",skuNumber: "+ skuNumber +" does not have stock balance");
                }
            }
        }
        log.info(">>> 库存变动StockBalance为：{}", result);
        return saveOrUpdateBatch(result);
    }

    /**
     * receive时 仓库中的 transfer 转移到 onhand
     * @param s
     * @param materialDoc
     * @return
     */
    private StockBalance transferToOnHand(StockBalance s, MaterialDoc materialDoc) {
        StockBalance b = new StockBalance();
        BeanUtils.copyProperties(s, b);
        b.setTotalOnhandQty(s.getTotalOnhandQty().add(materialDoc.getSkuQty()));
        if (s.getTotalTransferQty().compareTo(materialDoc.getSkuQty()) == -1) {
            throw new ServiceException("warehouse:" + s.getWarehouseCode() + ",sku number:" + s.getSkuNumber() +" does not have sufficient transfer balance");
        }
        b.setTotalTransferQty(s.getTotalTransferQty().subtract(materialDoc.getSkuQty()));
        b.setGmtModified(new Date());
        return b;
    }

    /**
     * revert transfer： intransit -> open,
     *
     * @param materialDocs
     * @return
     */
    public boolean updateBalanceByMaterialDocsWhenRevertTransfer(List<MaterialDoc> materialDocs) {
        if (StringUtils.isEmpty(materialDocs)) {
            throw new ServiceException("no item pass to change stock balance");
        }
        List<String> skuNumbers = materialDocs.stream().map(MaterialDoc::getSkuNumber).collect(Collectors.toList());
        // 这边的warehouse 和 toWarehouse 分别是transfer时的接收库 和 发出库，是相反的
        String[] warehouseCodes = { materialDocs.get(0).getWarehouseCode(), materialDocs.get(0).getToWarehouseCode()};
        String companyCode = materialDocs.get(0).getCompanyCode();
        LambdaQueryWrapper<StockBalance> stockWrapper = new LambdaQueryWrapper<>();
        stockWrapper.eq(StockBalance::getCompanyCode, companyCode)
                .in(StockBalance::getSkuNumber, skuNumbers)
                .in(StockBalance::getWarehouseCode, warehouseCodes);
        // 查询所有满足条件的库存
        List<StockBalance> stockBalances = stockBalanceMapper.selectList(stockWrapper);
        List<StockBalance> result = new ArrayList<>();
        for (String warehouseCode : warehouseCodes) {
            for (String skuNumber : skuNumbers) {
                StockBalance s = stockBalances.stream().filter(v -> v.getWarehouseCode().equals(warehouseCode) && v.getSkuNumber().equals(skuNumber)).findFirst().orElse(null);
                MaterialDoc materialDoc = materialDocs.stream().filter(v -> v.getSkuNumber().equals(skuNumber)).findFirst().orElse(null);
                if (StringUtils.isNull(materialDoc)) {
                    throw new ServiceException("sku number:" +skuNumber + " does not have material doc");
                }
                if (StringUtils.isNull(s)) {
                    throw new ServiceException("warehouse:"+ warehouseCode + ",skuNumber: "+ skuNumber +" does not have stock balance");
                }
                StockBalance b;
                if (warehouseCode.equals(warehouseCodes[0])) { // 等于老的接收库的，需要减去transfer
                    b = reduceOnTransfer(s, materialDoc);
                } else { // 等于老的发出库的，需要加在onHand上
                    b = addOnHand(s, materialDoc);
                }
                result.add(b);
            }
        }
        log.info(">>> 库存变动StockBalance为：{}", result);
        return saveOrUpdateBatch(result);
    }

    private StockBalance reduceOnTransfer(StockBalance s, MaterialDoc materialDoc) {
        StockBalance b = new StockBalance();
        BeanUtils.copyProperties(s, b);
        if (s.getTotalTransferQty().compareTo(materialDoc.getSkuQty()) == -1) {
            throw new ServiceException("warehouse:" + s.getWarehouseCode() + ",sku number:" + s.getSkuNumber() +" does not have sufficient transfer balance");
        }
        b.setTotalTransferQty(s.getTotalTransferQty().subtract(materialDoc.getSkuQty()));
        b.setTotalAmount(s.getTotalAmount().subtract(materialDoc.getTotalAmount()));
        b.setTotalQty(s.getTotalQty().subtract(materialDoc.getSkuQty()));
        b.setGmtModified(new Date());
        return b;
    }
    private StockBalance addOnHand(StockBalance s, MaterialDoc materialDoc) {
        StockBalance b = new StockBalance();
        BeanUtils.copyProperties(s, b);
        b.setTotalAmount(s.getTotalAmount().add(materialDoc.getTotalAmount()));
        b.setTotalOnhandQty(s.getTotalOnhandQty().add(materialDoc.getSkuQty()));
        b.setTotalQty(s.getTotalQty().add(materialDoc.getSkuQty()));
        b.setGmtModified(new Date());
        return b;
    }

    public boolean updateBalanceByMaterialDocsWhenRevertReceive(List<MaterialDoc> materialDocs) {
        if (StringUtils.isEmpty(materialDocs)) {
            throw new ServiceException("no item pass to change stock balance");
        }
        List<String> skuNumbers = materialDocs.stream().map(MaterialDoc::getSkuNumber).collect(Collectors.toList());
        String[] warehouseCodes = { materialDocs.get(0).getWarehouseCode()};
        String companyCode = materialDocs.get(0).getCompanyCode();
        LambdaQueryWrapper<StockBalance> stockWrapper = new LambdaQueryWrapper<>();
        stockWrapper.eq(StockBalance::getCompanyCode, companyCode)
                .in(StockBalance::getSkuNumber, skuNumbers)
                .in(StockBalance::getWarehouseCode, warehouseCodes);
        // 查询所有满足条件的库存
        List<StockBalance> stockBalances = stockBalanceMapper.selectList(stockWrapper);
        List<StockBalance> result = new ArrayList<>();
        for (String warehouseCode : warehouseCodes) {
            for (String skuNumber : skuNumbers) {
                StockBalance s = stockBalances.stream().filter(v -> v.getWarehouseCode().equals(warehouseCode) && v.getSkuNumber().equals(skuNumber)).findFirst().orElse(null);
                MaterialDoc materialDoc = materialDocs.stream().filter(v -> v.getSkuNumber().equals(skuNumber)).findFirst().orElse(null);
                if (StringUtils.isNull(materialDoc)) {
                    throw new ServiceException("sku number:" +skuNumber + " does not have material doc");
                }
                if (StringUtils.isNull(s)) {
                    throw new ServiceException("warehouse:"+ warehouseCode + ",skuNumber: "+ skuNumber +" does not have stock balance");
                }
                StockBalance b = onHandToTransfer(s, materialDoc);
                result.add(b);

            }
        }
        log.info(">>> 库存变动StockBalance为：{}", result);
        return saveOrUpdateBatch(result);
    }

    private StockBalance onHandToTransfer(StockBalance s, MaterialDoc materialDoc) {
        StockBalance b = new StockBalance();
        BeanUtils.copyProperties(s, b);
        if (s.getTotalOnhandQty().compareTo(materialDoc.getSkuQty()) == -1) {
            throw new ServiceException("warehouse:" + s.getWarehouseCode() + ",sku number:" + s.getSkuNumber() +" does not have sufficient onHand balance");
        }
        b.setTotalOnhandQty(s.getTotalOnhandQty().subtract(materialDoc.getSkuQty()));
        b.setTotalTransferQty(s.getTotalTransferQty().add(materialDoc.getSkuQty()));
        b.setGmtModified(new Date());
        return b;
    }

    /**
     * 获取库存，之前外包写的一堆shit，现在进行优化
     * @param queryStockListVo
     * @return
     */
    public List<QueryStockBalanceResVo> getStockList(QueryStockListVo query) {
        MPJLambdaWrapper<StockBalance> wrapper = JoinWrappers.lambda(StockBalance.class)
                .selectAll(StockBalance.class)
                .leftJoin(Warehouse.class, Warehouse::getWarehouseCode, StockBalance::getWarehouseCode,
                        ext -> ext.selectAs(Warehouse::getName, QueryStockBalanceResVo::getWarehouseName))
                .leftJoin(SkuMaster.class, SkuMaster::getSkuNumber, StockBalance::getSkuNumber,
                        ext -> ext.selectAs(SkuMaster::getSkuSatetyStock, QueryStockBalanceResVo::getSkuSatetyStock)
                                .selectAs(SkuMaster::getSkuName, QueryStockBalanceResVo::getSkuName))
                .eq(StringUtils.isNotEmpty(query.getCompanyCode()),StockBalance::getCompanyCode, query.getCompanyCode())
                .eq(StringUtils.isNotEmpty(query.getWarehouseCode()), StockBalance::getWarehouseCode, query.getWarehouseCode())
                .like(StringUtils.isNotEmpty(query.getSearchText()), StockBalance::getSkuNumber, query.getSearchText())
                .eq(StockBalance::getIsDeleted, 0);
        if (StringUtils.isNotEmpty(query.getSkuCodeSort())) {
            wrapper.orderBy(true, query.getSkuCodeSort().equals("ASC"), StockBalance::getSkuNumber);
        } else if(StringUtils.isNotEmpty(query.getTotalStockQtySort())) {
            wrapper.orderBy(true, query.getTotalStockQtySort().equals("ASC"), StockBalance::getTotalQty);
        } else if (StringUtils.isNotEmpty(query.getOnHandQtySort())) {
            wrapper.orderBy(true, query.getOnHandQtySort().equals("ASC"), StockBalance::getTotalOnhandQty);
        } else if (StringUtils.isNotEmpty(query.getBlockQtySort())) {
            wrapper.orderBy(true, query.getBlockQtySort().equals("ASC"), StockBalance::getTotalBlockQty);
        } else if (StringUtils.isNotEmpty(query.getBalanceValueSort())) {
            wrapper.orderBy(true, query.getBalanceValueSort().equals("ASC"), StockBalance::getTotalAmount);
        } else if (StringUtils.isNotEmpty(query.getTransferQtySort())) {
            wrapper.orderBy(true, query.getTransferQtySort().equals("ASC"), StockBalance::getTotalTransferQty);
        } else {
            wrapper.orderByDesc(StockBalance::getGmtCreate);
        }
        List<QueryStockBalanceResVo> queryStockBalanceResVos = stockBalanceMapper.selectJoinList(QueryStockBalanceResVo.class, wrapper);
        return queryStockBalanceResVos;
    }

    public List<QueryStockBalanceResVo> getSatetyList(QueryStockListVo queryStockListVo) {
        List<QueryStockBalanceResVo> stockList = this.getStockList(queryStockListVo);
        return stockList.stream().filter(v -> {
            if (v.getSkuSatetyStock() != null) {
                return v.getSkuSatetyStock().compareTo(v.getTotalQty()) > 0;
            } else {
                return false;
            }
        }).collect(Collectors.toList());
    }

    public List<StockBalance> getList(BalanceSearchFormDTO form) {
        MPJLambdaWrapper<StockBalance> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(StockBalance.class);
        wrapper.eq(StockBalance::getCompanyCode, form.getCompanyCode());
        wrapper.in(StringUtils.isNotEmpty(form.getWarehouse()), StockBalance::getWarehouseCode, form.getWarehouse());
        wrapper.between(StringUtils.isNotNull(form.getTotalQtyStart()), StockBalance::getTotalQty, form.getTotalQtyStart(), form.getTotalQtyEnd());
        wrapper.between(StringUtils.isNotNull(form.getTotalOnhandQtyStart()), StockBalance::getTotalOnhandQty, form.getTotalOnhandQtyStart(), form.getTotalOnhandQtyEnd());
        wrapper.between(StringUtils.isNotNull(form.getTotalBlockQtyStart()), StockBalance::getTotalBlockQty, form.getTotalBlockQtyStart(), form.getTotalBlockQtyEnd());
        wrapper.between(StringUtils.isNotNull(form.getTotalTransferQtyStart()), StockBalance::getTotalTransferQty, form.getTotalTransferQtyStart(), form.getTotalTransferQtyEnd());
        wrapper.between(StringUtils.isNotNull(form.getAveragePriceStart()), StockBalance::getAveragePrice, form.getAveragePriceStart(), form.getAveragePriceEnd());
        wrapper.leftJoin(SkuMaster.class,SkuMaster::getSkuNumber, StockBalance::getSkuNumber,  ext -> {
            ext.nested(StringUtils.isNotEmpty(form.getSearchText()),
                    i -> i.like(SkuMaster::getSkuName, form.getSearchText())
                            .or().like(SkuMaster::getSkuNumber, form.getSearchText()));
            ext.in(StringUtils.isNotEmpty(form.getSkuGroup()), SkuMaster::getSkuGroupCode, form.getSkuGroup());
            if (form.getSafetyStock().equals("BelowSafety")) {
                ext.isNotNull(SkuMaster::getSkuSatetyStock).gt(SkuMaster::getSkuSatetyStock, StockBalance::getTotalOnhandQty);
            } else if (form.getSafetyStock().equals("Safety")) {
                ext.isNotNull(SkuMaster::getSkuSatetyStock);
            }
            return ext.selectAs(SkuMaster::getSkuSatetyStock, StockBalance::getSkuSatetyStock)
                    .selectAs(SkuMaster::getSkuGroupName, StockBalance::getSkuGroupName);
        });
        wrapper.orderBy(StringUtils.isNotNull(form.getOrderBy()), form.getIsAsc(), StringUtils.toUnderScoreCase(form.getOrderBy()));
        // 排序的字段值相同则按照id倒序
        wrapper.orderBy(true, false, StockBalance::getId);
        return stockBalanceMapper.selectJoinList(StockBalance.class, wrapper);
    }
}
