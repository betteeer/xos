package com.inossem.oms.svc.controller;

import com.alibaba.druid.util.StringUtils;
import com.inossem.oms.base.svc.domain.DTO.SoHeaderSearchForm;
import com.inossem.oms.base.svc.domain.DeliveryHeader;
import com.inossem.oms.base.svc.domain.SoHeader;
import com.inossem.oms.base.svc.domain.VO.*;
import com.inossem.oms.svc.service.SoHeaderNewService;
import com.inossem.sco.common.core.domain.R;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import com.inossem.oms.svc.service.IDeliveryHeaderService;
import com.inossem.oms.svc.service.ISoHeaderService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Selling Controller
 * <p>create_delivery
 * incloud sales order list & delivery list
 */
@RestController
@RequestMapping("/svc/so")
@Slf4j
public class SoHeaderController extends BaseController {

    @Resource
    private ISoHeaderService soHeaderService;
    @Resource
    private SoHeaderNewService soHeaderNewService;

    @Resource
    private IDeliveryHeaderService deliveryService;

    /**
     * get sales order list  so列表
     *
     * @param salesOrderListQyery
     * @return
     */
    @ApiOperation(value = "get sales order list", notes = "分页查询,获取sales order list")
    @GetMapping("/list")
    public TableDataInfo list(SalesOrderListQyery salesOrderListQyery) {
        if (StringUtils.isEmpty(salesOrderListQyery.getCompanyCode())) {
            return getDataTable(new ArrayList<>());
        }
        startPage();
        List<SoHeader> list = soHeaderService.selectSoHeaderList(salesOrderListQyery);
        return getDataTable(list);
    }

    /**
     * 创建销售订单
     * <p>
     * 【创建订单】 --> 发运 --> 开票
     * create service sales order
     * create inventory sales order
     * create drop-ship sales order
     *
     * @param soOrderHeaderInfoVo
     * @return
     */
    @ApiOperation(value = "create so order", notes = "创建销售订单")
    @PostMapping("/create")
    public AjaxResult<SoHeader> create(@RequestHeader(name = "X-Userid") String userId, @RequestBody SoOrderHeaderInfoVo soOrderHeaderInfoVo) {
        soOrderHeaderInfoVo.setUserId(userId);
        return AjaxResult.success().withData(soHeaderService.create(soOrderHeaderInfoVo));
    }

    /**
     * 修改销售订单
     * <p>
     * 在发运之前可以任意修改订单信息，包括Inventory Order， Service Order and Drop-ship Order
     *
     * @param soOrderHeaderInfoVo
     * @return
     */
    @ApiOperation(value = "modify so order", notes = "发运前修改So信息")
    @PostMapping("/modify")
    public AjaxResult<SoHeader> modify(@RequestHeader(name = "X-Userid") String userId, @RequestBody SoOrderHeaderInfoVo soOrderHeaderInfoVo) {
        soOrderHeaderInfoVo.setUserId(userId);
        return AjaxResult.success().withData(soHeaderService.modify(soOrderHeaderInfoVo));
    }

    /**
     * 查询 so order header的明细  用于前端展示
     * <p>
     * 发运：
     * * 0.回显订单信息 √
     * * 1.查可发运数量
     * * 2.查已发运列表
     * * 3.发运
     *
     * @param soNumber
     * @param companyCode
     * @return
     */
    @ApiOperation(value = "by soNumber & company  get so order header info", notes = "获取so order header信息,用于so回显")
    @GetMapping("/details_order_header/{so_number}/{company_code}")
    public AjaxResult<Map<String, Object>> orderHeaderDetails(
            @PathVariable("so_number") String soNumber,
            @PathVariable("company_code") String companyCode) {
        if (StringUtils.isEmpty(soNumber)) {
            return AjaxResult.error("参数校验异常");
        }
        if (StringUtils.isEmpty(companyCode)) {
            return AjaxResult.error("参数校验异常");
        }
        return AjaxResult.success().withData(soHeaderService.details(companyCode, soNumber));
    }

    /**
     * 查询so的信息，可用于inventory so、service so 和 drop-ship so
     * <p>
     * 1、找到这条销售订单
     * 2、查这个订单中的sku，还有多少可以发运
     * <p>
     * 组合产品，在保存到delivery中的时候，保存的是item，并有字段标识了kitting的id
     * 组合产品，在保存到so中的时候，保存的是kitting，没有保存item
     * <p>
     * 对于组合产品，在计算还有多少数量的时候，就比较麻烦
     * <p>
     * 发运：
     * 0.回显订单信息
     * 1.查可发运数量  √
     * 2.查已发运列表
     * 3.发运
     *
     * @param soNumber
     * @return
     */
    @ApiOperation(value = "by soNumber & company  get so order header info", notes = "获取so order header信息,用于创建delivery使用")
    @GetMapping("/search_order_header/{so_number}/{company_code}")
    public AjaxResult<OrderHeaderResp> getOrderHeader(@PathVariable("so_number") String soNumber,
                                                      @PathVariable("company_code") String companyCode) {
        if (StringUtils.isEmpty(soNumber)) {
            return AjaxResult.error("参数校验异常");
        }
        if (StringUtils.isEmpty(companyCode)) {
            return AjaxResult.error("参数校验异常");
        }
        return AjaxResult.success().withData(deliveryService.getOrderHeader(soNumber, companyCode));
    }

    /**
     * by soNumber get shipped info card  查询发运的订单
     * <p>
     * 发运：
     * * 0.回显订单信息
     * * 1.查可发运数量
     * * 2.查已发运列表  √
     * * 3.发运
     *
     * @param soNumber
     * @return
     */
    @ApiOperation(value = "by soNumber get shipped info card", notes = "通过soNumber获取发运信息卡片")
    @GetMapping("/shipped/{so_number}/{company_code}")
    public AjaxResult shipped(@PathVariable("so_number") String soNumber, @PathVariable("company_code") String companyCode) {
        if (StringUtils.isEmpty(soNumber)
                || StringUtils.isEmpty(companyCode)) {
            return AjaxResult.error("params error");
        }
        return AjaxResult.success().withData(deliveryService.shipped(soNumber, companyCode));
    }

    /**
     * create so delivery  创建so delivery
     *
     * <p>
     * 发运：
     * 0.回显订单信息
     * 1.查可发运数量
     * 2.查已发运列表
     * 3.发运 √
     *
     * @param deliveryInfoVo
     * @return
     */
    @ApiOperation(value = "create so delivery ", notes = "销售订单发运")
    @PostMapping("/create_delivery")
    public AjaxResult<DeliveryHeader> createDelivery(@RequestHeader(name="X-Userid") String userId, @RequestBody DeliveryInfoVo deliveryInfoVo) {

        String deliveryType = deliveryInfoVo.getDeliveryType();
        if (org.apache.commons.lang3.StringUtils.isBlank(deliveryType)) {
            throw new IllegalArgumentException("deliveryType 为必填参数");
        }

        return AjaxResult.success().withData(deliveryService.createDelivery(deliveryInfoVo, userId));
    }

    /**
     * 针对空发运的单子进行发运
     * <p>
     * 1、修改发运单的信息
     * 2、同步修改库存 ==> 如果是service so，则不修改库存
     *
     * @param
     * @return
     */
    @ApiOperation(value = "modify so delivery ", notes = "修改Delivery")
    @PostMapping("/modify_delivery")
    public AjaxResult<DeliveryHeader> modifyDelivery(@RequestHeader(name="X-Userid") String userId, @RequestBody DeliveryInfoVo deliveryInfoVo) {
        return AjaxResult.success().withData(deliveryService.modifyDelivery(deliveryInfoVo, userId));
    }


    /**
     * get delivery list  发运报表
     *
     * @param deliveryedListQueryVo
     * @return
     */
    @ApiOperation(value = "get delivery list", notes = "发运报表查询")
    @GetMapping("/delivery_list")
    public TableDataInfo deliveryList(DeliveryedListQuery deliveryedListQueryVo) {
        if (StringUtils.isEmpty(deliveryedListQueryVo.getCompanyCode())) {
            return getDataTable(new ArrayList<>());
        }
        startPage();
        List<DeliveryedListResp> deliveryedListResps = deliveryService.selectDeliveryList(deliveryedListQueryVo);
        return getDataTable(deliveryedListResps);
    }

    /**
     * 通过companyCode/ bpCode / 及bpName 更新  soHeader中的bpName
     *
     * @return
     */
    @Deprecated
    @ApiOperation(value = "update so order header bpName", notes = "更新soHeader中的bpName")
    @GetMapping("/updateBpName/{company_code}/{bp_number}/{bp_name}")
    public R<SoHeader> updateBpName(
            @PathVariable("company_code") String companyCode,
            @PathVariable("bp_number") String bpNumber,
            @PathVariable("bp_name") String bpName
    ) {
        soHeaderService.updateBpName(companyCode, bpNumber, bpName);
        return R.ok();
    }

    /**
     * modify so shipped delivery  针对已经发运的delivery单子进行修改操作
     * <p>
     *
     * @param
     * @return
     */
    @ApiOperation(value = "modify so shipped delivery ", notes = "修改已经发运的Delivery订单")
    @PostMapping("/modify_shipped_delivery")
    public AjaxResult<DeliveryHeader> modifyShippedDelivery(@RequestHeader(name="X-Userid") String userId,@RequestBody DeliveryInfoVo deliveryInfoVo) {
        return AjaxResult.success().withData(deliveryService.modifyShippedDelivery(deliveryInfoVo, userId));
    }

    /**
     * update service so status
     * 针对SESO   修改so的状态
     * <p>
     *
     * @param
     * @return
     */
    @ApiOperation(value = "update service so status", notes = "修改service so的order status")
    @GetMapping("/update_seso_status/{soNumber}/{companyCode}")
    public AjaxResult updateStatus(@PathVariable("soNumber") String soNumber, @PathVariable("companyCode") String companyCode) {
        return toAjax(soHeaderService.updateStatus(soNumber, companyCode));
    }

    @PostMapping("/header/list")
    public TableDataInfo list(@RequestBody @Validated SoHeaderSearchForm form) {
        startPage();
        return getDataTable(soHeaderNewService.getNewList(form));
    }

    @GetMapping("/block/reason")
    public AjaxResult blockReason(@RequestParam String type){
        return AjaxResult.success().withData(soHeaderNewService.getBlockReason(type));
    }
}
