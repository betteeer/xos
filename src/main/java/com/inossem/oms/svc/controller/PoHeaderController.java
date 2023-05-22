package com.inossem.oms.svc.controller;

import com.alibaba.druid.util.StringUtils;
import com.inossem.oms.base.svc.domain.DeliveryHeader;
import com.inossem.oms.base.svc.domain.PoHeader;
import com.inossem.oms.base.svc.domain.VO.*;
import com.inossem.oms.svc.service.IDeliveryHeaderService;
import com.inossem.oms.svc.service.PoHeaderService;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 【采购订单】Controller
 *
 * @author shigf
 * @date 2022-11-04
 */
@RestController
@RequestMapping("/svc/po/header")
@Slf4j
public class PoHeaderController extends BaseController {
    @Resource
    private PoHeaderService poHeaderService;

    @Resource
    private IDeliveryHeaderService deliveryService;

    /**
     * 查询【采购订单】列表
     */
    @GetMapping("/list")
    public TableDataInfo list(PoListVo po) {
        if (StringUtils.isEmpty(po.getCompanyCode())) {
            return getDataTable(new ArrayList<>());
        }
        startPage();
        List<PoHeader> list = poHeaderService.selectPoHeaderList(po);
        return getDataTable(list);
    }

    /**
     * 获取【采购订单】详细信息
     */
    @GetMapping(value = "/{company_code}/{po_number}")
    public AjaxResult getInfo(@PathVariable("company_code") String companyCode,
                              @PathVariable("po_number") String poNumber) {
        return AjaxResult.success(poHeaderService.details(companyCode, poNumber, "po"));
    }


    /**
     * 获取【开票订单】详细信息
     */
    @GetMapping(value = "/getInvoiceInfo/{company_code}/{po_number}")
    public AjaxResult getInvoiceInfo(@PathVariable("company_code") String companyCode,
                              @PathVariable("po_number") String poNumber) {
        return AjaxResult.success(poHeaderService.details(companyCode, poNumber, "invoice"));
    }

    /**
     * 新增【采购订单】
     */
    @PostMapping("/create")
    public AjaxResult<PoHeader> create(@RequestBody PoSaveVo po) {
        return AjaxResult.success().withData(poHeaderService.create(po));
    }

    /**
     * 修改【采购订单】
     */
    @PostMapping("/modify")
    public AjaxResult<PoHeader> modify(@RequestBody PoSaveVo po) {
        return AjaxResult.success().withData(poHeaderService.modify(po));
    }


    /**
     * create po delivery
     * <p>
     *
     * @param deliveryInfoVo
     * @return
     */
    @ApiOperation(value = "create po delivery ", notes = "创建PO Delivery")
    @PostMapping("/createDelivery")
    public AjaxResult<DeliveryHeader> createDelivery(@RequestBody DeliveryInfoVo deliveryInfoVo) {
        return AjaxResult.success().withData(deliveryService.createPoDelivery(deliveryInfoVo));
    }

    /**
     * modify po delivery
     * <p>
     *
     * @param
     * @return
     */
    @ApiOperation(value = "modify po delivery ", notes = "修改PO Delivery")
    @PostMapping("/modifyDelivery")
    public AjaxResult<DeliveryHeader> modifyDelivery(@RequestBody DeliveryInfoVo deliveryInfoVo) {
        return AjaxResult.success().withData(deliveryService.modifyPoDelivery(deliveryInfoVo));
    }

    /**
     * get po delivery list
     *
     * @param poDeliveryedListQuery
     * @return
     */
    @ApiOperation(value = "get delivery list", notes = "收货报表查询")
    @GetMapping("/deliveryList")
    public TableDataInfo deliveryList(PoDeliveryedListQuery poDeliveryedListQuery) {
        if (StringUtils.isEmpty(poDeliveryedListQuery.getCompanyCode())) {
            return getDataTable(new ArrayList<>());
        }
        startPage();
        List<PoDeliveryedListResp> poDeliveryedListResps = deliveryService.selectPoDeliveryList(poDeliveryedListQuery);
        return getDataTable(poDeliveryedListResps);
    }

    /**
     * by poNumber get shipped info card
     * <p>
     *
     * @param poNumber
     * @return
     */
    @ApiOperation(value = "by poNumber get shipped info card", notes = "通过poNumber获取发运信息卡片")
    @GetMapping("/shipped/{poNumber}/{companyCode}")
    public AjaxResult poShipped(@PathVariable("poNumber") String poNumber, @PathVariable("companyCode") String companyCode) {
        if (StringUtils.isEmpty(poNumber)
                || StringUtils.isEmpty(companyCode)) {
            return AjaxResult.error("params error");
        }
        return AjaxResult.success().withData(deliveryService.poShipped(poNumber, companyCode));
    }

    /**
     * by soNumber & company get so order header info
     * <p>
     * use delivery create
     * <p>
     *
     * @param poNumber
     * @return
     */
    @ApiOperation(value = "by poNumber & company  get po order header info", notes = "获取po order header信息,用于创建delivery使用")
    @GetMapping("/searchOrderHeader/{poNumber}/{companyCode}")
    public AjaxResult<OrderHeaderResp> getOrderHeader(@PathVariable("poNumber") String poNumber, @PathVariable("companyCode") String companyCode) {
        if (StringUtils.isEmpty(poNumber)) {
            return AjaxResult.error("参数校验异常");
        }

        if (StringUtils.isEmpty(companyCode)) {
            return AjaxResult.error("参数校验异常");
        }
        return AjaxResult.success().withData(deliveryService.getPoOrderHeader(poNumber, companyCode));
    }


    /**
     * update service so status
     * 针对SEPO   修改Po的状态
     * <p>
     *
     * @param
     * @return
     */
    @ApiOperation(value = "update service po status", notes = "修改service po的order status")
    @GetMapping("/update_sepo_status/{poNumber}/{companyCode}")
    public AjaxResult updateStatus(@PathVariable("poNumber") String poNumber,@PathVariable("companyCode") String companyCode) {
        return toAjax(poHeaderService.updateStatus(poNumber,companyCode));
    }

}
