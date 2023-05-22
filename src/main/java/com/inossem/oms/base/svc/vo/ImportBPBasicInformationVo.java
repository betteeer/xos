package com.inossem.oms.base.svc.vo;

import com.inossem.oms.base.common.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ImportBPBasicInformationVo {

    @Excel(name = "Partner Name")
    public String partnerName;

    @Excel(name = "Tel")
    public String tel;

    @Excel(name = "Email")
    public String email;

    @Excel(name = "Contact")
    public String contact;
}
