package com.inossem.oms.api.oms.api.remote;

import com.inossem.oms.base.common.constant.ServiceNameConstants;
import com.inossem.oms.base.svc.domain.*;
import com.inossem.oms.base.svc.domain.VO.*;
import com.inossem.sco.common.core.domain.R;
import com.inossem.oms.api.oms.api.factory.RemoteMdmFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Sku feign服务
 *
 * @author shigf
 */
@FeignClient(contextId = "remoteMdmService", value = ServiceNameConstants.MDM_SERVICE, fallbackFactory = RemoteMdmFallbackFactory.class)
public interface RemoteMdmService {

    /**
     * 根据skuName模糊查询sku列表
     *
     * @param skuName
     * @return
     */
    //    , @RequestHeader(SecurityConstants.FROM_SOURCE) String source
    @GetMapping("/mdm/api/sku/listBySkuName/{skuName}/{companyCode}")
    R<List<SkuMaster>> listSkuName(@PathVariable("skuName") String skuName,@PathVariable("companyCode") String companyCode);

    /**
     * 根据skuNumber查询sku信息
     *
     * @param skuNumber
     * @return
     */
    //    , @RequestHeader(SecurityConstants.FROM_SOURCE) String source
    @GetMapping("/mdm/api/sku/{skuNumber}/{companyCode}")
    R<SkuMaster> getSkuByNumber(@PathVariable("skuNumber") String skuNumber,@PathVariable("companyCode") String companyCode);

    @GetMapping("/mdm/api/sku/getDetail/{skuNumber}/{version}/{companyCode}")
    R<SkuMaster> getSkuByNumberAndVersion(@PathVariable("skuNumber") String skuNumber,@PathVariable(value = "version",required = false) String version,@PathVariable("companyCode") String companyCode);


    /**
     * 根据companyCode查询company信息
     *
     * @param companyCode
     * @return
     */
    //    , @RequestHeader(SecurityConstants.FROM_SOURCE) String source
    @GetMapping("/mdm/api/company/{companyCode}")
    R<Company> getCompanyByCode(@PathVariable("companyCode") String companyCode);

    /**
     * 根据warehouseCode查询warehouse信息
     *
     * @param warehouseCode
     * @return
     */
    //    , @RequestHeader(SecurityConstants.FROM_SOURCE) String source
    @GetMapping("/mdm/api/warehouse/get")
    R<Warehouse> getWarehourseByCode(@RequestParam(value = "companyCode") String companyCode, @RequestParam(value = "warehouseCode") String warehouseCode);


    /**
     * 根据soNumber 或 deliveryNumber获取shippingAddress
     *
     * @param
     * @return
     */
    @PostMapping("/mdm/api/address/getShipAddress")
    R<Address> getShippingAddress(@RequestBody SoShippingAddressVO soShippingAddressVO);


    /**
     * 根据poNumber 或 deliveryNumber获取shippingAddress
     *
     * @param
     * @return
     */
    @PostMapping("/mdm/api/address/getPoShipAddress")
    R<Address> getPoShippingAddress(@RequestBody PoShippingAddressVO poShippingAddressVO);

    /**
     * 地址的保存
     * @param address
     * @return
     */
    @PostMapping("/mdm/address/save")
    R<Address> saveAddress(@RequestBody AddressSaveVo address);

    /**
     * 地址的更新
     * @param address
     * @return
     */
    @PostMapping("/mdm/address/modify")
    R<Address> modifyAddress(@RequestBody AddressSaveVo address);


    /**
     * 根据 companyCode 和 poNumber或soNumber 查询发运及收货地址信息
     * @param companyCode
     * @param orderNumber
     * @return
     */
    @GetMapping("/mdm/api/address/getAddress")
    @Deprecated
    R<List<Address>>  getAddress(@RequestParam(value = "companyCode") String companyCode, @RequestParam(value = "orderNumber") String orderNumber);


    /**
     * 根据 bpNumber 获取 BpName
     * @param bpNumber
     * @return
     */
    @GetMapping("/mdm/api/bp/getBpName")
    R<BusinessPartner>  getBpNameByBpNumber(@RequestParam(value = "companyCode") String companyCode,@RequestParam(value = "bpNumber") String bpNumber);


//    /**
//     * 根据skuNumber查询sku信息
//     *
//     * @param skuName
//     * @return
//     */
//    @GetMapping("/mdm/api/sku/{skuName}")
//    R<SkuMaster> getSku(@PathVariable("skuName") String skuName);

    @GetMapping("/mdm/address/getAddressInfo")
    R<List<Address>> getAddress(@RequestBody AddressQueryVo addressQueryVo);






}
