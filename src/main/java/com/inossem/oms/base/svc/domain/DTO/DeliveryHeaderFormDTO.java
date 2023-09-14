package com.inossem.oms.base.svc.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

@Data
public class DeliveryHeaderFormDTO {
    @NotBlank(message = "company code cannot be empty")
    private String companyCode;
    private String searchText;

    private List<String> status;

    private Date deliveryDateStart;
    private Date deliveryDateEnd;
    private Date postDateStart;
    private Date postDateEnd;

    private List<String> warehouseCode;

    private List<String> carrierCode;
    @Pattern(regexp = "^deliveryNumber|deliveryDate|postingDate$", message = "order by should within deliveryNumber,deliveryDate,postingDate")
    private String orderBy;
    private Boolean isAsc = true;

}
