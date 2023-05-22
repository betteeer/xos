package com.inossem.oms.mdm.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.inossem.oms.api.bk.api.BookKeepingService;
import com.inossem.oms.base.svc.domain.*;
import com.inossem.oms.base.svc.domain.VO.AddressVO;
import com.inossem.oms.base.svc.domain.VO.BPListVO;
import com.inossem.oms.base.svc.mapper.AddressMapper;
import com.inossem.oms.base.svc.mapper.BusinessPartnerMapper;
import com.inossem.oms.base.svc.mapper.ContactMapper;
import com.inossem.oms.base.svc.vo.ImportBusinessPartnerVo;
import com.inossem.oms.base.utils.UserInfoUtils;
import com.inossem.oms.base.utils.poi.ExcelUtil;
import com.inossem.oms.mdm.common.Util;
import com.inossem.oms.svc.service.ConditionTableService;
import com.inossem.sco.common.core.exception.ServiceException;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.inossem.sco.common.core.utils.PageUtils.startPage;

/**
 * @author zoutong
 * @date 2022/10/17
 **/
@Service
@Slf4j
public class BpService {

    @Resource
    private BusinessPartnerMapper businessPartnerMapper;

    @Resource
    private AddressMapper addressMapper;

    @Resource
    private ContactMapper contactMapper;

    @Resource
    private BookKeepingService bookKeepingService;

    @Resource
    private CompanyService companyService;

    @Resource
    private ConditionTableService conditionTableService;


    @Transactional(rollbackFor = Exception.class)
    public BusinessPartner create(BusinessPartner businessPartner) {
        try {
            String companyCode = businessPartner.getCompanyCode();
            List<BusinessPartner> businessPartnerList = businessPartnerMapper.selectList(new LambdaQueryWrapper<BusinessPartner>()
                    .eq(BusinessPartner::getBpName, businessPartner.getBpName())
                    .eq(BusinessPartner::getCompanyCode, companyCode));
            if (businessPartnerList.size() > 0) {
                throw new RuntimeException("bp name already exists");
            }
            LambdaQueryWrapper<BusinessPartner> businessPartnerLambdaQueryWrapper = new LambdaQueryWrapper<BusinessPartner>()
                    .eq(BusinessPartner::getCompanyCode, companyCode)
                    .orderByDesc(BusinessPartner::getBpNumber).last("limit 1");
            BusinessPartner oldBp = businessPartnerMapper.selectOne(businessPartnerLambdaQueryWrapper);
            String oldBpNumber = "10000";
            String newBpNumber = oldBpNumber;
            if (oldBp != null) {
                if (oldBp.getCompanyCode().equals(businessPartner.getCompanyCode()) && oldBp.getBpName().equals(businessPartner.getBpName())) {
                    throw new RuntimeException("Business Partner Name Already Exists");
                }
                if ("99999".equals(oldBp.getBpNumber())) {
                    throw new RuntimeException("Business Partner ID already is 99999,Not allowed to create again");
                }
                oldBpNumber = oldBp.getBpNumber();
                newBpNumber = new BigDecimal(oldBpNumber).add(BigDecimal.ONE).toPlainString();
            }
            BusinessPartner bp = new BusinessPartner();
            bp.setCompanyCode(companyCode);
            bp.setBpNumber(newBpNumber);
            bp.setBpName(businessPartner.getBpName());
            bp.setBpTel(businessPartner.getBpTel());
            bp.setBpEmail(businessPartner.getBpEmail());
            bp.setBpContact(businessPartner.getBpContact());
            Date nowTime = new Date();
            //bp.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
            bp.setGmtCreate(nowTime);
            //bp.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
            bp.setGmtModified(nowTime);

            //根据companyCode查询company表得到orgid_ex和company_code_ex
            Company company = companyService.getCompany(bp.getCompanyCode());
            //businessPartner.setOrgidEx(company.getData().getOrgidEx());
            //businessPartner.setCompanyCodeEx(company.getData().getCompanyCodeEx());

            // sync bk. 查下bk下是否有这个bp
            JSONObject customerBp = bookKeepingService.bpCustomerList(company.getOrgidEx(), company.getCompanyCodeEx(), businessPartner.getBpName());
//            JSONObject vendorBp = bookKeepingService.bpVendorList(businessPartner.getCompanyCode(), businessPartner.getBpName());
            if (customerBp != null) {
                bp.setBkBpNumberCustomer(customerBp.getString("contact_id"));
            } else {
                // 为空，要调用新增的接口
                JSONObject otherParam = new JSONObject();
                // 根据公司信息去查，暂时固定
                List<ConditionTable> tables = conditionTableService.innerList(businessPartner.getCompanyCode(), "CU01");
                ConditionTable table = null;
                if (tables == null || tables.isEmpty()) {
                    table = new ConditionTable();
                } else {
                    table = tables.get(0);
                }
                log.info("customer:{}", JSONObject.toJSONString(table));
                otherParam.put("customerAccount", table.getAccountName());

                tables = conditionTableService.innerList(businessPartner.getCompanyCode(), "VD01");
                if (tables == null || tables.isEmpty()) {
                    table = new ConditionTable();
                } else {
                    table = tables.get(0);
                }
                log.info("supplier:{}", JSONObject.toJSONString(table));
                otherParam.put("supplierAccount", table.getAccountName());

                log.info("other:{}", otherParam.toJSONString());


                JSONObject obj = syncBpToBk(businessPartner, otherParam);
                bp.setBkBpNumberCustomer(obj.getString("contact_id"));
                bp.setBkBpNumberVendor("");
            }
            bp.setBkBpNumberVendor("");

            businessPartnerMapper.insert(bp);
            bp.setContactList(businessPartner.getContactList());
            bp.setOfficeList(businessPartner.getOfficeList());
            bp.setBilltoList(businessPartner.getBilltoList());
            bp.setShiptoList(businessPartner.getShiptoList());
            if (CollectionUtils.isNotEmpty(businessPartner.getContactList())) {
                businessPartner.getContactList().forEach(contact -> saveContact(contact, companyCode, bp.getBpNumber(), nowTime));
            }
            if (CollectionUtils.isNotEmpty(businessPartner.getOfficeList())) {
                if (businessPartner.getOfficeList().size() > 1) {
                    throw new RuntimeException("Office Address only one");
                }
                saveAddress(businessPartner.getOfficeList(), companyCode, bp.getBpNumber(), Util.SUB_TYPE_ADDRESS_BP_OFFICE, nowTime);
            }
            if (CollectionUtils.isNotEmpty(businessPartner.getBilltoList())) {
                if (businessPartner.getBilltoList().size() > 1) {
                    throw new RuntimeException("Billing Address only one");
                }
                saveAddress(businessPartner.getBilltoList(), companyCode, bp.getBpNumber(), Util.SUB_TYPE_ADDRESS_BP_BILLTO, nowTime);
            }
            if (CollectionUtils.isNotEmpty(businessPartner.getShiptoList())) {
                saveAddress(businessPartner.getShiptoList(), companyCode, bp.getBpNumber(), Util.SUB_TYPE_ADDRESS_BP_SHIPTO, nowTime);
            }
            return bp;
        } catch (Exception e) {
            log.error("create bp failed", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 同步bp信息到bk
     *
     * @param bp
     */
    private JSONObject syncBpToBk(BusinessPartner bp, JSONObject otherParam) throws IOException {
        return bookKeepingService.saveBp(bp, otherParam);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveAddress(List<AddressVO> addressVOList, String companyCode, String bpNumber, String subtype, Date nowTime) {
        try {
            addressVOList.forEach(addressVO -> {
                Address address = new Address();
                address.setCompanyCode(companyCode);
                address.setType(Util.TYPE_ADDRESS_BP);
                address.setSubType(subtype);
                address.setReferenceKey(bpNumber);
                address.setAddress1(addressVO.getStreet());
                address.setCity(addressVO.getCity());
                address.setRegionCode(addressVO.getProvince());
                address.setCountryCode(addressVO.getCountry());
                address.setPostalCode(addressVO.getPostCode());
                address.setIsDefault(addressVO.getIsDefault());
                //address.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                address.setGmtCreate(nowTime);
                //address.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
                address.setGmtModified(nowTime);
                addressMapper.insert(address);
            });
        } catch (Exception e) {
            log.error("create bp address failed", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int modify(BusinessPartner businessPartner) {
        try {
            String companyCode = businessPartner.getCompanyCode();
            List<BusinessPartner> businessPartnerList = businessPartnerMapper.selectList(new LambdaQueryWrapper<BusinessPartner>()
                    .eq(BusinessPartner::getBpName, businessPartner.getBpName())
                    .eq(BusinessPartner::getCompanyCode, companyCode).ne(BusinessPartner::getId, businessPartner.getId()));
            if (businessPartnerList.size() > 0) {
                throw new RuntimeException("bp name already exists");
            }
            Date nowTime = new Date();
            LambdaUpdateWrapper<BusinessPartner> businessPartnerLambdaUpdateWrapper = new LambdaUpdateWrapper<BusinessPartner>();
            businessPartnerLambdaUpdateWrapper
                    .set(BusinessPartner::getBpName, businessPartner.getBpName())
                    .set(BusinessPartner::getBpTel, businessPartner.getBpTel())
                    .set(BusinessPartner::getBpEmail, businessPartner.getBpEmail())
                    .set(BusinessPartner::getBpContact, businessPartner.getBpContact())
                    //.set(BusinessPartner::getModifiedBy, "test01")
                    .set(BusinessPartner::getModifiedBy, String.valueOf(UserInfoUtils.getSysUserId()))
                    .set(BusinessPartner::getGmtModified, nowTime)
                    .eq(BusinessPartner::getBpNumber, businessPartner.getBpNumber())
                    .eq(BusinessPartner::getCompanyCode, companyCode);

            // modify Contact
            List<Long> newIds = businessPartner.getContactList().stream().map(Contact::getId)
                    .collect(Collectors.toList());
            newIds.removeIf(Objects::isNull);
            List<Contact> contactList = contactMapper.selectList(new LambdaQueryWrapper<Contact>()
                    .eq(Contact::getCompanyCode, companyCode)
                    .eq(Contact::getBpNumber, businessPartner.getBpNumber()));
            LambdaUpdateWrapper<Contact> contactLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            businessPartner.getContactList().forEach(contact -> {
                contactLambdaUpdateWrapper.clear();
                if (contact.getId() == null) {
                    saveContact(contact, companyCode, businessPartner.getBpNumber(), nowTime);
                } else {
                    contactLambdaUpdateWrapper.eq(Contact::getId, contact.getId())
                            .set(Contact::getContactType, contact.getContactType())
                            .set(Contact::getContactPerson, contact.getContactPerson())
                            .set(Contact::getContactTel, contact.getContactTel())
                            .set(Contact::getContactEmail, contact.getContactEmail())
                            .set(Contact::getContactNote, contact.getContactNote())
                            .set(Contact::getGmtModified, nowTime)
                            .set(Contact::getModifiedBy, String.valueOf(UserInfoUtils.getSysUserId()))
                            .eq(Contact::getCompanyCode, companyCode)
                    ;
                    contactMapper.update(null, contactLambdaUpdateWrapper);
                }
            });//更新之前有的
            List<Long> delIds = new ArrayList<>(); //修改时删除的
            contactList.forEach(contact -> {
                long count = newIds.stream().filter(id -> Objects.equals(contact.getId(), id)).count();
                if (count < 1) {
                    delIds.add(contact.getId());
                }
            });
            delIds.forEach(id -> {
                contactLambdaUpdateWrapper.clear();
                contactLambdaUpdateWrapper.eq(Contact::getId, id)
                        .set(Contact::getIsDeleted, 1)
                        .set(Contact::getGmtModified, nowTime)
                        .set(Contact::getModifiedBy, String.valueOf(UserInfoUtils.getSysUserId()))
                ;
                contactMapper.update(null, contactLambdaUpdateWrapper);
            });

            List<Address> oldAddressList = addressMapper.selectList(new LambdaQueryWrapper<Address>()
                    .eq(Address::getCompanyCode, companyCode)
                    .eq(Address::getType, Util.TYPE_ADDRESS_BP)
                    .eq(Address::getSubType, Util.SUB_TYPE_ADDRESS_BP_SHIPTO)
                    .eq(Address::getReferenceKey, businessPartner.getBpNumber()));

            if (CollectionUtils.isNotEmpty(businessPartner.getOfficeList()) && businessPartner.getOfficeList().size() > 1) {
                throw new RuntimeException("Office Address only one");
            }
            if (CollectionUtils.isNotEmpty(businessPartner.getBilltoList()) && businessPartner.getBilltoList().size() > 1) {
                throw new RuntimeException("Billing Address only one");
            }
            //modify address
            updateAddress(businessPartner.getOfficeList(), nowTime, companyCode, Util.SUB_TYPE_ADDRESS_BP_OFFICE, businessPartner.getBpNumber());
            updateAddress(businessPartner.getBilltoList(), nowTime, companyCode, Util.SUB_TYPE_ADDRESS_BP_BILLTO, businessPartner.getBpNumber());
            updateAddress(businessPartner.getShiptoList(), oldAddressList, companyCode, businessPartner.getBpNumber(), nowTime);
            return businessPartnerMapper.update(null, businessPartnerLambdaUpdateWrapper);
        } catch (Exception e) {
            log.error("modify bp failed", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveContact(Contact contact, String companyCode, String bpNumber, Date nowTime) {
        try {
            Contact cont = new Contact();
            cont.setCompanyCode(companyCode);
            cont.setBpNumber(bpNumber);
            cont.setContactType(contact.getContactType());
            cont.setContactPerson(contact.getContactPerson());
            cont.setContactTel(contact.getContactTel());
            cont.setContactEmail(contact.getContactEmail());
            cont.setContactNote(contact.getContactNote());
            cont.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
            cont.setGmtCreate(nowTime);
            cont.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
            cont.setGmtModified(nowTime);
            contactMapper.insert(cont);
        } catch (Exception e) {
            log.error("create bp Contact failed", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(List<AddressVO> addressList, Date nowTime, String companyCode, String subtype, String bpNumber) {
        try {
            LambdaUpdateWrapper<Address> addressLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            addressList.forEach(addressVO -> {
                addressLambdaUpdateWrapper.clear();
                if (null == addressVO.getId()) {
                    Address address = new Address();
                    address.setCompanyCode(companyCode);
                    address.setType(Util.TYPE_ADDRESS_BP);
                    address.setSubType(subtype);
                    address.setReferenceKey(bpNumber);
                    address.setAddress1(addressVO.getStreet());
                    address.setCity(addressVO.getCity());
                    address.setRegionCode(addressVO.getProvince());
                    address.setCountryCode(addressVO.getCountry());
                    address.setPostalCode(addressVO.getPostCode());
                    address.setIsDefault(addressVO.getIsDefault());
                    address.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                    address.setGmtCreate(nowTime);
                    address.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
                    address.setGmtModified(nowTime);
                    addressMapper.insert(address);
                } else {
                    addressLambdaUpdateWrapper.eq(Address::getId, addressVO.getId())
                            .set(Address::getAddress1, addressVO.getStreet())
                            .set(Address::getCity, addressVO.getCity())
                            .set(Address::getRegionCode, addressVO.getProvince())
                            .set(Address::getCountryCode, addressVO.getCountry())
                            .set(Address::getPostalCode, addressVO.getPostCode())
                            .set(Address::getGmtModified, nowTime)
                            .set(Address::getModifiedBy, String.valueOf(UserInfoUtils.getSysUserId()))
                    ;
                    addressMapper.update(null, addressLambdaUpdateWrapper);
                }
            });
        } catch (Exception e) {
            log.error("update bp address failed", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(List<AddressVO> addressList, List<Address> oldAddressList, String companyCode, String bpNumber, Date nowTime) {
        try {
            LambdaUpdateWrapper<Address> addressLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            addressList.forEach(addressVO -> {
                addressLambdaUpdateWrapper.clear();
                if (addressVO.getId() == null) {
                    saveAddress(new ArrayList<AddressVO>() {{
                        add(addressVO);
                    }}, companyCode, bpNumber, Util.SUB_TYPE_ADDRESS_BP_SHIPTO, nowTime);
                } else {
                    addressLambdaUpdateWrapper.eq(Address::getId, addressVO.getId())
                            .set(Address::getAddress1, addressVO.getStreet())
                            .set(Address::getCity, addressVO.getCity())
                            .set(Address::getRegionCode, addressVO.getProvince())
                            .set(Address::getCountryCode, addressVO.getCountry())
                            .set(Address::getPostalCode, addressVO.getPostCode())
                            .set(Address::getIsDefault, addressVO.getIsDefault())
                            .set(Address::getGmtModified, nowTime)
                            .set(Address::getModifiedBy, String.valueOf(UserInfoUtils.getSysUserId()))
                    ;
                    addressMapper.update(null, addressLambdaUpdateWrapper);
                }
            });
            List<Long> newIds = addressList.stream().map(AddressVO::getId).collect(Collectors.toList());
            newIds.removeIf(Objects::isNull);
            List<Long> delIds = new ArrayList<>(); //修改时删除的
            oldAddressList.forEach(addressVO -> {
                long count = newIds.stream().filter(id -> Objects.equals(addressVO.getId(), id)).count();
                if (count < 1) {
                    delIds.add(addressVO.getId());
                }
            });
            delIds.forEach(id -> {
                addressLambdaUpdateWrapper.clear();
                addressLambdaUpdateWrapper.eq(Address::getId, id)
                        .set(Address::getIsDeleted, 1)
                        .set(Address::getGmtModified, nowTime)
                        .set(Address::getModifiedBy, String.valueOf(UserInfoUtils.getSysUserId()))
                ;
                addressMapper.update(null, addressLambdaUpdateWrapper);
            });
        } catch (Exception e) {
            log.error("update bp address failed", e);
            throw new RuntimeException(e);
        }
    }

    public List<BusinessPartner> getList(BPListVO bpListVO) {
        try {
            startPage();
            LambdaQueryWrapper<BusinessPartner> businessPartnerLambdaQueryWrapper = new LambdaQueryWrapper<>();
            businessPartnerLambdaQueryWrapper.eq(BusinessPartner::getCompanyCode, bpListVO.getCompanyCode());
            businessPartnerLambdaQueryWrapper.eq(BusinessPartner::getIsBlock, "0");
            if (StringUtils.isNotBlank(bpListVO.getSearchText())) {
                businessPartnerLambdaQueryWrapper.and(bpWrapper ->
                        bpWrapper.like(BusinessPartner::getBpName, bpListVO.getSearchText())
                                .or()
                                .like(BusinessPartner::getBpTel, bpListVO.getSearchText())
                                .or()
                                .like(BusinessPartner::getBpEmail, bpListVO.getSearchText()));
            }
            businessPartnerLambdaQueryWrapper.orderByDesc(BusinessPartner::getGmtCreate);
            //执行分页查询
            List<BusinessPartner> businessPartnerList = businessPartnerMapper.selectList(businessPartnerLambdaQueryWrapper);

            //查询Contact
            LambdaQueryWrapper<Contact> contactLambdaQueryWrapper = new LambdaQueryWrapper<Contact>()
                    .eq(Contact::getCompanyCode, bpListVO.getCompanyCode())
                    .eq(Contact::getIsDeleted, 0);
            List<Contact> contactList = contactMapper.selectList(contactLambdaQueryWrapper);
            log.info(">>>contactList:{}", contactList);

            //查询Address 信息  包括OfficeList BilltoList ShiptoList
            LambdaQueryWrapper<Address> addressLambdaQueryWrapper = new LambdaQueryWrapper<Address>()
                    .eq(Address::getCompanyCode, bpListVO.getCompanyCode())
                    .eq(Address::getType, Util.TYPE_ADDRESS_BP)
                    .eq(Address::getIsDeleted, 0);
            List<Address> addressList = addressMapper.selectList(addressLambdaQueryWrapper);
            log.info(">>>>>addressList:{}", addressList);

            businessPartnerList.stream().forEach(bp -> {
                List<Contact> contactLists = new ArrayList<>();
                for (Contact contact : contactList
                ) {
                    if (bp.getBpNumber().equals(contact.getBpNumber())) {
                        contactLists.add(contact);
                    }
                }
                bp.setContactList(contactLists);


                List<AddressVO> officeList = new ArrayList<>();
                List<AddressVO> billtoList = new ArrayList<>();
                List<AddressVO> shiptoList = new ArrayList<>();
                for (Address address : addressList
                ) {
                    if (bp.getBpNumber().equals(address.getReferenceKey())) {
                        log.info(">>>>>address.getSubType():{}",address.getSubType());
                        switch (address.getSubType()) {
                            case Util.SUB_TYPE_ADDRESS_BP_OFFICE:
                                officeList.add(pickAddressVoParams(address));
                                break;
                            case Util.SUB_TYPE_ADDRESS_BP_BILLTO:

                                billtoList.add(pickAddressVoParams(address));
                                break;
                            case Util.SUB_TYPE_ADDRESS_BP_SHIPTO:
                                shiptoList.add(pickAddressVoParams(address));
                                break;
                            default:
                                throw new ServiceException("bp address类型不存在");
                        }

                    }
                }
                bp.setOfficeList(officeList);
                bp.setBilltoList(billtoList);
                bp.setShiptoList(shiptoList);
            });
            return businessPartnerList;
        } catch (Exception e) {
            log.error("get bp list failed", e);
            throw new RuntimeException(e);
        }
    }

    public AddressVO pickAddressVoParams(Address address) {
        AddressVO addressVO = new AddressVO();
        addressVO.setId(address.getId());
        addressVO.setCity(address.getCity());
        addressVO.setCountry(address.getCountryCode());
        addressVO.setProvince(address.getRegionCode());
        addressVO.setStreet(address.getAddress1());
        addressVO.setPostCode(address.getPostalCode());
        addressVO.setIsDefault(address.getIsDefault());
        return addressVO;
    }

    public BusinessPartner getBp(String bpNumber, String companyCode) {
        try {
            BusinessPartner businessPartner = businessPartnerMapper.selectOne(new LambdaQueryWrapper<BusinessPartner>()
                    .eq(BusinessPartner::getBpNumber, bpNumber)
                    .eq(BusinessPartner::getCompanyCode, companyCode));
            List<Contact> contactList = contactMapper.selectList(new LambdaQueryWrapper<Contact>()
                    .eq(Contact::getBpNumber, bpNumber)
                    .eq(Contact::getCompanyCode, companyCode)
                    .eq(Contact::getIsDeleted, 0));
            List<Address> addressList = addressMapper.selectList(new LambdaQueryWrapper<Address>()
                    .eq(Address::getReferenceKey, bpNumber)
                    .eq(Address::getCompanyCode, companyCode)
                    .eq(Address::getType, Util.TYPE_ADDRESS_BP)
                    .eq(Address::getIsDeleted, 0));
            businessPartner.setContactList(contactList);
            businessPartner.setOfficeList(generateList(addressList, Util.SUB_TYPE_ADDRESS_BP_OFFICE));
            businessPartner.setBilltoList(generateList(addressList, Util.SUB_TYPE_ADDRESS_BP_BILLTO));
            businessPartner.setShiptoList(generateList(addressList, Util.SUB_TYPE_ADDRESS_BP_SHIPTO));
            return businessPartner;
        } catch (Exception e) {
            log.error("get one bp failed", e);
            throw new RuntimeException(e);
        }
    }

    private List<AddressVO> generateList(List<Address> addressList, String subType) {
        try {
            List<AddressVO> list = new ArrayList<>();
            List<Address> subAddressList = addressList.stream().filter(address -> subType.equals(address.getSubType())).collect(Collectors.toList());
            subAddressList.forEach(address -> {
                AddressVO addressVO = new AddressVO();
                addressVO.setId(address.getId());
                addressVO.setCity(address.getCity());
                addressVO.setCountry(address.getCountryCode());
                addressVO.setProvince(address.getRegionCode());
                addressVO.setStreet(address.getAddress1());
                addressVO.setPostCode(address.getPostalCode());
                addressVO.setIsDefault(address.getIsDefault());
                list.add(addressVO);
            });
            return list;
        } catch (Exception e) {
            log.error("generate bp address list failed", e);
            throw new RuntimeException(e);
        }
    }

    public BusinessPartner getBpNameByBpNumber(String companyCode, String bpNumber) {
        QueryWrapper<BusinessPartner> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("company_code", companyCode);
        queryWrapper.eq("bp_number", bpNumber);
        queryWrapper.eq("is_block", 0);
        return businessPartnerMapper.selectOne(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void importBP(MultipartFile file, String companyCode) {
        try {
            List<BusinessPartner> businessPartnerList = new ArrayList<>();
            List<Object[]> objects = ExcelUtil.readExcelToObj(file.getInputStream(), 19);
            objects.remove(0);
            objects.remove(0);
            for (int i = 0; i < objects.size(); i++) {
                Object[] object = objects.get(i);
                if (StringUtils.isBlank((String) object[0])) {
                    throw new RuntimeException("Line " + (i + 3) + " Business Partner Name is a required field");
                }
                BusinessPartner businessPartner = new BusinessPartner();
                businessPartner.setCompanyCode(companyCode);
                businessPartner.setBpName((String) object[0]);
                businessPartner.setBpTel((String) object[1]);
                businessPartner.setBpEmail((String) object[2]);
                businessPartner.setBpContact((String) object[3]);

                if (isOneNotBlank((String) object[4],
                        (String) object[5], (String) object[6],
                        (String) object[7], (String) object[8])) {
                    if (StringUtils.isNotBlank((String) object[8]) && checkPostCode((String) object[8], (String) object[7])) {
                        throw new RuntimeException("Line " + (i + 3) + " Column 9 Postal Code Format Error");
                    }
                    businessPartner.setOfficeList(new ArrayList<AddressVO>() {{
                        add(new AddressVO() {{
                            setStreet((String) object[4]);
                            setCity((String) object[5]);
                            setProvince((String) object[6]);
                            setCountry((String) object[7]);
                            setPostCode((String) object[8]);
                        }});
                    }});
                }

                if (isOneNotBlank((String) object[9],
                        (String) object[10], (String) object[11],
                        (String) object[12], (String) object[13])) {
                    if (StringUtils.isNotBlank((String) object[13]) && checkPostCode((String) object[13], (String) object[12])) {
                        throw new RuntimeException("Line " + (i + 3) + " Column 14 Postal Code Format Error");
                    }
                    businessPartner.setBilltoList(new ArrayList<AddressVO>() {{
                        add(new AddressVO() {{
                            setStreet((String) object[9]);
                            setCity((String) object[10]);
                            setProvince((String) object[11]);
                            setCountry((String) object[12]);
                            setPostCode((String) object[13]);
                        }});
                    }});

                }

                if (isOneNotBlank((String) object[14],
                        (String) object[15], (String) object[16],
                        (String) object[17], (String) object[18])) {
                    if (StringUtils.isNotBlank((String) object[18]) && checkPostCode((String) object[18], (String) object[17])) {
                        throw new RuntimeException("Line " + (i + 3) + " Column 19 Postal Code Format Error");
                    }
                    businessPartner.setShiptoList(new ArrayList<AddressVO>() {{
                        add(new AddressVO() {{
                            setStreet((String) object[14]);
                            setCity((String) object[15]);
                            setProvince((String) object[16]);
                            setCountry((String) object[17]);
                            setPostCode((String) object[18]);
                        }});
                    }});
                }
                try {
                    create(businessPartner);
                } catch (Exception e) {
                    throw new RuntimeException("Line " + (i + 3) + e.getMessage());
                }
                // businessPartnerList.add(businessPartner);
            }
            //businessPartnerList.forEach(this::create);
        } catch (Exception e) {
            log.error("import BP failed", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void importBP2(MultipartFile file, String companyCode) {
        try {
            ExcelUtil<ImportBusinessPartnerVo> util = new ExcelUtil<>(ImportBusinessPartnerVo.class);
            List<ImportBusinessPartnerVo> importBusinessPartnerVos = util.importExcel(file.getInputStream());
            importBusinessPartnerVos.forEach(bp -> {

            });
        } catch (Exception e) {
            log.error("import BP failed", e);
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

    public static boolean checkPostCode(String postCode, String country) {
        String reg = "^[a-zA-Z][0-9][a-zA-Z]\\s[0-9][a-zA-Z][0-9]$";
        if ("CA".equals(country)) {
            return !Pattern.matches(reg, postCode);
        } else if ("US".equals(country)) {
            return postCode.length() != 5 && postCode.length() != 10;
        }
        return false;
    }

    public JSONObject getBkList(String companyCode, String name) throws IOException {
        log.info(">>>>>bp list 入参, companyCode:{},name:{}", companyCode, name);

        Company company = companyService.getCompany(companyCode);
        JSONArray customerBp = bookKeepingService.bpCustomerListLike(company.getOrgidEx(), company.getCompanyCodeEx());
        //JSONArray vendorBp = bookKeepingService.bpVendorListList(companyCode, name);
        JSONObject res = new JSONObject();
        res.put("customer", customerBp);
        // res.put("vendor", vendorBp);
        return res;
    }
}
