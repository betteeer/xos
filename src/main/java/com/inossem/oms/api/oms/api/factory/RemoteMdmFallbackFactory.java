package com.inossem.oms.api.oms.api.factory;


import com.inossem.oms.api.oms.api.remote.RemoteMdmService;
import com.inossem.oms.base.svc.domain.*;
import com.inossem.oms.base.svc.domain.VO.*;
import com.inossem.sco.common.core.domain.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 文件服务降级处理
 *
 * @author shigf
 */
@Component
public class RemoteMdmFallbackFactory implements FallbackFactory<RemoteMdmService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteMdmFallbackFactory.class);

    @Override
    public RemoteMdmService create(Throwable throwable) {
        log.error("Mdm服务调用失败:{}", throwable.getMessage());
        return new RemoteMdmService() {

            @Override
            public R<List<SkuMaster>> listSkuName(String skuName, String companyCode) {
                return R.fail("根据name模糊查询sku失败:" + throwable.getMessage());
            }

            @Override
            public R<SkuMaster> getSkuByNumber(String skuNumber, String companyCode) {
                return R.fail("根据number查询sku失败:" + throwable.getMessage());
            }

            @Override
            public R<SkuMaster> getSkuByNumberAndVersion(String skuNumber, String version, String companyCode) {
                return R.fail("根据skuNumber查询kittingSku失败:" + throwable.getMessage());
            }

            @Override
            public R<Company> getCompanyByCode(String companyCode) {
                return R.fail("根据code查询code失败:" + throwable.getMessage());
            }

            @Override
            public R<Warehouse> getWarehourseByCode(String companyCode,String warehouseCode) {
                return R.fail("根据code模糊查询warehorse失败:" + throwable.getMessage());
            }

            @Override
            public R<Address> getShippingAddress(SoShippingAddressVO soShippingAddressVO) {
                return R.fail("根据soNumber或deliveryNumber获取shippingAddress失败:" + throwable.getMessage());
            }

            @Override
            public R<Address> getPoShippingAddress(PoShippingAddressVO poShippingAddressVO) {
                return R.fail("根据poNumber或deliveryNumber获取shippingAddress失败:" + throwable.getMessage());
            }
//            @Override
//            public R<SkuMaster> getSku(String skuName) {
//                return R.fail("根据soNumber获取sku信息失败:" + throwable.getMessage());
//            }

            @Override
            public R<Address> saveAddress(AddressSaveVo addressSaveVo) {
                return R.fail("保存地址信息失败:" + throwable.getMessage());
            }

            @Override
            public R<Address> modifyAddress(AddressSaveVo address) {
                return R.fail("更新地址信息失败:" + throwable.getMessage());
            }


            @Override
            public R<List<Address>> getAddress(String companyCode, String orderNumber) {
                return R.fail("获取收发地址信息失败:" + throwable.getMessage());
            }

            @Override
            public R<BusinessPartner> getBpNameByBpNumber(String companyCode, String bpNumber) {
                return R.fail("获取bpName信息失败:" + throwable.getMessage());
            }

            @Override
            public R<List<Address>> getAddress(AddressQueryVo addressQueryVo) {
                return R.fail("获取getAddress信息失败:" + throwable.getMessage());
            }




//            @Override
//            public R<Address> getAddress(String a) {
//                return null;
//            }
        };
    }
}
