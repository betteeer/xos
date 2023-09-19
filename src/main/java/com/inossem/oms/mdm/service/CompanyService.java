package com.inossem.oms.mdm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.inossem.oms.api.kyc.api.KycCommonService;
import com.inossem.oms.api.kyc.model.KycCompany;
import com.inossem.oms.api.kyc.utils.TaxChartUtils;
import com.inossem.oms.base.svc.domain.Address;
import com.inossem.oms.base.svc.domain.Company;
import com.inossem.oms.base.svc.domain.CompanyUserCheck;
import com.inossem.oms.base.svc.mapper.AddressMapper;
import com.inossem.oms.base.svc.mapper.CompanyMapper;
import com.inossem.oms.base.svc.mapper.CompanyUserCheckMapper;
import com.inossem.oms.mdm.common.Util;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class CompanyService {

    @Resource
    private CompanyMapper companyMapper;

    @Resource
    private AddressMapper addressMapper;

    @Resource
    private CompanyUserCheckMapper companyUserCheckMapper;

    @Resource
    private KycCommonService kycCommonService;
    @Transactional(rollbackFor = Exception.class)
    public Company create(Company companyVO) {
        try {
            Date nowTime = new Date();
            Company company = new Company();
            company.setCompanyCode(companyVO.getCompanyCode());
            company.setName(companyVO.getName());
            company.setDescription(companyVO.getDescription());
            company.setCurrencyCode(companyVO.getCurrencyCode());
            company.setLanguageCode(companyVO.getLanguageCode());
            company.setTimeZone(companyVO.getTimeZone());
            company.setStatus(companyVO.getStatus());
            company.setLogoUrl(companyVO.getLogoUrl());
            company.setCompanyEmail(companyVO.getCompanyEmail());
            company.setGstHstTaxCode(companyVO.getGstHstTaxCode());
            company.setQstTaxCode(companyVO.getQstTaxCode());
            company.setPstBcTaxCode(companyVO.getPstBcTaxCode());
            company.setPstSkTaxCode(companyVO.getPstSkTaxCode());
            company.setDeptid(companyVO.getDeptid());
            // ###todo###
            company.setCreateBy(String.valueOf(1));
            company.setGmtCreate(nowTime);
            // ###todo###
            company.setModifiedBy(String.valueOf(1));
            company.setGmtModified(nowTime);
            companyMapper.insert(company);
            if (isOneNotBlank(companyVO.getStreet(), companyVO.getCity(), companyVO.getProvince(),
                    companyVO.getPostCode(), companyVO.getCountry(), companyVO.getCompanyCode())) {
                saveAddress(companyVO);
            }
            return company;
        } catch (Exception e) {
            log.error("company create failed", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveAddress(Company companyVO) {
        try {
            Date nowTime = new Date();
            Address address = new Address();
            address.setCompanyCode(companyVO.getCompanyCode());
            address.setType(Util.TYPE_ADDRESS_COMPANY);
            address.setSubType(Util.SUB_TYPE_ADDRESS_COMPANY);
            address.setReferenceKey(companyVO.getCompanyCode());  //todo 暂定
            address.setAddress1(companyVO.getStreet());
            address.setCity(companyVO.getCity());
            address.setRegionCode(companyVO.getProvince());
            address.setCountryCode(companyVO.getCountry());
            address.setPostalCode(companyVO.getPostCode());
            // ###todo###
            address.setCreateBy(String.valueOf(1));
            address.setGmtCreate(nowTime);
            // ###todo###
            address.setModifiedBy(String.valueOf(1));
            address.setGmtModified(nowTime);
            addressMapper.insert(address);
        } catch (Exception e) {
            log.error("address create failed", e);
            throw new RuntimeException(e);
        }
    }

    public Company getCompany(String companyCode) {
        try {
            KycCompany kycCompany = kycCommonService.getCompanyByCode(companyCode);
            Company company = new Company();
            company.setId(Long.valueOf(kycCompany.getId()));
            company.setCompanyCode(kycCompany.getCode());
            company.setOrgidEx(kycCompany.getId());
            company.setCompanyCodeEx(kycCompany.getCode());
            company.setName(kycCompany.getName());
            company.setDescription(kycCompany.getName());
            company.setCurrencyCode(kycCompany.getCurrency());
            company.setLanguageCode(kycCompany.getLanguage());
            company.setTimeZone(kycCompany.getTimezone());
            company.setCompanyEmail(kycCompany.getEmail());
            company.setDeptid(kycCompany.getId());
            company.setGstHstTaxCode(TaxChartUtils.getTaxValue(kycCompany.getTaxChart(), "HST / GST"));
            return company;
        } catch (IOException e) {
            log.error("get Company failed", e);
            throw new RuntimeException(e);
        }
    }

    public Company getCompanyById(Long id) {
        try {
            Company company = companyMapper.selectOne(new LambdaQueryWrapper<Company>()
                    .eq(Company::getOrgidEx, id));
            Address address = addressMapper.selectOne(new LambdaQueryWrapper<Address>()
                    .eq(Address::getCompanyCode, company.getCompanyCode())
                    .eq(Address::getType, Util.TYPE_ADDRESS_COMPANY)
                    .eq(Address::getSubType, Util.SUB_TYPE_ADDRESS_COMPANY)
                    .eq(Address::getReferenceKey, company.getCompanyCode())
                    .eq(Address::getIsDeleted, 0));
            if (null != address) {
                company.setAddressId(address.getId());
                company.setStreet(address.getAddress1());
                company.setCity(address.getCity());
                company.setCountry(address.getCountryCode());
                company.setProvince(address.getRegionCode());
                company.setPostCode(address.getPostalCode());
            }
            return company;
        } catch (Exception e) {
            log.error("get Company failed", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @deprecated
     * no usage
     * @param userName
     * @return
     */
    @Deprecated
    public List<Company> list(String userName) {
        try {
            LambdaQueryWrapper<CompanyUserCheck> userCheckLambdaQueryWrapper = new LambdaQueryWrapper<CompanyUserCheck>()
                    .eq(CompanyUserCheck::getUserName, userName)
                    .eq(CompanyUserCheck::getIsDeleted, 0);
            List<CompanyUserCheck> companyUserChecks = companyUserCheckMapper.selectList(userCheckLambdaQueryWrapper);
            if (companyUserChecks.isEmpty()) {
                log.info("当前用户名称:{},未查询到公司信息", userName);
                throw new RuntimeException("当前登录用户未查询到公司信息");
            }
            List<String> companyCodes = new ArrayList<>();
            companyUserChecks.forEach(com -> {
                companyCodes.add(com.getCompanyCode());
            });
            log.info(">>>>companyCodes:{}", companyCodes);
            List<Address> addressList = addressMapper.selectList(new LambdaQueryWrapper<Address>()
                    .eq(Address::getType, Util.TYPE_ADDRESS_COMPANY)
                    .eq(Address::getSubType, Util.SUB_TYPE_ADDRESS_COMPANY)
                    .eq(Address::getIsDeleted, 0));
            List<Company> companyList = companyMapper.selectList(new LambdaQueryWrapper<Company>()
                    .in(Company::getCompanyCode, companyCodes)
                    .eq(Company::getIsDeleted, 0));
            companyList.forEach(company -> {
                Optional<Address> addressOptional = addressList.stream().filter(add ->
                        add.getCompanyCode().equals(company.getCompanyCode())).findFirst();
                if (addressOptional.isPresent()) {
                    Address address = addressOptional.get();
                    company.setAddressId(address.getId());
                    company.setStreet(address.getAddress1());
                    company.setCity(address.getCity());
                    company.setCountry(address.getCountryCode());
                    company.setProvince(address.getRegionCode());
                    company.setPostCode(address.getPostalCode());
                }
            });
            return companyList;
        } catch (Exception e) {
            log.error("get company list failed", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Company modify(Company company) {
        try {
            if (null == company.getId()) {
                throw new RuntimeException("company id is not null");
            }
            companyMapper.updateById(company);
            LambdaUpdateWrapper<Address> addressLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            addressLambdaUpdateWrapper.eq(Address::getId, company.getAddressId())
                    .set(Address::getAddress1, company.getStreet())
                    .set(Address::getCity, company.getCity())
                    .set(Address::getCountryCode, company.getCountry())
                    .set(Address::getRegionCode, company.getProvince())
                    .set(Address::getPostalCode, company.getPostCode())
                    .set(Address::getGmtModified, new Date())
                    // ###todo###
                    .set(Address::getModifiedBy, String.valueOf(1))
                    .eq(Address::getCompanyCode, company.getCompanyCode());
            addressMapper.update(null, addressLambdaUpdateWrapper);
            return company;
        } catch (Exception e) {
            log.error("modify company failed", e);
            throw new RuntimeException(e);
        }
    }

    private Company getCompanyStruct(Map<String, Object> newDeptDetail) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Company company = new Company();
            //Map<String,Object> oldDeptDetail = (Map<String,Object>)deptMap.get("oldDeptDetail");
            company.setName((String) newDeptDetail.get("deptName"));
            String status = (String) newDeptDetail.get("status");
            company.setStatus(Integer.parseInt(status));
            int deptId = (Integer) newDeptDetail.get("deptId");
            company.setDeptid(String.valueOf(deptId));
            company.setCompanyCode(String.valueOf(deptId));
            company.setCompanyEmail((String) newDeptDetail.get("email"));
            company.setCreateBy(null);
            if (newDeptDetail.get("createBy") != null) {
                company.setCreateBy((String) newDeptDetail.get("createBy"));
            }

            company.setGmtCreate(null);
            if (newDeptDetail.get("createTime") != null) {
                company.setGmtCreate(simpleDateFormat.parse((String) newDeptDetail.get("createTime")));
            }

            company.setModifiedBy(null);
            if (newDeptDetail.get("updateBy") != null) {
                company.setModifiedBy((String) newDeptDetail.get("updateBy"));
            }

            company.setGmtModified(null);
            if (newDeptDetail.get("updateTime") != null) {
                company.setGmtModified(simpleDateFormat.parse((String) newDeptDetail.get("updateTime")));
            }
            return company;
        } catch (Exception e) {
            log.error("getCompanyStruct failed", e);
            throw new RuntimeException(e);
        }
    }

    public static boolean isOneNotBlank(String... strs) {
        for (String str : strs) {
            if (StringUtils.isNotBlank(str)) {
                return true;
            }
        }
        return false;
    }

    public String getCurrencyCodeByCompanyCode(String companyCode) {
        Company company = companyMapper.selectOne(new LambdaQueryWrapper<Company>().eq(Company::getCompanyCode, companyCode));
        return company.getCurrencyCode();
    }
}
