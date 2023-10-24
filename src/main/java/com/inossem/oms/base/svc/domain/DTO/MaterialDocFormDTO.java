package com.inossem.oms.base.svc.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
@Data
public class MaterialDocFormDTO {
    @NotBlank(message = "company code cannot be empty")
    private String companyCode;
    private String searchText;

    // 见文档movement type那张图，后端返回数据只有类型数字，需要前端自行渲染对应文本
    private List<String> transactionType;

    private List<String> warehouseCode;

    private List<String> referenceType;

    // A: Available
    // B: Blocked
    // T: Intransit(for sprint2)
    // F: Frozen(for sprint2)
    // I: Inspection(for sprint2)
    private List<String> stockStatus;

    private Date postingDateStart;
    private Date postingDateEnd;

    private BigDecimal quantityStart;
    private BigDecimal quantityEnd;

    private BigDecimal totalAmountStart;
    private BigDecimal totalAmountEnd;

    @Pattern(regexp = "^docNumber$", message = "order by should within docNumber")
    private String orderBy;
    private Boolean isAsc = true;
}
