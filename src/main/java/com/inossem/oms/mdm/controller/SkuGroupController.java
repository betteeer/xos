package com.inossem.oms.mdm.controller;

import com.inossem.oms.base.svc.domain.DTO.SkuGroupFormDTO;
import com.inossem.oms.base.svc.domain.SkuGroup;
import com.inossem.oms.mdm.service.SkuGroupService;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/mdm/sku_group")
public class SkuGroupController extends BaseController {

    @Resource
    private SkuGroupService skuGroupService;

    @ApiOperation(value = "sku group list", notes = "sku group list")
    @GetMapping("/list")
    public AjaxResult getList(
            @RequestParam("companyCode") String companyCode,
            @RequestParam(name = "onlyEnabled", defaultValue = "false") Boolean onlyEnabled
            ) {
        return AjaxResult.success().withData(skuGroupService.getList(companyCode, onlyEnabled));
    }

    /**
     * 批量操作，可新增 可修改
     * @return
     */
    @ApiOperation(value = "create/modify sku group", notes = "create/modify sku group")
    @PostMapping("/batch")
    public AjaxResult batchOperation(@RequestBody() SkuGroupFormDTO formDTO) {
        List<SkuGroup> addItems = formDTO.getAddItems();
        List<SkuGroup> modifyItems = formDTO.getModifyItems();
        String companyCode = formDTO.getCompanyCode();
        if (StringUtils.isEmpty(companyCode)) {
            return AjaxResult.error("missing companyCode");
        }
        //兜底为null的情况
        if (addItems == null) addItems = new ArrayList<>();
        if (modifyItems == null) modifyItems = new ArrayList<>();
        return AjaxResult.success().withData(skuGroupService.batch(addItems, modifyItems, companyCode));
    }
}
