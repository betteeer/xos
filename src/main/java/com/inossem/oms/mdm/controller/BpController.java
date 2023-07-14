package com.inossem.oms.mdm.controller;

import com.inossem.oms.base.svc.domain.BusinessPartner;
import com.inossem.oms.base.svc.domain.VO.BPListVO;
import com.inossem.oms.mdm.service.BpService;
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
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.Objects;

/**
 * @author zoutong
 * @date 2022/10/17
 **/
@RestController
@RequestMapping("/mdm/bp")
@Slf4j
@Api(tags = {"bp op"})
public class BpController extends BaseController {

    @Resource
    private BpService bpService;

    @ApiOperation(value = "create bp", notes = "create bp")
    @PostMapping("/create")
    public AjaxResult create(@Valid @RequestBody BusinessPartner businessPartner, BindingResult result) {
        if (result.hasErrors()) {
            return AjaxResult.error(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return AjaxResult.success("create success").withData(bpService.create(businessPartner));
    }

    @ApiOperation(value = "modify bp", notes = "modify bp")
    @PostMapping("/modify")
    public AjaxResult modify(@Valid @RequestBody BusinessPartner businessPartner, BindingResult result) {
        if (result.hasErrors()) {
            return AjaxResult.error(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return AjaxResult.success("modify success").withData(bpService.modify(businessPartner));
    }

    @ApiOperation(value = "get bp list", notes = "get bp page list")
    @GetMapping("/list")
    public TableDataInfo list(BPListVO bpListVO) {
        return bpService.getList(bpListVO);
    }

    @ApiOperation(value = "get bp list", notes = "get bp page list")
    @PostMapping("/wms/list")
    public TableDataInfo listWms(@RequestBody BPListVO bpListVO) {
        return bpService.getList(bpListVO);
    }

    @ApiOperation(value = "get bp list", notes = "get bp page list")
    @GetMapping("/bkList")
    public AjaxResult bkList(String companyCode, String name) throws IOException {
        return AjaxResult.success(bpService.getBkList(companyCode, name));
    }

    @ApiOperation(value = "get one bp detail", notes = "get one bp detail")
    @GetMapping("/get/{bpNumber}")
    public AjaxResult getBp(@PathVariable("bpNumber") String bpNumber, @RequestParam String companyCode) {
        return AjaxResult.success("some-information").withData(bpService.getBp(bpNumber, companyCode));
    }

    @PostMapping("/import")
    @ApiOperation(value = "导入BP", notes = "导入BP")
    public AjaxResult importBP(MultipartFile file, @RequestParam("companyCode") @NotBlank String companyCode) {
        bpService.importBP(file, companyCode);
        return AjaxResult.success();
    }
}
