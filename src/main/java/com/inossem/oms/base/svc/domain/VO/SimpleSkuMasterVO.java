package com.inossem.oms.base.svc.domain.VO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleSkuMasterVO {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String skuName;

    private String skuNumber;

    private String basicUom;
}
