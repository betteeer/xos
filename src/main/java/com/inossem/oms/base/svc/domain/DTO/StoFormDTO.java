package com.inossem.oms.base.svc.domain.DTO;

import com.inossem.oms.base.svc.domain.StoHeader;
import com.inossem.oms.base.svc.domain.StoItem;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class StoFormDTO extends StoHeader {

    @NotBlank(message = "companyCode cannot be empty")
    private String companyCode;

    @NotBlank(message = "fromWarehouseCode cannot be empty")
    private String fromWarehouseCode;

    @NotBlank(message = "toWarehouseCode cannot be empty")
    private String toWarehouseCode;

    @NotEmpty(message = "items cannot be empty")
    private List<StoItem> items;
}
