package com.inossem.oms.svc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inossem.oms.base.svc.domain.StockBalance;
import com.inossem.oms.base.svc.domain.VO.SimpleStockBalanceVo;
import com.inossem.oms.base.svc.mapper.StockBalanceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class StockBalanceNewService {

    @Resource
    private StockBalanceMapper stockBalanceMapper;

    public List<SimpleStockBalanceVo> getSkuStockInWarehouse(List<String> skuNumbers, List<String> warehouseCodes, String companyCode) {
        LambdaQueryWrapper<StockBalance> stockWrapper = new LambdaQueryWrapper<>();
        stockWrapper.eq(StockBalance::getCompanyCode, companyCode)
                .in(StockBalance::getSkuNumber, skuNumbers)
                .in(StockBalance::getWarehouseCode, warehouseCodes);
        // 查询所有满足条件的库存
        List<StockBalance> stockBalances = stockBalanceMapper.selectList(stockWrapper);
        List<SimpleStockBalanceVo> result = new ArrayList();
        // 遍历传入的仓库code，简化stock balance对象 并且将没有查到库存的warehouse code填充默认值。
        for (String warehouseCode : warehouseCodes) {
            for (String skuNumber : skuNumbers) {
                Optional<StockBalance> first = stockBalances.stream().filter(v -> v.getWarehouseCode().equals(warehouseCode) && v.getSkuNumber().equals(skuNumber)).findFirst();
                SimpleStockBalanceVo vo = new SimpleStockBalanceVo();
                if (first.isPresent()) {
                    BeanUtils.copyProperties(first.get(), vo);
                } else {
                    vo.setWarehouseCode(warehouseCode);
                    vo.setSkuNumber(skuNumber);
                    vo.setCompanyCode(companyCode);
                    vo.setTotalQty(BigDecimal.ZERO);
                    vo.setTotalOnhandQty(BigDecimal.ZERO);
                    vo.setTotalBlockQty(BigDecimal.ZERO);
                    vo.setTotalTransferQty(BigDecimal.ZERO);
                    vo.setId(null);
                }
                result.add(vo);
            }

        }
        return result;
    }
}
