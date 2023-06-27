package com.inossem.oms.base.svc.domain.DTO;

import com.inossem.oms.base.svc.domain.SkuGroup;
import lombok.Data;

import java.util.List;

@Data
public class SkuGroupFormDTO {
    private List<SkuGroup> addItems;
    private List<SkuGroup> modifyItems;
    private String companyCode;
}
