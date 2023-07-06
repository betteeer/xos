package com.inossem.oms.svc.controller;


import com.alibaba.druid.util.StringUtils;
import com.inossem.oms.base.svc.domain.DTO.WarehouseStockFormDTO;
import com.inossem.oms.base.svc.domain.VO.SimpleStockBalanceVo;
import com.inossem.oms.base.svc.vo.*;
import com.inossem.oms.base.utils.poi.ExcelUtil;
import com.inossem.oms.svc.service.StockBalanceNewService;
import com.inossem.oms.svc.service.StockBalanceService;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/svc/stock")
@Slf4j
@Api(tags = {"库存相关接口"})
@Validated
public class StockBalanceController extends BaseController {

    @Autowired
    private StockBalanceService stockBalanceService;

    @Autowired
    private StockBalanceNewService stockBalanceNewService;
    @GetMapping("/list")
    @ApiOperation(value = "查询库存列表", notes = "通过sku_code,sku_name,wareHourse_code分页查询库存列表")
    public TableDataInfo list(@ApiParam(value = "库存列表请求参数") QueryStockListVo queryStockListVo) {
        if (StringUtils.isEmpty(queryStockListVo.getCompanyCode())) {
            return getDataTable(new ArrayList<>());
        }
        startPage();
        List<QueryStockBalanceResVo> list = stockBalanceService.list(queryStockListVo);
        return getDataTable(list);
    }


    /**
     * 库存报警查询 (sku低于安全库存的库存列表)
     *
     * @param
     * @return
     */
    @GetMapping("/satety_list")
    @ApiOperation(value = "查询低于sku设置的安全库存-库存列表", notes = "查询低于sku设置的安全库存-库存列表")
    public TableDataInfo satetyList(@ApiParam(value = "库存报警查询") QueryStockListVo queryStockListVo) {
        if (StringUtils.isEmpty(queryStockListVo.getCompanyCode())) {
            return getDataTable(new ArrayList<>());
        }
        List<QueryStockBalanceResVo> list = stockBalanceService.satetyList(queryStockListVo);
        return getDataTable(list);
    }


    @GetMapping("/getbysku")
    @ApiOperation(value = "根据sku查询库存请求参数", notes = "根据sku查询库存请求参数")
    public AjaxResult<QueryStockBalanceResVo> getBySku(@ApiParam(value = "根据sku查询库存请求参数") QueryStockBySkuVo queryStockListVo) {
        QueryStockBalanceResVo queryStockBalanceResVo = stockBalanceService.getBySku(queryStockListVo);
        return AjaxResult.success().withData(queryStockBalanceResVo);
    }

    @PostMapping("/checkstock")
    @ApiOperation(value = "校验库存是否充足", notes = "校验库存是否充足")
    public AjaxResult<CheckStockBalanceResVo> checkStock(@ApiParam(value = "库存列表请求参数") @RequestBody CheckStockBalanceParamVo checkStockBalanceParamVo) {
        CheckStockBalanceResVo queryStockBalanceResVo = stockBalanceService.checkStock(checkStockBalanceParamVo);
        return AjaxResult.success().withData(queryStockBalanceResVo);
    }

    /**
     * 导入库存列表
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入库存列表", notes = "导入库存列表")
    public AjaxResult export(MultipartFile file, @RequestParam("companyCode") @NotBlank String companyCode) throws Exception {
        ExcelUtil<ImportStockBalanceVo> util = new ExcelUtil<>(ImportStockBalanceVo.class);
        List<ImportStockBalanceVo> importStockBalanceVoList = util.importExcel(file.getInputStream());
        String message = stockBalanceService.importExcel(importStockBalanceVoList, companyCode);
        return AjaxResult.success(message);
    }


    @GetMapping("/checkStockWithoutWH")
    @ApiOperation(value = "校验商品库存是否充足，不区分warehourse", notes = "校验商品库存是否充足，不区分warehourse")
    public AjaxResult<Boolean> checkStockWithoutWH(@ApiParam(value = "库存列表请求参数") @RequestParam String skuNumber, @RequestParam BigDecimal useQty, @RequestParam String companyCode) {
        boolean isAdequate = stockBalanceService.checkStockWithoutWH(skuNumber, useQty, companyCode);
        return AjaxResult.success().withData(isAdequate);
    }


    @PostMapping("/getStockInWarehouses")
//    @ApiOperation(value = "", notes = "")
    public AjaxResult<List<SimpleStockBalanceVo>> getStockInWarehouse(@RequestBody() @Validated WarehouseStockFormDTO w) {
        return AjaxResult.success().withData(
                stockBalanceNewService.getSkuStockInWarehouse(
                        w.getSkuNumbers(),
                        w.getWarehouseCodes(),
                        w.getCompanyCode()));
    }

}
