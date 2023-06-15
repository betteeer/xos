package com.inossem.oms.api.bk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxContent {
    private String alias;
    private String category;
    private String categoryName;
    private String fieldName;
    private BigDecimal value;
}
