package com.inossem.oms.base.svc.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

@Data
public class StoSearchFormDTO {
    @NotBlank(message = "company code cannot be empty")
    private String companyCode;
    private String searchText;
    private List<String> status;
    private List<String> fromWarehouse;
    private List<String> toWarehouse;
    private Date createDateStart;
    private Date createDateEnd;
    private Date shipoutDateStart;
    private Date shipoutDateEnd;
    private Date receiveDateStart;
    private Date receiveDateEnd;
    @Pattern(regexp = "^stoNumber|createDate|shipoutDate|receiveDate$", message = "order by should within stoNumber, createDate, shipoutDate, receiveDate")
    private String orderBy;
    private Boolean isAsc = true;
}
