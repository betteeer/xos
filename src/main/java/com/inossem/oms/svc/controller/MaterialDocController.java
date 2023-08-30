package com.inossem.oms.svc.controller;

import com.inossem.oms.base.svc.domain.MaterialDoc;
import com.inossem.oms.base.svc.vo.*;
import com.inossem.oms.svc.service.MaterialDocNewService;
import com.inossem.oms.svc.service.MaterialDocService;
import com.inossem.sco.common.core.exception.ServiceException;
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

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/svc/materialdoc")
@Slf4j
@Api(tags = {"物料凭证相关接口"})
@Validated
public class MaterialDocController extends BaseController {

    @Autowired
    private MaterialDocService materialDocService;
    @Resource
    private MaterialDocNewService materialDocNewService;
    @GetMapping("/list")
    @ApiOperation(value = "查询物料凭证列表",notes = "通过sku_code,sku_name,order_num,wareHourse_code分页查询物料凭证列表")
    public TableDataInfo list(@ApiParam(value = "库存列表请求参数") @Valid QueryMaterialDocListVo queryMaterialDocListVo) {
        startPage();
        List<QueryMaterialDocResVo> list = materialDocNewService.getDocList(queryMaterialDocListVo);
        return getDataTable(list);
    }
//    @GetMapping("/list1")
//    @ApiOperation(value = "查询物料凭证列表",notes = "通过sku_code,sku_name,order_num,wareHourse_code分页查询物料凭证列表")
//    public TableDataInfo list1(@ApiParam(value = "库存列表请求参数") @Valid QueryMaterialDocListVo queryMaterialDocListVo) {
//        startPage();
//        List<QueryMaterialDocResVo> list = materialDocService.list(queryMaterialDocListVo);
//        return getDataTable(list);
//    }

    @ApiOperation(value = "创建订单",
            notes="通过json数据创建对象，输入json")
    @PostMapping("/add")
    public AjaxResult<MaterialDoc> add(@RequestBody @Valid CreateMaterialDocVo createMaterialDocVo) throws ServiceException {
        List<MaterialDoc> materialDoc =  materialDocService.add(createMaterialDocVo);
        return AjaxResult.success().withData(materialDoc);
    }

    @GetMapping("/getunreversed/{docNumber}/{companyCode}")
    @ApiOperation(value = "查询未回退的物料凭证列表",notes = "通过docNum查询物料凭证")
    public AjaxResult<QueryUnReversedResVo> getUnReversedByDocNumber(@PathVariable("docNumber") String docNumber, @PathVariable("companyCode") String companyCode) {
        QueryUnReversedResVo queryUnReversedResVo = materialDocService.getUnReversedByDocNumber(docNumber,companyCode);
        return AjaxResult.success().withData(queryUnReversedResVo);
    }

    @PostMapping("/reverse")
    @ApiOperation(value = "根据传入的物料凭证进行反向操作",notes = "通过docNum查询物料凭证")
    public TableDataInfo reverseMaterialDoc(@RequestBody @Valid ReversedMaterialDocVO reversedMaterialDocVO) {
        List<MaterialDoc> materialDocList = materialDocService.reverseMaterialDoc(reversedMaterialDocVO);
        return getDataTable(materialDocList);
    }
    @PostMapping("/saveNote")
    @ApiOperation(value = "根据传入的物料凭证进行反向操作",notes = "通过docNum查询物料凭证")
    public TableDataInfo saveNote(@RequestBody @Valid ReversedMaterialDocVO reversedMaterialDocVO) {
        List<MaterialDoc> materialDocList = materialDocNewService.saveNote(reversedMaterialDocVO);
        return getDataTable(materialDocList);
    }

    @GetMapping("/test")
    @ApiOperation(value = "查询未回退的物料凭证列表",notes = "通过docNum查询物料凭证")
    public AjaxResult test() {
        throw new ServiceException("我是异常！！！！");
    }
}
