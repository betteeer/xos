package com.inossem.oms.mdm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.inossem.oms.base.svc.domain.Address;
import com.inossem.oms.base.svc.domain.VO.WarehouseVO;
import com.inossem.oms.base.svc.domain.Warehouse;
import com.inossem.oms.base.svc.mapper.AddressMapper;
import com.inossem.oms.base.svc.mapper.WarehouseMapper;
import com.inossem.oms.base.utils.UserInfoUtils;
import com.inossem.oms.mdm.common.Util;
import com.inossem.oms.svc.service.WareHouseApiService;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zoutong
 * @date 2022/10/17
 **/
@Service
@Slf4j
public class WarehouseService {

    @Resource
    private WarehouseMapper warehouseMapper;

    @Resource
    private AddressMapper addressMapper;

    @Resource
    private WareHouseApiService wareHouseApiService;
    @Transactional(rollbackFor = Exception.class)
    public Warehouse Create(WarehouseVO warehouseVO) {
        try {
            Warehouse oldWarehouse = warehouseMapper.selectOne(new LambdaQueryWrapper<Warehouse>()
                    .eq(Warehouse::getWarehouseCode, warehouseVO.getWarehouseCode())
                    .eq(Warehouse::getCompanyCode, warehouseVO.getCompanyCode()));
            if (oldWarehouse != null) {
                throw new RuntimeException("WarehouseCode Already Exists");
            }
            Date nowTime = new Date();
            String companyCode = warehouseVO.getCompanyCode();
            Warehouse warehouse = new Warehouse();
            warehouse.setCompanyCode(companyCode);
            warehouse.setName(warehouseVO.getName());
            warehouse.setWarehouseCode(warehouseVO.getWarehouseCode());
            warehouse.setStatus(warehouseVO.getStatus());
            warehouse.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
            //warehouse.setCreateBy("test01");
            warehouse.setGmtCreate(nowTime);
            warehouse.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
            //warehouse.setModifiedBy("test01");
            warehouse.setGmtModified(nowTime);
            warehouse.setIsDeleted(0);
            warehouseMapper.insert(warehouse);
            Address address = new Address();
            address.setCompanyCode(companyCode);
            address.setType(Util.TYPE_ADDRESS_WH);
            address.setSubType(Util.SUB_TYPE_ADDRESS_WH);
            address.setReferenceKey(warehouseVO.getWarehouseCode());
            address.setAddress1(warehouseVO.getStreet());
            address.setCity(warehouseVO.getCity());
            address.setRegionCode(warehouseVO.getProvince());
            address.setCountryCode(warehouseVO.getCountry());
            address.setPostalCode(warehouseVO.getPostCode());
            //address.setCreateBy("test01");
            address.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
            address.setGmtCreate(nowTime);
            //address.setModifiedBy("test01");
            address.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
            address.setGmtModified(nowTime);
            addressMapper.insert(address);
            warehouse.setStreet(address.getAddress1());
            warehouse.setCity(address.getCity());
            warehouse.setProvince(address.getRegionCode());
            warehouse.setCountry(address.getCountryCode());
            warehouse.setPostCode(address.getPostalCode());
            return warehouse;
        } catch (Exception e) {
            log.error("create warehouse failed", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int Modify(WarehouseVO warehouseVO) {
        try {
            String wareHouseCode = warehouseVO.getWarehouseCode();
            String companyCode = warehouseVO.getCompanyCode();
            if (StringUtils.isEmpty(wareHouseCode) || StringUtils.isEmpty(companyCode)) {
                throw new RuntimeException("查询wareHouse是否存在未发运接口入参异常");
            }
            Boolean checkData = wareHouseApiService.check(wareHouseCode, companyCode);

            log.info(">>>>>检查 wareHouse是否存在未关闭订单,wareHouse:{},companyCode:{},返回结果:{} ", warehouseVO.getWarehouseCode(), warehouseVO.getCompanyCode(), checkData);
            if (checkData == false) {
                throw new RuntimeException("存在未完成的发货订单，Warehouse不允许停用！");
            }
            Date nowTime = new Date();
            LambdaUpdateWrapper<Warehouse> warehouseLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            warehouseLambdaUpdateWrapper
                    //.set(Warehouse::getName, warehouseVO.getName())
                    //.set(Warehouse::getWarehouseCode, warehouseVO.getWarehouseCode())
                    .set(Warehouse::getStatus, warehouseVO.getStatus())
                    .set(Warehouse::getModifiedBy, String.valueOf(UserInfoUtils.getSysUserId()))
                    .set(Warehouse::getGmtModified, nowTime)
                    .eq(Warehouse::getWarehouseCode, warehouseVO.getWarehouseCode())
                    .eq(Warehouse::getCompanyCode, warehouseVO.getCompanyCode());
            LambdaUpdateWrapper<Address> addressLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            if (null == warehouseVO.getStreet()) {
                warehouseVO.setStreet("");
            }
            if (null == warehouseVO.getCity()) {
                warehouseVO.setCity("");
            }
            if (null == warehouseVO.getProvince()) {
                warehouseVO.setProvince("");
            }
            if (null == warehouseVO.getCountry()) {
                warehouseVO.setCountry("");
            }
            if (null == warehouseVO.getPostCode()) {
                warehouseVO.setPostCode("");
            }
            addressLambdaUpdateWrapper
                    .set(Address::getAddress1, warehouseVO.getStreet())
                    .set(Address::getCity, warehouseVO.getCity())
                    .set(Address::getRegionCode, warehouseVO.getProvince())
                    .set(Address::getCountryCode, warehouseVO.getCountry())
                    .set(Address::getPostalCode, warehouseVO.getPostCode())
                    .set(Address::getModifiedBy, String.valueOf(UserInfoUtils.getSysUserId()))
                    .set(Address::getGmtModified, nowTime)
                    .eq(Address::getReferenceKey, warehouseVO.getWarehouseCode())
                    .eq(Address::getCompanyCode, warehouseVO.getCompanyCode());
            addressMapper.update(null, addressLambdaUpdateWrapper);
            return warehouseMapper.update(null, warehouseLambdaUpdateWrapper);
        } catch (Exception e) {
            log.error("modify warehouse failed", e);
            throw new RuntimeException(e);
        }
    }

    public List<Warehouse> list(String companyCode) {
        try {
            LambdaQueryWrapper<Warehouse> warehouseLambdaQueryWrapper = new LambdaQueryWrapper<>();
            warehouseLambdaQueryWrapper.eq(Warehouse::getCompanyCode, companyCode);
            warehouseLambdaQueryWrapper.eq(Warehouse::getIsDeleted, 0);
            List<Warehouse> warehouseList = warehouseMapper.selectList(warehouseLambdaQueryWrapper);
            LambdaQueryWrapper<Address> addressLambdaQueryWrapper = new LambdaQueryWrapper<>();
            addressLambdaQueryWrapper
                    .eq(Address::getCompanyCode, companyCode)
                    .eq(Address::getType, Util.TYPE_ADDRESS_WH)
                    .eq(Address::getSubType, Util.SUB_TYPE_ADDRESS_WH)
                    .eq(Address::getIsDeleted, 0);
            List<Address> addressList = addressMapper.selectList(addressLambdaQueryWrapper);
            warehouseList.forEach(warehouse -> {
                Address address = addressList.stream().filter(add ->
                        warehouse.getWarehouseCode().equals(add.getReferenceKey())).findFirst().orElse(null);
                if (address != null) {
                    warehouse.setStreet(address.getAddress1());
                    warehouse.setCity(address.getCity());
                    warehouse.setProvince(address.getRegionCode());
                    warehouse.setCountry(address.getCountryCode());
                    warehouse.setPostCode(address.getPostalCode());
                }
            });
            return warehouseList;
        } catch (Exception e) {
            log.error("warehouse list failed", e);
            throw new RuntimeException(e);
        }
    }

    public Warehouse getWarehouse(String companyCode, String warehouseCode) {
        try {
            Warehouse warehouse = warehouseMapper.selectOne(new LambdaQueryWrapper<Warehouse>()
                    .eq(Warehouse::getWarehouseCode, warehouseCode)
                    .eq(Warehouse::getCompanyCode, companyCode));
            LambdaQueryWrapper<Address> addressLambdaQueryWrapper = new LambdaQueryWrapper<>();
            addressLambdaQueryWrapper.eq(Address::getCompanyCode, companyCode)
                    .eq(Address::getReferenceKey, warehouseCode)
                    .eq(Address::getType, Util.TYPE_ADDRESS_WH)
                    .eq(Address::getSubType, Util.SUB_TYPE_ADDRESS_WH)
                    .eq(Address::getIsDeleted, 0);
            Address address = addressMapper.selectOne(addressLambdaQueryWrapper);
            if (address != null) {
                warehouse.setStreet(address.getAddress1());
                warehouse.setCity(address.getCity());
                warehouse.setProvince(address.getRegionCode());
                warehouse.setCountry(address.getCountryCode());
                warehouse.setPostCode(address.getPostalCode());
            }
            return warehouse;
        } catch (Exception e) {
            log.error("get Warehouse failed", e);
            throw new RuntimeException(e);
        }
    }
}
