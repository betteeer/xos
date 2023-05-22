package com.inossem.oms.mdm.controller;

import com.inossem.oms.base.svc.domain.BusinessPartner;
import com.inossem.oms.base.svc.domain.VO.SkuVO;
import com.inossem.oms.mdm.service.WmsApiService;
import com.inossem.sco.common.core.utils.StringUtils;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author guoh
 * @date 2022/12/27
 **/

@RestController
@RequestMapping("/mdm/wms/api/")
@Slf4j
@Api(tags = {"wms sku op"})
public class WmsApiController extends BaseController {

    @Resource
    private WmsApiService wmsApiService;

    @ApiOperation(value = "wms create sku", notes = "wms create sku")
    @PostMapping("/sku/create")
    public AjaxResult create(@RequestBody SkuVO skuVO) {
        log.info("wms创建sku,接收到的参数为:{}", skuVO);
        StringBuffer sb = checkSkuParams(skuVO);
        if (sb.length() > 0) {
            return AjaxResult.error("创建sku错误,必填参数校验失败," + sb + "为必填项");
        }
        return AjaxResult.success("create success").withData(wmsApiService.createSku(skuVO));
    }

    @ApiOperation(value = "wms modify sku", notes = "wms modify sku")
    @PostMapping("/sku/modify")
    public AjaxResult modifySku(@RequestBody SkuVO skuVO) {
        log.info("wms创建sku,接收到的参数为:{}", skuVO);
        StringBuffer sb = checkSkuParams(skuVO);
        if (sb.length() > 0) {
            return AjaxResult.error("修改sku错误,必填参数校验失败," + sb + "为必填项");
        }
        return AjaxResult.success("modify success").withData(wmsApiService.modifySku(skuVO));
    }

    @ApiOperation(value = "wms createOrModify sku", notes = "wms createOrModify sku")
    @PostMapping("/sku/createOrModify")
    public AjaxResult createOrModifySku(@RequestBody SkuVO skuVO) {
        log.info("sku createOrModify,接收到的参数为:{}", skuVO);
        StringBuffer sb = checkSkuParams(skuVO);
        if (sb.length() > 0) {
            return AjaxResult.error("sku createOrModify错误,必填参数校验失败," + sb + "为必填项");
        }
        return AjaxResult.success("modify success").withData(wmsApiService.createOrModifySku(skuVO));
    }

    /**
     * 创建 , 修改 sku必填参数校验
     *
     * @param skuVO
     * @return
     * @type 0-新增   1-修改
     */
    private StringBuffer checkSkuParams(SkuVO skuVO) {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isEmpty(skuVO.getCompanyCode())) {
            sb.append("[CompanyCode]");
        }
        if (StringUtils.isEmpty(skuVO.getSkuName())) {
            sb.append("[SkuName]");
        }
        if (StringUtils.isEmpty(skuVO.getSkuType())) {
            sb.append("[SkuType]");
        }
        if (StringUtils.isEmpty(skuVO.getBasicUom())) {
            sb.append("[BasicUom]");
        }
        if (null == String.valueOf(skuVO.getIsKitting())) {
            sb.append("[IsKitting]");
        }
        if ("" == String.valueOf(skuVO.getIsKitting())) {
            sb.append("[IsKitting]");
        }
        return sb;
    }

    @ApiOperation(value = "create bp", notes = "create bp")
    @PostMapping("/bp/create")
    public AjaxResult create(@RequestBody BusinessPartner businessPartner) {
        log.info("wms创建bp,接收到的参数为:{}", businessPartner);
        StringBuffer sb = checkBpParams(businessPartner);
        if (sb.length() > 0) {
            return AjaxResult.error("新增bp错误,必填参数校验失败," + sb + "为必填项");
        }
        return AjaxResult.success("create success").withData(wmsApiService.createBp(businessPartner));
    }

    @ApiOperation(value = "modify bp", notes = "modify bp")
    @PostMapping("/bp/modify")
    public AjaxResult modify(@RequestBody BusinessPartner businessPartner) {
        log.info("wms修改bp,接收到的参数为:{}", businessPartner);
        StringBuffer sb = checkBpParams(businessPartner);
        if (sb.length() > 0) {
            return AjaxResult.error("修改Bp错误,必填参数校验失败," + sb + "为必填项");
        }
        return AjaxResult.success("modify success").withData(wmsApiService.modifyBp(businessPartner));
    }

    /**
     * 不存在就创建，存在就更新
     *
     * @param businessPartner
     * @return
     */
    @ApiOperation(value = "createOrModify bp", notes = "createOrModify bp")
    @PostMapping("/bp/createOrModify")
    public AjaxResult createOrModify(@RequestBody BusinessPartner businessPartner) {
        log.info("bp createOrModify,接收到的参数为:{}", businessPartner);
        StringBuffer sb = checkBpParams(businessPartner);
        if (sb.length() > 0) {
            return AjaxResult.error("bp createOrModify错误,必填参数校验失败," + sb + "为必填项");
        }
        return AjaxResult.success("modify success").withData(wmsApiService.createOrModifyBp(businessPartner));
    }


    /**
     * 创建 , 修改 Bp必填参数校验
     *
     * @param businessPartner
     * @return
     * @type 0-新增   1-修改
     */
    private StringBuffer checkBpParams(BusinessPartner businessPartner) {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isEmpty(businessPartner.getCompanyCode())) {
            sb.append("[CompanyCode]");
        }
        if (StringUtils.isEmpty(businessPartner.getBpName())) {
            sb.append("[BpName]");
        }
        return sb;
    }


    @ApiOperation(value = "get company list", notes = "get company list")
    @GetMapping("/company/list")
    public AjaxResult list() {
        return AjaxResult.success().withData(wmsApiService.companyList());
    }

    @ApiOperation(value = "get company detail", notes = "get company detail")
    @GetMapping("/company/get/{code}")
    public AjaxResult getCompany(@PathVariable("code") String code) {
        return AjaxResult.success("some-information").withData(wmsApiService.getCompanyByCode(code));
    }
}
