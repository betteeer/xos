package com.inossem.oms.svc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inossem.oms.base.svc.domain.MaterialDoc;
import com.inossem.oms.base.svc.domain.StockBalance;
import com.inossem.oms.base.svc.domain.VO.SimpleStockBalanceVo;
import com.inossem.oms.base.svc.mapper.SkuMasterMapper;
import com.inossem.oms.base.svc.mapper.StockBalanceMapper;
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
public class StockBalanceNewService extends ServiceImpl<StockBalanceMapper, StockBalance> implements IStockBalanceService {

    @Resource
    private StockBalanceMapper stockBalanceMapper;

    @Resource
    private SkuMasterMapper skuMasterMapper;

    @Resource
    private CompanyService companyService;

    @Transactional(rollbackFor = Exception.class)
    public List<SimpleStockBalanceVo> getSkuStockInWarehouse(List<String> skuNumbers, String warehouseCode, String companyCode) {
        return this.getSkuStockInWarehouse(skuNumbers, Arrays.asList(warehouseCode), companyCode);
    }
    @Transactional(rollbackFor = Exception.class)
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

    @Transactional(rollbackFor = Exception.class)
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
                        throw new ServiceException("warehouse:"+ warehouseCode + " does not have stock balance");
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


    private StockBalance reduceOnHand(StockBalance s, MaterialDoc materialDoc) {
        StockBalance b = new StockBalance();
        BeanUtils.copyProperties(s, b);
        b.setTotalAmount(s.getTotalAmount().subtract(materialDoc.getTotalAmount()));
        b.setTotalOnhandQty(s.getTotalOnhandQty().subtract(materialDoc.getSkuQty()));
        b.setTotalQty(s.getTotalQty().subtract(materialDoc.getSkuQty()));
        b.setGmtModified(new Date());
        return b;
    }
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
        String[] warehouseCodes = { materialDocs.get(0).getToWarehouseCode()};
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
                    if (warehouseCode.equals(warehouseCodes[0])) {
                        throw new ServiceException("warehouse:"+ warehouseCode + " does not have stock balance");
                    }
                }
            }
        }
        log.info(">>> 库存变动StockBalance为：{}", result);
        return saveOrUpdateBatch(result);
    }

    /**
     * 仓库中的 transfer 转移到 onhand
     * @param s
     * @param materialDoc
     * @return
     */
    private StockBalance transferToOnHand(StockBalance s, MaterialDoc materialDoc) {
        StockBalance b = new StockBalance();
        BeanUtils.copyProperties(s, b);
        b.setTotalOnhandQty(s.getTotalOnhandQty().add(materialDoc.getSkuQty()));
        b.setTotalTransferQty(s.getTotalTransferQty().add(materialDoc.getSkuQty()));
        b.setGmtModified(new Date());
        return b;
    }
}
