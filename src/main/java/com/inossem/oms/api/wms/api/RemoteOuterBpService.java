package com.inossem.oms.api.wms.api;

import com.inossem.oms.base.common.constant.ServiceNameConstants;
import com.inossem.oms.base.svc.domain.BusinessPartner;
import com.inossem.oms.base.svc.domain.VO.BPListVO;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import com.inossem.oms.api.wms.factory.RemoteOuterBpFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 提供给外部调用的BP服务
 */
@FeignClient(contextId = "remoteOuterBpService", value = ServiceNameConstants.MDM_SERVICE, fallbackFactory = RemoteOuterBpFallbackFactory.class)
public interface RemoteOuterBpService {


    /**
     * 查询bp列表服务
     *
     * @param bpListVO
     * @return
     */
    @PostMapping("/mdm/bp/wms/list")
    TableDataInfo list(@RequestBody BPListVO bpListVO);


    /**
     * 创建bp服务
     *
     * @param businessPartner
     * @return
     */
    @PostMapping("/mdm/wms/api/bp/create")
    AjaxResult create(@RequestBody BusinessPartner businessPartner);

    /**
     * 修改bp服务
     *
     * @param businessPartner
     * @return
     */
    @PostMapping("/mdm/wms/api/bp/modify")
    AjaxResult modify(@RequestBody BusinessPartner businessPartner);


}
