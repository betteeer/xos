package com.inossem.oms.common.controller;

import com.inossem.oms.base.common.domain.PaymentTerm;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import com.inossem.oms.common.service.PaymentTermService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 【请填写功能名称】Controller
 * 
 * @author shigf
 * @date 2022-11-04
 */
@RestController
@RequestMapping("/common/common/term")
@Api(tags = {"paymentTerm字典相关接口"})
public class PaymentTermController extends BaseController
{
    @Autowired
    private PaymentTermService paymentTermService;

    /**
     * 查询【请填写功能名称】列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "paymentTerm字典列表", notes = "paymentTerm字典列表")
    public TableDataInfo list(PaymentTerm paymentTerm)
    {
        startPage();
        List<PaymentTerm> list = paymentTermService.selectPaymentTermList(paymentTerm);
        return getDataTable(list);
    }

}
