package com.inossem.oms.base.svc.vo;

import com.inossem.oms.base.common.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ImportStockBalanceVo {

    @Excel(name = "posting_date",dateFormat = "yyyy/MM/dd")
    private Date postingDate;

    @Excel(name = "warehouse_code")
    private String warehouseCode;

    @Excel(name = "stock_status",readConverterExp = "A=Avaliable,B=Blocked,T=intransit(for sprint2),F=frozen(for sprint2),I=inspection(for sprint2)")
    private String stockStatus;

    @Excel(name = "sku_number")
    private String skuNumber;

    @Excel(name = "sku_qty")
    private BigDecimal skuQty;

    @Excel(name = "total_amount")
    private BigDecimal totalAmount;

    @Excel(name = "basic_uom")
    private String basicUom;

    @Excel(name = "currency_code")
    private String currencyCode;

}
