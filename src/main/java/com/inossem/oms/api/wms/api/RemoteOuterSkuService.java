package com.inossem.oms.api.wms.api;

import com.inossem.oms.base.common.constant.ServiceNameConstants;
import com.inossem.oms.base.svc.domain.VO.SkuListReqVO;
import com.inossem.oms.base.svc.domain.VO.SkuVO;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import com.inossem.sco.common.core.web.page.TableDataInfo;
import com.inossem.oms.api.wms.factory.RemoteOuterSkuFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 提供给外部调用的SKU服务
 */
@FeignClient(contextId = "remoteOuterSkuService", value = ServiceNameConstants.MDM_SERVICE, fallbackFactory = RemoteOuterSkuFallbackFactory.class)
public interface RemoteOuterSkuService {

    /**
     * 查询sku列表服务
     *
     * @param skuListReqVO
     * @param pageNum 页数
     * @param pageSize 每页显示的条数
     * @return
     */
    @PostMapping("/mdm/sku/wms/list")
    TableDataInfo list(@RequestBody SkuListReqVO skuListReqVO, @RequestParam(value = "pageNum") Integer pageNum, @RequestParam(value = "pageSize") Integer pageSize);

    /**
     * 创建sku服务
     *
     * @return
     */
    @PostMapping("/mdm/wms/api/sku/create")
    AjaxResult create(@RequestBody SkuVO skuVO);

    /**
     * 修改sku服务
     *
     * @param skuVO
     * @return
     */
    @PostMapping("/mdm/wms/api/sku/modify")
    AjaxResult modifySku(@RequestBody SkuVO skuVO);


}
