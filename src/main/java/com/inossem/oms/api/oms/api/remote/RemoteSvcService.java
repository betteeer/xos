package com.inossem.oms.api.oms.api.remote;

import com.inossem.oms.base.common.constant.ServiceNameConstants;
import com.inossem.oms.base.svc.domain.*;
import com.inossem.sco.common.core.domain.R;
import com.inossem.oms.api.oms.api.factory.RemoteSvcFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * svc feign服务
 *
 * @author shigf
 */
@FeignClient(contextId = "remoteSvcService", value = "oms", fallbackFactory = RemoteSvcFallbackFactory.class)
public interface RemoteSvcService {

    /**
     * 通过companyCode/ bpCode / 及bpName 更新  soHeader中的bpName
     *
     * @return
     */
    @GetMapping("/svc/so/updateBpName/{company_code}/{bp_number}/{bp_name}")
    R<SoHeader> updateBpName(@PathVariable("company_code") String companyCode,
                             @PathVariable("bp_number") String bpNumber,
                             @PathVariable("bp_name") String bpName);


    /**
     * wareHouse停用查询是否还有delivery未发运  或者 so / po 未发运的订单
     * 无  true   可停用
     * 有  false  不可停用
     *
     * @param wareHouseCode
     * @param companyCode
     * @return
     */
    @GetMapping("/svc/wareHouse/check/{wareHouseCode}/{companyCode}")
    R<Boolean> check(@PathVariable("wareHouseCode") String wareHouseCode,
                     @PathVariable("companyCode") String companyCode);

    /**
     * condition table list
     *
     * @return
     */
    @GetMapping("/svc/conditionTable/innerList")
    R<List<ConditionTable>> conditionTableList(@RequestParam(value = "companyCode") String companyCode,
                                               @RequestParam(value = "type") String type);

    /**
     * sysConnect table list
     *
     * @return
     */
    @PostMapping("/svc/sysConnect/list")
    List<SystemConnect> connectList(@RequestBody SystemConnect systemConnect);


    /**
     * sysConnect table list
     *
     * @return
     */
    @PostMapping("/svc/sysConnect/lists")
    List<SystemConnect> connectLists(@RequestBody SystemConnect systemConnect);


    /**
     * 校验sku是否在po so 库存中使用
     * @param skuCode
     * @param companyCode
     * @return
     */
    @GetMapping("/svc/svcFeign/checkSku")
    R<Boolean> checkSku(@RequestParam("skuCode")String skuCode,@RequestParam("companyCode") String companyCode);
}
