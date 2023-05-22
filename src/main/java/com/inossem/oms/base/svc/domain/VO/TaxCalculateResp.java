package com.inossem.oms.base.svc.domain.VO;

import io.swagger.annotations.ApiModel;

import java.math.BigDecimal;

/**
 * Tax Calculate Response Data
 *
 * @author guoh
 * @date 2022-10-17
 */
@ApiModel("TaxCalculateResp")
public class TaxCalculateResp {

    private BigDecimal netAmount;

    private BigDecimal gst;

    private BigDecimal hst;

    private BigDecimal qst;

    private BigDecimal pst;

    private BigDecimal taxSubtotal;

    private BigDecimal totalCad;

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getGst() {
        return gst;
    }

    public void setGst(BigDecimal gst) {
        this.gst = gst.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getHst() {
        return hst;
    }

    public void setHst(BigDecimal hst) {
        this.hst = hst.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getQst() {
        return qst;
    }

    public void setQst(BigDecimal qst) {
        this.qst = qst.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getPst() {
        return pst;
    }

    public void setPst(BigDecimal pst) {
        this.pst = pst.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getTaxSubtotal() {
        return taxSubtotal;
    }

    public void setTaxSubtotal(BigDecimal taxSubtotal) {
        this.taxSubtotal = taxSubtotal.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getTotalCad() {
        return totalCad;
    }

    public void setTotalCad(BigDecimal totalCad) {
        this.totalCad = totalCad.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
