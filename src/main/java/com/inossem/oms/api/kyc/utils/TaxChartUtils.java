package com.inossem.oms.api.kyc.utils;

import com.inossem.oms.api.kyc.model.KycCompany;

import java.util.List;

public class TaxChartUtils {
    public static String getTaxValue(List<KycCompany.TaxItem> taxCharts, String taxCode) {
        for (KycCompany.TaxItem taxChart : taxCharts) {
            if (taxCode.equalsIgnoreCase(taxChart.getName())) {
                return taxChart.getValue();
            }
        }
        return null;
    }
}
