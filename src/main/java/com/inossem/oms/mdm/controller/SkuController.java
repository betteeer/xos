package com.inossem.oms.mdm.controller;

import com.inossem.oms.base.svc.domain.SkuMaster;
import com.inossem.oms.base.svc.domain.VO.SimpleSkuMasterVO;
import com.inossem.oms.base.svc.domain.VO.SkuListReqVO;
import com.inossem.oms.base.svc.domain.VO.SkuVO;
import com.inossem.oms.mdm.service.SkuNewService;
import com.inossem.oms.mdm.service.SkuService;
import com.inossem.sco.common.core.domain.R;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author zoutong
 * @date 2022/10/15
 **/

@RestController
@RequestMapping("/mdm/sku")
@Slf4j
@Api(tags = {"sku op"})
public class SkuController extends BaseController {

    @Resource
    private SkuService service;

    @Resource
    private SkuNewService skuNewService;

    @ApiOperation(value = "create sku", notes = "create sku")
    @PostMapping("/create")
    public AjaxResult create(@RequestHeader(name = "X-Userid") String userId, @Valid @RequestBody SkuVO skuVO, BindingResult result) {
        if (result.hasErrors()) {
            return AjaxResult.error(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return AjaxResult.success("create success").withData(service.create(skuVO, userId));
    }

    @ApiOperation(value = "modify sku", notes = "modify sku")
    @PostMapping("/modify")
    public AjaxResult modifySku(@RequestHeader(name = "X-Userid") String userId, @Valid @RequestBody SkuVO skuVO, BindingResult result) {
        if (result.hasErrors()) {
            return AjaxResult.error(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return AjaxResult.success("modify success").withData(service.modifySku(skuVO, userId));
    }

    @ApiOperation(value = "get sku list", notes = "get sku page list")
    @GetMapping("/list")
    public TableDataInfo list(SkuListReqVO skuListReqVO) {
        return getDataTable(service.getList(skuListReqVO));
    }

    @ApiOperation(value = "get sku list", notes = "get sku page list")
    @PostMapping("/wms/list")
    public TableDataInfo wmsList(@RequestBody SkuListReqVO skuListReqVO) {
        return getDataTable(service.getList(skuListReqVO));
    }

    @ApiOperation(value = "get one sku detail", notes = "get one sku detail")
    @GetMapping("/get/{skuCode}/{companyCode}")
    public AjaxResult getSku(@PathVariable("skuCode") String skuCode,@PathVariable("companyCode")String companyCode) {
        return AjaxResult.success("some-information").withData(service.getSku(skuCode,"",companyCode));
    }

    @ApiOperation(value = "gallery upload picture", notes = "New Inventory SKU gallery upload picture")
    @PostMapping("/gallery/upload")
    public AjaxResult galleryUpload(String skuCode, MultipartFile[] pictures, HttpServletRequest req) {
        return AjaxResult.success("gallery picture").withData(service.galleryUpload(skuCode, pictures, req));
    }

    @ApiOperation(value = "gallery list picture", notes = "New Inventory SKU gallery list picture")
    @GetMapping("/gallery/list")
    public AjaxResult galleryList(String skuCode, HttpServletRequest req) {
        return AjaxResult.success("gallery list").withData(service.galleryList(skuCode, req));
    }

    @ApiOperation(value = "get sku list by name", notes = "get sku page list")
    @GetMapping("/list/{skuName}/{companyCode}")
    public R<List<SkuMaster>> listSkuName(@PathVariable("skuName") String skuName,@PathVariable("companyCode")String companyCode) {
        log.error("进到了根据name查询sku信息");
        List<SkuMaster> skuMasters = new ArrayList<>();
        SkuMaster skuMaster = new SkuMaster();
        skuMaster.setSkuName(skuName);
        skuMaster.setCompanyCode(companyCode);
        skuMaster.setBasicUom("chn");
        skuMasters.add(skuMaster);
        return R.ok(skuMasters);
    }

    @PostMapping("/upload")
    public AjaxResult<Map<String, Object>> uploadFile(
            @RequestParam("pictures") MultipartFile[] files) {
        return AjaxResult.success(service.upload(files));
    }

    /*@ApiOperation(value = "get one sku detail",notes = "Fegin get one sku detail")
    @GetMapping("/get/{skuCode}")
    public R<SkuMaster> getSkuForCall(@PathVariable("skuCode") String skuCode) {
        return R.ok(service.getSku(skuCode));
    }*/

    @PostMapping("/import")
    @ApiOperation(value = "导入SKU",notes = "导入SKU")
    public AjaxResult importSku(@RequestHeader(name = "X-Userid") String userId, MultipartFile file, @RequestParam("companyCode") @NotBlank String companyCode) {
        service.importSKU(file,companyCode, userId);
        return AjaxResult.success();
    }

    @GetMapping("/list/filterKitting")
    public TableDataInfo getListInWarehouse(@RequestParam("companyCode") @NotBlank String companyCode, String search, Integer pageSize) {
        if (pageSize != null && pageSize.intValue() != -1) {
            startPage();
        }
        List<SimpleSkuMasterVO> listFilterKitting = skuNewService.getListFilterKitting(companyCode, search);
        return getDataTable(listFilterKitting);
    }
}
