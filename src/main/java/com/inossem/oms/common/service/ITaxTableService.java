package com.inossem.oms.common.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.inossem.oms.base.svc.domain.TaxTable;
import com.inossem.oms.base.svc.domain.TaxTableItems;
import com.inossem.oms.base.svc.domain.VO.TaxCalculateResp;
import com.inossem.oms.base.svc.domain.VO.TaxTableCalculate;
import com.inossem.oms.base.svc.mapper.TaxTableMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 【税费计算】Service接口
 *
 * @author shigf
 * @date 2022-10-19
 */
@Service
@Slf4j
public class ITaxTableService {

    @Resource
    private TaxTableMapper taxTableMapper;

    public TaxCalculateResp taxCaculation(TaxTableCalculate taxTableCalculate) {
        TaxCalculateResp taxCalculateResp = new TaxCalculateResp();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("province_code", taxTableCalculate.getProvinceCode());
        TaxTable taxTable = taxTableMapper.selectOne(qw);
        log.info("算税的数据为：" + taxTable);
        if (taxTable == null) {
            throw new RuntimeException("未找到{" + taxTableCalculate.getProvinceCode() + "} province 的数据.");
        }

        BigDecimal netAmount = BigDecimal.ZERO;
        BigDecimal gst = BigDecimal.ZERO;
        BigDecimal hst = BigDecimal.ZERO;
        BigDecimal qst = BigDecimal.ZERO;
        BigDecimal pst = BigDecimal.ZERO;
        BigDecimal taxSubtotal;
        for (int i = 0; i < taxTableCalculate.getTaxTableItemsList().size(); i++) {
            TaxTableItems x = taxTableCalculate.getTaxTableItemsList().get(i);
            netAmount = netAmount.add(x.getAmount());
            //1 免税   0不免税
            if ("0".equals(x.getIsTaxExempt())) {
                BigDecimal gstConfig = taxTable.getGstRate();
                if (gstConfig != null) {
                    BigDecimal gstRate = gstConfig.divide(BigDecimal.valueOf(100));
                    gst = gst.add(x.getAmount().multiply(gstRate).setScale(2,BigDecimal.ROUND_HALF_UP));
                }

                BigDecimal hstConfig = taxTable.getHstRate();
                if (hstConfig != null) {
                    BigDecimal hstRate = hstConfig.divide(BigDecimal.valueOf(100));
                    hst = hst.add(x.getAmount().multiply(hstRate).setScale(2,BigDecimal.ROUND_HALF_UP));
                }

                BigDecimal qstConfig = taxTable.getQstRate();
                if (qstConfig != null) {
                    BigDecimal qstRate = qstConfig.divide(BigDecimal.valueOf(100));
                    qst = qst.add(x.getAmount().multiply(qstRate).setScale(2,BigDecimal.ROUND_HALF_UP));
                }

                BigDecimal pstConfig = taxTable.getPstRate();
                if (pstConfig != null) {
                    BigDecimal pstRate = pstConfig.divide(BigDecimal.valueOf(100));
                    pst = pst.add(x.getAmount().multiply(pstRate).setScale(2,BigDecimal.ROUND_HALF_UP));
                }
            }
        }
        taxSubtotal = gst.add(hst).add(qst).add(pst);
        taxCalculateResp.setNetAmount(netAmount);
        taxCalculateResp.setGst(gst);
        taxCalculateResp.setHst(hst);
        taxCalculateResp.setQst(qst);
        taxCalculateResp.setPst(pst);
        taxCalculateResp.setTaxSubtotal(taxSubtotal);
        taxCalculateResp.setTotalCad(netAmount.add(taxSubtotal));
        return taxCalculateResp;
    }
}
