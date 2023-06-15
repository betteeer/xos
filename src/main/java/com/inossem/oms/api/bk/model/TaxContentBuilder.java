package com.inossem.oms.api.bk.model;

import java.math.BigDecimal;
import java.util.ArrayList;

public class TaxContentBuilder {
    public static ArrayList<TaxContent> build(BigDecimal gst, BigDecimal hst, BigDecimal pst, BigDecimal qst) {
        ArrayList<TaxContent> taxContents = new ArrayList<>();
        TaxContent taxGst = new TaxContent("HST / GST", "Tax Chart", "Country Tax", "GST", gst.add(hst));
        TaxContent taxQst = new TaxContent("QST", "Tax Chart", "Province Tax", "QST", qst);
        TaxContent taxPst = new TaxContent("PST", "Tax Chart", "Country Tax", "PST", pst);

        taxContents.add(taxGst);
        taxContents.add(taxQst);
        taxContents.add(taxPst);
        return taxContents;
    }
}
