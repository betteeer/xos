package com.inossem.oms.mdm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.inossem.oms.base.svc.domain.Address;
import com.inossem.oms.base.svc.domain.VO.*;
import com.inossem.oms.base.svc.mapper.AddressMapper;
import com.inossem.oms.base.utils.UserInfoUtils;
import com.inossem.oms.mdm.common.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zoutong
 * @date 2022/10/25
 **/
@Service
@Slf4j
public class AddressService {

    @Resource
    private AddressMapper addressMapper;

    @Transactional(rollbackFor = Exception.class)
    public void saveSoAddress(SoAddressVO soAddressVO) {
        try {
            Date nowTime = new Date();
            List<AddressVO> billingAddressList = soAddressVO.getBillingAddressList();
            if (!CollectionUtils.isEmpty(billingAddressList)) {
                saveAddress(billingAddressList,
                        soAddressVO.getCompanyCode(), soAddressVO.getRefeenceKey(),
                        Util.SUB_TYPE_ADDRESS_BP_BILLTO, nowTime);
            }
            List<AddressVO> shippingAddressList = soAddressVO.getShippingAddressList();
            if (!CollectionUtils.isEmpty(shippingAddressList)) {
                saveAddress(shippingAddressList,
                        soAddressVO.getCompanyCode(), soAddressVO.getRefeenceKey(),
                        Util.SUB_TYPE_ADDRESS_BP_SHIPTO, nowTime);
            }
        } catch (Exception e) {
            log.error("save So Address failed", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveAddress(List<AddressVO> addressVOList, String companyCode, String key, String subtype, Date nowTime) {
        try {
            addressVOList.forEach(addressVO -> {
                Address address = new Address();
                address.setCompanyCode(companyCode);
                address.setType(Util.TYPE_ADDRESS_SO);
                address.setSubType(subtype);
                address.setReferenceKey(key);
                address.setAddress1(addressVO.getStreet());
                address.setCity(addressVO.getStreet());
                address.setRegionCode(addressVO.getProvince());
                address.setCountryCode(addressVO.getCountry());
                address.setPostalCode(addressVO.getPostCode());
                address.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                address.setGmtCreate(nowTime);
                address.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
                address.setGmtModified(nowTime);
                addressMapper.insert(address);
            });
        } catch (Exception e) {
            log.error("create so address failed", e);
            throw new RuntimeException(e);
        }
    }


    public Address getShippingAddress(SoShippingAddressVO soShippingAddressVO) {
        try {
            LambdaQueryWrapper<Address> addressLambdaQueryWrapper = new LambdaQueryWrapper<>();
            addressLambdaQueryWrapper.eq(Address::getCompanyCode, soShippingAddressVO.getCompanyCode());
            addressLambdaQueryWrapper.eq(Address::getSubType, Util.SUB_TYPE_ADDRESS_BP_SHIPTO);
            if (StringUtils.isNotBlank(soShippingAddressVO.getDeliveryKey())) {
                addressLambdaQueryWrapper.eq(Address::getType, Util.TYPE_ADDRESS_SODN);
                addressLambdaQueryWrapper.eq(Address::getReferenceKey, soShippingAddressVO.getDeliveryKey());
            } else {
                addressLambdaQueryWrapper.eq(Address::getType, Util.TYPE_ADDRESS_SO);
                addressLambdaQueryWrapper.eq(Address::getReferenceKey, soShippingAddressVO.getSoKey());
            }
            return addressMapper.selectOne(addressLambdaQueryWrapper);
        } catch (Exception e) {
            log.error("so getShippingAddress failed", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 通用地址保存方法
     *
     * @param address
     */
    public void save(AddressSaveVo address) {
        Date date = new Date();
        Address addr = new Address();
        addr.setCompanyCode(address.getCompanyCode());
        addr.setType(address.getType());
        addr.setSubType(address.getSubType());
        addr.setReferenceKey(address.getKey());
        addr.setAddress1(address.getStreet());
        addr.setCity(address.getStreet());
        addr.setRegionCode(address.getProvince());
        addr.setCountryCode(address.getCountry());
        addr.setPostalCode(address.getPostCode());
        addr.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
        addr.setGmtCreate(date);
        addr.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
        addr.setGmtModified(date);
        addressMapper.insert(addr);
    }
    public void save(AddressSaveVo address, String userId) {
        Date date = new Date();
        Address addr = new Address();
        addr.setCompanyCode(address.getCompanyCode());
        addr.setType(address.getType());
        addr.setSubType(address.getSubType());
        addr.setReferenceKey(address.getKey());
        addr.setAddress1(address.getStreet());
        addr.setCity(address.getStreet());
        addr.setRegionCode(address.getProvince());
        addr.setCountryCode(address.getCountry());
        addr.setPostalCode(address.getPostCode());
        addr.setCreateBy(userId);
        addr.setGmtCreate(date);
        addr.setModifiedBy(userId);
        addr.setGmtModified(date);
        addressMapper.insert(addr);
    }


    public List<Address> getAddress(String companyCode, String orderNumber) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("company_code", companyCode);
        queryWrapper.eq("reference_key", orderNumber);
        queryWrapper.eq("is_deleted", 0);
        return addressMapper.selectList(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void modifyAddress(AddressSaveVo address) {
        try {
            Date date = new Date();
            Address addr = new Address();
            addr.setCompanyCode(address.getCompanyCode());
            addr.setType(address.getType());
            addr.setSubType(address.getSubType());
            addr.setReferenceKey(address.getKey());
            addr.setAddress1(address.getStreet());
            addr.setCity(address.getCity());
            addr.setRegionCode(address.getProvince());
            addr.setCountryCode(address.getCountry());
            addr.setPostalCode(address.getPostCode());
            addr.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
            addr.setGmtCreate(date);
            addr.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
            addr.setGmtModified(date);

            QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("company_code", address.getCompanyCode());
            queryWrapper.eq("type", address.getType());
            queryWrapper.eq("sub_type", address.getSubType());
            queryWrapper.eq("reference_key", address.getKey());
            queryWrapper.eq("is_deleted", 0);
            addressMapper.update(addr, queryWrapper);
        } catch (Exception e) {
            log.error("modify address failed", e);
            throw new RuntimeException(e);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public void modifyAddress(AddressSaveVo address, String userId) {
        try {
            Date date = new Date();
            Address addr = new Address();
            addr.setCompanyCode(address.getCompanyCode());
            addr.setType(address.getType());
            addr.setSubType(address.getSubType());
            addr.setReferenceKey(address.getKey());
            addr.setAddress1(address.getStreet());
            addr.setCity(address.getCity());
            addr.setRegionCode(address.getProvince());
            addr.setCountryCode(address.getCountry());
            addr.setPostalCode(address.getPostCode());
            addr.setModifiedBy(userId);
            addr.setGmtModified(date);

            QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("company_code", address.getCompanyCode());
            queryWrapper.eq("type", address.getType());
            queryWrapper.eq("sub_type", address.getSubType());
            queryWrapper.eq("reference_key", address.getKey());
            queryWrapper.eq("is_deleted", 0);
            addressMapper.update(addr, queryWrapper);
        } catch (Exception e) {
            log.error("modify address failed", e);
            throw new RuntimeException(e);
        }
    }

    public List<Address> getAddress(AddressQueryVo address) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("company_code", address.getCompanyCode());
        queryWrapper.eq("reference_key", address.getKey());
        queryWrapper.eq("type", address.getType());
        queryWrapper.eq("sub_type", address.getSubType());
        queryWrapper.eq("is_deleted", 0);
        return addressMapper.selectList(queryWrapper);
    }

    public Address getPoShippingAddress(PoShippingAddressVO poShippingAddressVO) {
        try {
            LambdaQueryWrapper<Address> addressLambdaQueryWrapper = new LambdaQueryWrapper<>();
            addressLambdaQueryWrapper.eq(Address::getCompanyCode, poShippingAddressVO.getCompanyCode());
            addressLambdaQueryWrapper.eq(Address::getType, Util.TYPE_ADDRESS_SO);
            addressLambdaQueryWrapper.eq(Address::getSubType, Util.SUB_TYPE_ADDRESS_BP_SHIPTO);
            if (StringUtils.isNotBlank(poShippingAddressVO.getDeliveryKey())) {
                addressLambdaQueryWrapper.eq(Address::getReferenceKey, poShippingAddressVO.getDeliveryKey());
            } else {
                addressLambdaQueryWrapper.eq(Address::getReferenceKey, poShippingAddressVO.getPoKey());
            }
            return addressMapper.selectOne(addressLambdaQueryWrapper);
        } catch (Exception e) {
            log.error("so getShippingAddress failed", e);
            throw new RuntimeException(e);
        }
    }
}
