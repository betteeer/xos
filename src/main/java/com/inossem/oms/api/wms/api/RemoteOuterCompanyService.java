package com.inossem.oms.api.wms.api;

import com.inossem.oms.base.common.constant.ServiceNameConstants;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import com.inossem.oms.api.wms.factory.RemoteOuterCompanyFallbackFactory;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 提供给外部调用的BP服务
 */
@FeignClient(contextId = "remoteOuterCompanyService", value = ServiceNameConstants.MDM_SERVICE, fallbackFactory = RemoteOuterCompanyFallbackFactory.class)
public interface RemoteOuterCompanyService {

    @ApiOperation(value = "get company list",notes = "get company list")
    @GetMapping("/mdm/wms/api/company/list")
    AjaxResult list();

    @ApiOperation(value = "get company detail",notes = "get company detail")
    @GetMapping("/mdm/wms/api/company/get/{code}")
    AjaxResult getCompany(@PathVariable("code") String code);

}
