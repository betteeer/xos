package com.inossem.oms.mdm.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.inossem.oms.api.bk.api.BookKeepingService;
import com.inossem.oms.base.svc.domain.*;
import com.inossem.oms.base.svc.domain.VO.AddressVO;
import com.inossem.oms.base.svc.domain.VO.SkuUomConversionVo;
import com.inossem.oms.base.svc.domain.VO.SkuVO;
import com.inossem.oms.base.svc.mapper.*;
import com.inossem.oms.base.utils.UserInfoUtils;
import com.inossem.oms.mdm.common.Util;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zoutong
 * @date 2022/10/15
 **/
@Service
@Slf4j
public class WmsApiService {

    @Resource
    private SkuMasterMapper skuMasterMapper;

    @Resource
    private SkuUomConversionMapper skuUomConversionMapper;

    @Resource
    private SkuCharacterMapper skuCharacterMapper;

    @Resource
    private SkuKittingMapper skuKittingMapper;

    @Resource
    private SkuBpMapper skuBpMapper;

    @Resource
    private SkuTariffMapper skuTariffMapper;

    @Resource
    private BusinessPartnerMapper businessPartnerMapper;

    @Resource
    private BookKeepingService bookKeepingService;

    @Resource
    private AddressMapper addressMapper;

    @Resource
    private ContactMapper contactMapper;

    @Resource
    private CompanyMapper companyMapper;


    @Transactional(rollbackFor = Exception.class)
    public synchronized SkuMaster createSku(SkuVO skuVO) {
        try {
            String companyCode = skuVO.getCompanyCode();
            //判断当前skuName是否已经存在
            List<SkuMaster> skuMasterList = skuMasterMapper.selectList(new LambdaQueryWrapper<SkuMaster>()
                    .eq(SkuMaster::getSkuNumberEx, skuVO.getSkuNumberEx())
                    .eq(SkuMaster::getCompanyCode, companyCode));
            if (skuMasterList.size() > 0) {
                throw new RuntimeException("sku name already exists");
            }

            //获取skuMaster中最新生成的skuNumber
            QueryWrapper<SkuMaster> skuMasterLambdaQueryWrapper = new QueryWrapper<SkuMaster>()
                    .select("max(sku_number+0) as skuNumber")
                    .eq("company_code",skuVO.getCompanyCode())
                    .eq("sku_system_generated",0);
            SkuMaster oldSkuMaster = skuMasterMapper.selectOne(skuMasterLambdaQueryWrapper);
            String oldSkuCode = "0";
            if (oldSkuMaster != null) {
                oldSkuCode = oldSkuMaster.getSkuNumber();
            }

            //封装skuMaster参数
            SkuMaster skuMaster = new SkuMaster();
            skuMaster.setCompanyCode(companyCode);
            skuMaster.setSkuNumber(new BigDecimal(oldSkuCode).add(BigDecimal.ONE).intValue() + "");
            skuMaster.setSkuName(skuVO.getSkuName());
            if (StringUtils.isNotBlank(skuVO.getSkuNumberEx())) {
                skuMaster.setSkuNumberEx(skuVO.getSkuNumberEx());
            }
            if (StringUtils.isNotBlank(skuVO.getSkuDescription())) {
                skuMaster.setSkuDescription(skuVO.getSkuDescription());
            }
            skuMaster.setBasicUom(skuVO.getBasicUom());
            Date nowTime = new Date();
            skuMaster.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
            skuMaster.setGmtCreate(nowTime);
            skuMaster.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
            skuMaster.setGmtModified(nowTime);
            skuMaster.setIsDeleted(0);
            if (StringUtils.isNotBlank(skuVO.getSkuGroupCode())) {
                skuMaster.setSkuGroupCode(skuVO.getSkuGroupCode());
            }
            // 20230114 新增，给外部wms等系统使用
            if (StringUtils.isNotBlank(skuVO.getSkuGroupName())) {
                skuMaster.setSkuGroupName(skuVO.getSkuGroupName());
            }
            if (StringUtils.isNotBlank(skuVO.getSkuOwnerCode())) {
                skuMaster.setSkuOwnerCode(skuVO.getSkuOwnerCode());
            }
            if (StringUtils.isNotBlank(skuVO.getFsnIndicator())) {
                skuMaster.setFsnIndicator(skuVO.getFsnIndicator());
            }
            if (StringUtils.isNotBlank(skuVO.getStorageIndicator())) {
                skuMaster.setStorageIndicator(skuVO.getStorageIndicator());
            }
            if (null != skuVO.getIsHuTraceable()) {
                skuMaster.setIsHuTraceable(skuVO.getIsHuTraceable());
            }
            if (StringUtils.isNotBlank(skuVO.getPreserveDescription())) {
                skuMaster.setPreserveDescription(skuVO.getPreserveDescription());
            }
            if (StringUtils.isNotBlank(skuVO.getDisposeDescription())) {
                skuMaster.setDisposeDescription(skuVO.getDisposeDescription());
            }
            skuMaster.setWmsIndicator(1);
            if (Util.TYPE_SERVICE_SKU.equals(skuVO.getSkuType())) {
                skuMaster.setSkuType(Util.TYPE_SERVICE_SKU);
            } else if (Util.TYPE_INVENTORY_SKU.equals(skuVO.getSkuType())) {
                skuMaster.setSkuType(Util.TYPE_INVENTORY_SKU);
                if (StringUtils.isNotBlank(skuVO.getUpc())) {
                    skuMaster.setUpcNumber(skuVO.getUpc());
                }
                if (CollectionUtils.isNotEmpty(skuVO.getCharacters())) {
                    saveSkuCharacter(companyCode, skuMaster.getSkuNumber(), skuVO.getCharacters(), nowTime);
                }
                skuMaster.setIsKitting(skuVO.getIsKitting());
                if (skuVO.getIsKitting() == 1 && CollectionUtils.isNotEmpty(skuVO.getKittingItems())) {
                    saveSkuKitting(skuMaster.getSkuNumber(), skuVO.getKittingItems(), nowTime, companyCode);
                }
                if (null != skuVO.getWeight()) {
                    skuMaster.setGrossWeight(skuVO.getWeight());
                }
                skuMaster.setWeightUom(skuVO.getWeightUom());
                if (null != skuVO.getWidth()) {
                    skuMaster.setWidth(skuVO.getWidth());
                }
                if (null != skuVO.getHeight()) {
                    skuMaster.setHeight(skuVO.getHeight());
                }
                if (null != skuVO.getLength()) {
                    skuMaster.setLength(skuVO.getLength());
                }
                skuMaster.setWhlUom(skuVO.getWhlUom());
                //skuMaster.setNetWeight();//todo 不知怎么计算
                if (null != skuVO.getWidth() && null != skuVO.getHeight() && null != skuVO.getLength()) {
                    BigDecimal grossWeight = skuMaster.getWidth().multiply(skuMaster.getHeight()).multiply(skuMaster.getLength());
                    skuMaster.setGrossVolume(grossWeight);
                }
                //skuMaster.setNetVolume();
                skuMaster.setVolumeUom(skuVO.getWhlUom());//todo 体积单位暂取
                if (null != skuVO.getSalesPrice()) {
                    skuMaster.setSalesPrice(skuVO.getSalesPrice());
                }
                skuMaster.setSalesUom(skuVO.getSalesUom());
                if (null != skuVO.getSalesBasicRate()) {
                    skuMaster.setSalesBasicRate(skuVO.getSalesBasicRate());
                }
                skuMaster.setPurchaseUom(skuVO.getPurchaseUom());
                if (null != skuVO.getPurchaseBasicRate()) {
                    skuMaster.setPurchaseBasicRate(skuVO.getPurchaseBasicRate());
                }
                if (CollectionUtils.isNotEmpty(skuVO.getBPDetails())) {
                    saveSkuBp(skuMaster.getSkuNumber(), skuVO.getBPDetails(), nowTime, companyCode);
                }
                if (CollectionUtils.isNotEmpty(skuVO.getFTradeDetails())) {
                    saveSkuForeignTrade(skuMaster.getSkuNumber(), skuVO.getFTradeDetails(), nowTime, companyCode);
                }
            } else {
                throw new RuntimeException("error sku type");
            }
            skuMasterMapper.insert(skuMaster);
            //新增skuMComConversion
            if (CollectionUtils.isNotEmpty(skuVO.getSkuUomConversionVoList())) {
                for (SkuUomConversionVo skuUomConversionVo : skuVO.getSkuUomConversionVoList()) {
                    SkuUomConversion skuUomConversion = new SkuUomConversion();
                    skuUomConversion.setConversionUom(skuUomConversionVo.getConversionUom());
                    skuUomConversion.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                    skuUomConversion.setSkuNumber(skuMaster.getSkuNumber());
                    skuUomConversion.setCompanyCode(skuMaster.getCompanyCode());
                    skuUomConversion.setBasicUom(skuUomConversionVo.getBasicUom());
                    skuUomConversion.setCreateTime(nowTime);
                    skuUomConversion.setNumerator(skuUomConversionVo.getNumerator());
                    skuUomConversion.setUpdateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                    skuUomConversion.setUpdateTime(nowTime);
                    skuUomConversionMapper.insert(skuUomConversion);
                }
            }
            return skuMaster;
        } catch (Exception e) {
            log.error("create {} sku failed", skuVO.getSkuType(), e);
            throw new RuntimeException(e);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void saveSkuCharacter(String companyCode, String skuCode, List<SkuCharacter> characters, Date nowTime) {
        try {
            characters.forEach(character -> {
                SkuCharacter skuCharacter = new SkuCharacter();
                skuCharacter.setCompanyCode(companyCode);
                skuCharacter.setSkuNumber(skuCode);
                skuCharacter.setCharacterValue(character.getCharacterValue());
                skuCharacter.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                skuCharacter.setGmtCreate(nowTime);
                skuCharacter.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
                skuCharacter.setGmtModified(nowTime);
                skuCharacter.setIsDeleted(0);
                skuCharacterMapper.insert(skuCharacter);
            });
        } catch (Exception e) {
            log.error("Save SkuCharacter failed", e);
            throw new RuntimeException(e);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void saveSkuKitting(String skuCode, List<SkuKitting> kittingItems, Date nowTime, String companyCode) {
        try {
            kittingItems.forEach(item -> {
                SkuKitting skuKitting = new SkuKitting();
                skuKitting.setCompanyCode(companyCode);
                skuKitting.setKittingSku(skuCode);
                skuKitting.setVersion(1);
                skuKitting.setComponentLine(item.getComponentLine());
                skuKitting.setComponentSku(item.getComponentSku());
                skuKitting.setComponentQty(item.getComponentQty());
                skuKitting.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                skuKitting.setGmtCreate(nowTime);
                skuKitting.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
                skuKitting.setGmtModified(nowTime);
                skuKitting.setIsDeleted(0);
                skuKittingMapper.insert(skuKitting);
            });
        } catch (Exception e) {
            log.error("Save SkuKitting failed", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveSkuBp(String skuCode, List<SkuBp> bpDetails, Date nowTime, String companyCode) {
        try {
            bpDetails.forEach(bp -> {
                SkuBp skuBp = new SkuBp();
                skuBp.setCompanyCode(companyCode);
                skuBp.setSkuNumber(skuCode);
                skuBp.setBpNumber(bp.getBpNumber());
                skuBp.setRefCode(bp.getRefCode());
                skuBp.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                skuBp.setCreateTime(nowTime);
                skuBp.setUpdateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                skuBp.setUpdateTime(nowTime);
                skuBp.setIsDeleted(0);
                skuBpMapper.insert(skuBp);
            });
        } catch (Exception e) {
            log.error("Save SkuBp failed", e);
            throw new RuntimeException(e);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void saveSkuForeignTrade(String skuCode, List<SkuTariff> FTradeDetails, Date nowTime, String companyCode) {
        try {
            FTradeDetails.forEach(detail -> {
                SkuTariff skuTariff = new SkuTariff();
                skuTariff.setCompanyCode(companyCode);
                skuTariff.setSkuNumber(skuCode);
                skuTariff.setCountryCode(detail.getCountryCode());
                skuTariff.setTariffCode(detail.getTariffCode());
                skuTariff.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                skuTariff.setGmtCreate(nowTime);
                skuTariff.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
                skuTariff.setGmtModified(nowTime);
                skuTariff.setIsDeleted(0);
                skuTariffMapper.insert(skuTariff);
            });
        } catch (Exception e) {
            log.error("Save SkuForeignTrade failed", e);
            throw new RuntimeException(e);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public int modifySku(SkuVO skuVO) {
        try {
            String companyCode = skuVO.getCompanyCode();
            //校验当前skuName是否存在
            SkuMaster skuMaster = skuMasterMapper.selectOne(new LambdaQueryWrapper<SkuMaster>()
                    .eq(SkuMaster::getSkuNumberEx, skuVO.getSkuNumberEx())
                    .eq(SkuMaster::getCompanyCode, companyCode));

            if (skuMaster == null) {
                throw new RuntimeException("该skuNumberEx系统中不存在,不允许修改");
            }
            Date nowTime = new Date();
            LambdaUpdateWrapper<SkuMaster> skuMasterLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            skuMasterLambdaUpdateWrapper
                    .set(SkuMaster::getSkuName, skuVO.getSkuName())
                    .set(SkuMaster::getBasicUom, skuVO.getBasicUom())
                    .set(SkuMaster::getGmtModified, nowTime)
                    .set(SkuMaster::getModifiedBy, String.valueOf(UserInfoUtils.getSysUserId()))
                    .eq(SkuMaster::getId, skuMaster.getId());
            if (StringUtils.isNotBlank(skuVO.getSkuDescription())) {
                skuMasterLambdaUpdateWrapper.set(SkuMaster::getSkuDescription, skuVO.getSkuDescription());
            }
            if (StringUtils.isNotBlank(skuVO.getSkuNumberEx())) {
                skuMasterLambdaUpdateWrapper.set(SkuMaster::getSkuNumberEx, skuVO.getSkuNumberEx());
            }
            if (StringUtils.isNotBlank(skuVO.getSkuGroupCode())) {
                skuMasterLambdaUpdateWrapper.set(SkuMaster::getSkuGroupCode, skuVO.getSkuGroupCode());
            }
            if (StringUtils.isNotBlank(skuVO.getSkuGroupName())) {
                skuMasterLambdaUpdateWrapper.set(SkuMaster::getSkuGroupName, skuVO.getSkuGroupName());
            }
            if (StringUtils.isNotBlank(skuVO.getSkuOwnerCode())) {
                skuMasterLambdaUpdateWrapper.set(SkuMaster::getSkuOwnerCode, skuVO.getSkuOwnerCode());
            }
            if (StringUtils.isNotBlank(skuVO.getFsnIndicator())) {
                skuMasterLambdaUpdateWrapper.set(SkuMaster::getFsnIndicator, skuVO.getFsnIndicator());
            }
            if (StringUtils.isNotBlank(skuVO.getStorageIndicator())) {
                skuMasterLambdaUpdateWrapper.set(SkuMaster::getStorageIndicator, skuVO.getStorageIndicator());
            }
            if (null != skuVO.getIsHuTraceable()) {
                skuMasterLambdaUpdateWrapper.set(SkuMaster::getIsHuTraceable, skuVO.getIsHuTraceable());
            }
            if (null != skuVO.getIsDelete()) {
                skuMasterLambdaUpdateWrapper.set(SkuMaster::getIsDeleted, skuVO.getIsDelete());
            }
            if (StringUtils.isNotBlank(skuVO.getPreserveDescription())) {
                skuMasterLambdaUpdateWrapper.set(SkuMaster::getPreserveDescription, skuVO.getPreserveDescription());
            }
            if (StringUtils.isNotBlank(skuVO.getDisposeDescription())) {
                skuMasterLambdaUpdateWrapper.set(SkuMaster::getDisposeDescription, skuVO.getDisposeDescription());
            }
            skuMasterLambdaUpdateWrapper.set(SkuMaster::getWmsIndicator, 1);
            if (Util.TYPE_INVENTORY_SKU.equals(skuVO.getSkuType())) {
                if (null != skuVO.getWeight() && null != skuVO.getHeight() && null != skuVO.getLength()) {
                    BigDecimal grossWeight = skuVO.getWeight()
                            .multiply(skuVO.getHeight())
                            .multiply(skuVO.getLength());
                    skuMasterLambdaUpdateWrapper.set(SkuMaster::getGrossVolume, grossWeight);
                }

                skuMasterLambdaUpdateWrapper
                        .set(SkuMaster::getUpcNumber, skuVO.getUpc())
                        .set(SkuMaster::getIsKitting, skuVO.getIsKitting())
                        .set(SkuMaster::getWidth, skuVO.getWidth())
                        .set(SkuMaster::getHeight, skuVO.getHeight())
                        .set(SkuMaster::getLength, skuVO.getLength())
                        .set(SkuMaster::getWhlUom, skuVO.getWhlUom())
                        .set(SkuMaster::getGrossWeight, skuVO.getWeight())
                        .set(SkuMaster::getWeightUom, skuVO.getWeightUom())
                        //.setNetWeight();//todo 不知怎么计算
                        //.setNetVolume();
                        .set(SkuMaster::getVolumeUom, skuVO.getWhlUom())//todo 体积单位暂取长宽高的单位
                        .set(SkuMaster::getSalesPrice, skuVO.getSalesPrice())
                        .set(SkuMaster::getSalesUom, skuVO.getSalesUom())
                        .set(SkuMaster::getSalesBasicRate, skuVO.getSalesBasicRate())
                        .set(SkuMaster::getPurchaseUom, skuVO.getPurchaseUom())
                        .set(SkuMaster::getPurchaseBasicRate, skuVO.getPurchaseBasicRate());
                if (CollectionUtils.isNotEmpty(skuVO.getCharacters())) {
                    modifySkuCharacter(skuMaster.getSkuNumber(), skuVO.getCharacters(), nowTime, companyCode);
                }
                if (skuVO.getIsKitting() == 1) {
                    if (CollectionUtils.isNotEmpty(skuVO.getKittingItems())) {
                        modifySkuKitting(skuMaster.getSkuNumber(), skuVO.getKittingItems(), nowTime, companyCode);
                    }
                }
                if (CollectionUtils.isNotEmpty(skuVO.getBPDetails())) {
                    modifySkuBp(skuMaster.getSkuNumber(), skuVO.getBPDetails(), nowTime, companyCode);
                }
                if (CollectionUtils.isNotEmpty(skuVO.getFTradeDetails())) {
                    modifySkuForeignTrade(skuMaster.getSkuNumber(), skuVO.getFTradeDetails(), nowTime, companyCode);
                }
            }
            //修改skuMComConversion(先删除在新增)
            LambdaQueryWrapper<SkuUomConversion> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SkuUomConversion::getSkuNumber, skuMaster.getSkuNumber());
            wrapper.eq(SkuUomConversion::getCompanyCode, skuMaster.getCompanyCode());
            skuUomConversionMapper.delete(wrapper);
            if (CollectionUtils.isNotEmpty(skuVO.getSkuUomConversionVoList())) {
                for (SkuUomConversionVo skuUomConversionVo : skuVO.getSkuUomConversionVoList()) {
                    SkuUomConversion skuUomConversion = new SkuUomConversion();
                    skuUomConversion.setConversionUom(skuUomConversionVo.getConversionUom());
                    skuUomConversion.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                    skuUomConversion.setSkuNumber(skuMaster.getSkuNumber());
                    skuUomConversion.setCompanyCode(skuMaster.getCompanyCode());
                    skuUomConversion.setBasicUom(skuUomConversionVo.getBasicUom());
                    skuUomConversion.setCreateTime(nowTime);
                    skuUomConversion.setNumerator(skuUomConversionVo.getNumerator());
                    skuUomConversion.setUpdateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                    skuUomConversion.setUpdateTime(nowTime);
                    skuUomConversionMapper.insert(skuUomConversion);
                }
            }
            return skuMasterMapper.update(null, skuMasterLambdaUpdateWrapper);
        } catch (Exception e) {
            log.error("modify {} sku failed", skuVO.getSkuType(), e);
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int createOrModifySku(SkuVO skuVO) {
        try {
            String companyCode = skuVO.getCompanyCode();
            //校验当前skuName是否存在
            SkuMaster skuMaster = skuMasterMapper.selectOne(new LambdaQueryWrapper<SkuMaster>()
                    .eq(SkuMaster::getSkuNumberEx, skuVO.getSkuNumberEx())
                    .eq(SkuMaster::getCompanyCode, companyCode));

            if (skuMaster == null) {
                log.info("sku 不存在，调用create 方法");
                return createSku(skuVO) == null ? 0 : 1;
            } else {
                return modifySku(skuVO);
            }
        } catch (Exception e) {
            log.error("modify sku failed", e);
            throw new RuntimeException(e);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void modifySkuCharacter(String skuCode, List<SkuCharacter> characters, Date nowTime, String companyCode) {
        try {
            List<SkuCharacter> oldSkuCharacters = skuCharacterMapper.selectList(new LambdaQueryWrapper<SkuCharacter>()
                    .eq(SkuCharacter::getSkuNumber, skuCode));
            LambdaUpdateWrapper<SkuCharacter> skuCharacterLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            characters.forEach(character -> {
                if (character.getId() == null) {
                    SkuCharacter skuCharacter = new SkuCharacter();
                    skuCharacter.setCompanyCode(companyCode);
                    skuCharacter.setSkuNumber(skuCode);
                    skuCharacter.setCharacterValue(character.getCharacterValue());
                    skuCharacter.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                    skuCharacter.setGmtCreate(nowTime);
                    skuCharacter.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
                    skuCharacter.setGmtModified(nowTime);
                    skuCharacter.setIsDeleted(0);
                    skuCharacterMapper.insert(skuCharacter);
                } else {
                    skuCharacterLambdaUpdateWrapper.eq(SkuCharacter::getId, character.getId())
                            .set(SkuCharacter::getCharacterValue, character.getCharacterValue())
                            .set(SkuCharacter::getModifiedBy, String.valueOf(UserInfoUtils.getSysUserId()))
                            //.set(SkuCharacter::getModifiedBy, "test 01")
                            .set(SkuCharacter::getGmtModified, nowTime);
                    skuCharacterMapper.update(null, skuCharacterLambdaUpdateWrapper);
                }
            });

            List<Long> newIds = characters.stream().map(SkuCharacter::getId).collect(Collectors.toList());
            newIds.removeIf(Objects::isNull);
            List<Long> delIds = new ArrayList<>(); //修改时删除的
            oldSkuCharacters.forEach(skuCharacter -> {
                long count = newIds.stream().filter(id -> Objects.equals(skuCharacter.getId(), id)).count();
                if (count < 1) {
                    delIds.add(skuCharacter.getId());
                }
            });
            delIds.forEach(id -> {
                skuCharacterLambdaUpdateWrapper.clear();
                skuCharacterLambdaUpdateWrapper.eq(SkuCharacter::getId, id)
                        .set(SkuCharacter::getIsDeleted, 1)
                        .eq(SkuCharacter::getModifiedBy, String.valueOf(UserInfoUtils.getSysUserId()))
                ;
                skuCharacterMapper.update(null, skuCharacterLambdaUpdateWrapper);
            });
        } catch (Exception e) {
            log.error("Modify SkuCharacter failed", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void modifySkuKitting(String skuCode, List<SkuKitting> kittingItems, Date nowTime, String companyCode) {
        try {
            SkuKitting oldSkuKitting = skuKittingMapper.selectOne(new LambdaQueryWrapper<SkuKitting>()
                    .eq(SkuKitting::getKittingSku, skuCode)
                    .orderByDesc(SkuKitting::getVersion)
                    .last("limit 1"));
            int oldVersion = oldSkuKitting.getVersion();
            kittingItems.forEach(item -> {
                SkuKitting skuKitting = new SkuKitting();
                skuKitting.setCompanyCode(companyCode);
                skuKitting.setKittingSku(skuCode);
                skuKitting.setVersion(oldVersion + 1);
                skuKitting.setComponentLine(item.getComponentLine());
                skuKitting.setComponentSku(item.getComponentSku());
                skuKitting.setComponentQty(item.getComponentQty());
                skuKitting.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                skuKitting.setGmtCreate(nowTime);
                skuKitting.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
                skuKitting.setGmtModified(nowTime);
                skuKitting.setIsDeleted(0);
                skuKittingMapper.insert(skuKitting);
            });
        } catch (Exception e) {
            log.error("Modify SkuKitting failed", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void modifySkuBp(String skuCode, List<SkuBp> bpDetails, Date nowTime, String companyCode) {
        try {
            List<SkuBp> oldSkuBpList = skuBpMapper.selectList(new LambdaQueryWrapper<SkuBp>()
                    .eq(SkuBp::getSkuNumber, skuCode));
            LambdaUpdateWrapper<SkuBp> skuBpLambdaUpdateWrapper = new LambdaUpdateWrapper<>();

            bpDetails.forEach(bp -> {
                skuBpLambdaUpdateWrapper.clear();
                if (bp.getId() == null) {
                    SkuBp skuBp = new SkuBp();
                    skuBp.setCompanyCode(companyCode);
                    skuBp.setSkuNumber(skuCode);
                    skuBp.setBpNumber(bp.getBpNumber());
                    skuBp.setRefCode(bp.getRefCode());
                    skuBp.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                    skuBp.setCreateTime(nowTime);
                    skuBp.setUpdateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                    skuBp.setUpdateTime(nowTime);
                    skuBp.setIsDeleted(0);
                    skuBpMapper.insert(skuBp);
                } else {
                    skuBpLambdaUpdateWrapper
                            .eq(SkuBp::getId, bp.getId())
                            .set(SkuBp::getBpNumber, bp.getBpNumber())
                            .set(SkuBp::getRefCode, bp.getRefCode())
                            .set(SkuBp::getUpdateBy, String.valueOf(UserInfoUtils.getSysUserId()))
                            //.set(SkuBp::getUpdateBy, "test 01")
                            .set(SkuBp::getUpdateTime, nowTime);
                    skuBpMapper.update(null, skuBpLambdaUpdateWrapper);
                }
            });

            List<Long> newIds = bpDetails.stream().map(SkuBp::getId).collect(Collectors.toList());
            newIds.removeIf(Objects::isNull);
            List<Long> delIds = new ArrayList<>(); //修改时删除的
            oldSkuBpList.forEach(skuBp -> {
                long count = newIds.stream().filter(id -> Objects.equals(skuBp.getId(), id)).count();
                if (count < 1) {
                    delIds.add(skuBp.getId());
                }
            });
            delIds.forEach(id -> {
                skuBpLambdaUpdateWrapper.clear();
                skuBpLambdaUpdateWrapper.eq(SkuBp::getId, id)
                        .set(SkuBp::getIsDeleted, 1)
                        .set(SkuBp::getUpdateTime, nowTime)
                        .set(SkuBp::getUpdateBy, String.valueOf(UserInfoUtils.getSysUserId()))
                ;
                skuBpMapper.update(null, skuBpLambdaUpdateWrapper);
            });
        } catch (Exception e) {
            log.error("Modify SkuBp failed", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void modifySkuForeignTrade(String skuCode, List<SkuTariff> FTradeDetails, Date nowTime, String companyCode) {
        try {
            List<SkuTariff> oldSkuTariffs = skuTariffMapper.selectList(new LambdaQueryWrapper<SkuTariff>()
                    .eq(SkuTariff::getSkuNumber, skuCode));
            LambdaUpdateWrapper<SkuTariff> skuTariffLambdaUpdateWrapper = new LambdaUpdateWrapper<>();

            FTradeDetails.forEach(detail -> {
                skuTariffLambdaUpdateWrapper.clear();
                if (detail.getId() == null) {
                    SkuTariff skuTariff = new SkuTariff();
                    skuTariff.setCompanyCode(companyCode);
                    skuTariff.setSkuNumber(skuCode);
                    skuTariff.setCountryCode(detail.getCountryCode());
                    skuTariff.setTariffCode(detail.getTariffCode());
                    skuTariff.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                    skuTariff.setGmtCreate(nowTime);
                    skuTariff.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
                    skuTariff.setGmtModified(nowTime);
                    skuTariff.setIsDeleted(0);
                    skuTariffMapper.insert(skuTariff);
                } else {
                    skuTariffLambdaUpdateWrapper
                            .eq(SkuTariff::getId, detail.getId())
                            .set(SkuTariff::getCountryCode, detail.getCountryCode())
                            .set(SkuTariff::getTariffCode, detail.getTariffCode())
                            .set(SkuTariff::getModifiedBy, String.valueOf(UserInfoUtils.getSysUserId()))
                            //.set(SkuTariff::getModifiedBy, "test 01")
                            .set(SkuTariff::getGmtModified, nowTime);
                    skuTariffMapper.update(null, skuTariffLambdaUpdateWrapper);
                }
            });

            List<Long> newIds = FTradeDetails.stream().map(SkuTariff::getId).collect(Collectors.toList());
            newIds.removeIf(Objects::isNull);
            List<Long> delIds = new ArrayList<>(); //修改时删除的
            oldSkuTariffs.forEach(skuBp -> {
                long count = newIds.stream().filter(id -> Objects.equals(skuBp.getId(), id)).count();
                if (count < 1) {
                    delIds.add(skuBp.getId());
                }
            });
            delIds.forEach(id -> {
                skuTariffLambdaUpdateWrapper.clear();
                skuTariffLambdaUpdateWrapper.eq(SkuTariff::getId, id)
                        .set(SkuTariff::getIsDeleted, 1)
                        .set(SkuTariff::getGmtModified, nowTime)
                        .set(SkuTariff::getModifiedBy, String.valueOf(UserInfoUtils.getSysUserId()))
                ;
                skuTariffMapper.update(null, skuTariffLambdaUpdateWrapper);
            });
        } catch (Exception e) {
            log.error("Modify SkuForeignTrade failed", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public BusinessPartner createBp(BusinessPartner businessPartner) {
        try {
            String companyCode = businessPartner.getCompanyCode();
            List<BusinessPartner> businessPartnerList = businessPartnerMapper.selectList(new LambdaQueryWrapper<BusinessPartner>()
                    .eq(BusinessPartner::getBpNumberEx, businessPartner.getBpNumberEx())
                    .eq(BusinessPartner::getCompanyCode, companyCode));
            if (businessPartnerList.size() > 0) {
                throw new RuntimeException("bpNumberEx already exists");
            }
            LambdaQueryWrapper<BusinessPartner> businessPartnerLambdaQueryWrapper = new LambdaQueryWrapper<BusinessPartner>()
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
            log.info(">>>wms新增bp,bpNumberEx为:{}", businessPartner.getBpNumberEx());
            bp.setBpNumberEx(businessPartner.getBpNumberEx());
            if (StringUtils.isNotBlank(businessPartner.getBpTel())) {
                bp.setBpTel(businessPartner.getBpTel());
            }
            if (StringUtils.isNotBlank(businessPartner.getBpEmail())) {
                bp.setBpEmail(businessPartner.getBpEmail());
            }
            if (StringUtils.isNotBlank(businessPartner.getBpContact())) {
                bp.setBpContact(businessPartner.getBpContact());
            }
            if (StringUtils.isNotBlank(businessPartner.getNotes())) {
                bp.setNotes(businessPartner.getNotes());
            }
            Date nowTime = new Date();
            bp.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
            bp.setGmtCreate(nowTime);
            bp.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
            bp.setGmtModified(nowTime);
            bp.setWmsIndicator(1);

//            // sync bk. 查下bk下是否有这个bp
//            JSONObject customerBp = bookKeepingService.bpCustomerList(businessPartner.getCompanyCode(), businessPartner.getBpName());
//            JSONObject vendorBp = bookKeepingService.bpVendorList(businessPartner.getCompanyCode(), businessPartner.getBpName());
//            if (customerBp != null) {
//                bp.setBkBpNumberCustomer(customerBp.getString("customerId"));
//            } else {
//                // 为空，要调用新增的接口
//                JSONObject otherParam = new JSONObject();
//                // 根据公司信息去查，暂时固定
//                List<ConditionTable> tables = remoteSvcService.conditionTableList(businessPartner.getCompanyCode(), "CU01").getData();
//                ConditionTable table = null;
//                if (tables == null || tables.isEmpty()) {
//                    table = new ConditionTable();
//                } else {
//                    table = tables.get(0);
//                }
//                log.info("customer:{}", JSONObject.toJSONString(table));
//                otherParam.put("customerAccount", table.getAccountName());
//
//                tables = remoteSvcService.conditionTableList(businessPartner.getCompanyCode(), "VD01").getData();
//                if (tables == null || tables.isEmpty()) {
//                    table = new ConditionTable();
//                } else {
//                    table = tables.get(0);
//                }
//                log.info("supplier:{}", JSONObject.toJSONString(table));
//                otherParam.put("supplierAccount", table.getAccountName());
//
//                log.info("other:{}", otherParam.toJSONString());
//                JSONObject obj = syncBpToBk(businessPartner, otherParam);
//                bp.setBkBpNumberCustomer(obj.getString("customerId"));
//                bp.setBkBpNumberVendor(obj.getString("supplierId"));
//            }
//            if (vendorBp != null) {
//                bp.setBkBpNumberVendor(vendorBp.getString("supplierId"));
//            }
            log.info("wms新增bp,参数封装:{}", bp);
            businessPartnerMapper.insert(bp);
            if (CollectionUtils.isNotEmpty(businessPartner.getContactList())) {
                bp.setContactList(businessPartner.getContactList());
            }
            if (CollectionUtils.isNotEmpty(businessPartner.getOfficeList())) {
                bp.setOfficeList(businessPartner.getOfficeList());
            }
            if (CollectionUtils.isNotEmpty(businessPartner.getBilltoList())) {
                bp.setBilltoList(businessPartner.getBilltoList());
            }
            if (CollectionUtils.isNotEmpty(businessPartner.getShiptoList())) {
                bp.setShiptoList(businessPartner.getShiptoList());
            }
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
    public int modifyBp(BusinessPartner businessPartner) {
        try {
            String companyCode = businessPartner.getCompanyCode();
            BusinessPartner businessPartnerOne = businessPartnerMapper.selectOne(new LambdaQueryWrapper<BusinessPartner>()
                    .eq(BusinessPartner::getBpNumberEx, businessPartner.getBpNumberEx())
                    .eq(BusinessPartner::getCompanyCode, companyCode));

            if (businessPartnerOne == null) {
                throw new RuntimeException("该bpNumberEx系统中不存在,不允许修改");
            }

            Date nowTime = new Date();
            LambdaUpdateWrapper<BusinessPartner> businessPartnerLambdaUpdateWrapper = new LambdaUpdateWrapper<BusinessPartner>();
            businessPartnerLambdaUpdateWrapper
                    .set(BusinessPartner::getBpName, businessPartner.getBpName())
                    .set(BusinessPartner::getModifiedBy, String.valueOf(UserInfoUtils.getSysUserId()))
                    .set(BusinessPartner::getGmtModified, nowTime)
                    .eq(BusinessPartner::getBpNumber, businessPartnerOne.getBpNumber())
                    .eq(BusinessPartner::getWmsIndicator, 1)
                    .eq(BusinessPartner::getId, businessPartnerOne.getId());

            businessPartnerLambdaUpdateWrapper.set(BusinessPartner::getBpNumberEx, businessPartner.getBpNumberEx());

            if (StringUtils.isNotBlank(businessPartner.getBpTel())) {
                businessPartnerLambdaUpdateWrapper.set(BusinessPartner::getBpTel, businessPartner.getBpTel());
            }

            //if (businessPartner.getIsBlock() != 0) {
            businessPartnerLambdaUpdateWrapper.set(BusinessPartner::getIsBlock, businessPartner.getIsBlock());
            //}

            if (StringUtils.isNotBlank(businessPartner.getBpEmail())) {
                businessPartnerLambdaUpdateWrapper.set(BusinessPartner::getBpEmail, businessPartner.getBpEmail());
            }
            if (StringUtils.isNotBlank(businessPartner.getBpContact())) {
                businessPartnerLambdaUpdateWrapper.set(BusinessPartner::getBpContact, businessPartner.getBpContact());
            }
//            if (StringUtils.isNotBlank(businessPartner.getNotes())) {
                businessPartnerLambdaUpdateWrapper.set(BusinessPartner::getNotes, businessPartner.getNotes());
//            }
            // modify Contact
            List<Long> newIds = businessPartner.getContactList().stream().map(Contact::getId)
                    .collect(Collectors.toList());
            newIds.removeIf(Objects::isNull);
            log.info(">>>>>wms修改bp,newIds:{}", newIds);
            List<Contact> contactList = contactMapper.selectList(new LambdaQueryWrapper<Contact>()
                    .eq(Contact::getCompanyCode, companyCode)
                    .eq(Contact::getBpNumber, businessPartnerOne.getBpNumber()));
            LambdaUpdateWrapper<Contact> contactLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            businessPartner.getContactList().forEach(contact -> {
                contactLambdaUpdateWrapper.clear();
                if (contact.getId() == null) {
                    saveContact(contact, companyCode, businessPartnerOne.getBpNumber(), nowTime);
                } else {
                    contactLambdaUpdateWrapper.eq(Contact::getId, contact.getId())
                            .set(Contact::getContactType, contact.getContactType())
                            .set(Contact::getContactPerson, contact.getContactPerson())
                            .set(Contact::getContactTel, contact.getContactTel())
                            .set(Contact::getContactEmail, contact.getContactEmail())
                            .set(Contact::getContactNote, contact.getContactNote())
                            .set(Contact::getGmtModified, nowTime)
                            .set(Contact::getModifiedBy, String.valueOf(UserInfoUtils.getSysUserId()))
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
                    .eq(Address::getReferenceKey, businessPartnerOne.getBpNumber()));

            if (CollectionUtils.isNotEmpty(businessPartner.getOfficeList()) && businessPartner.getOfficeList().size() > 1) {
                throw new RuntimeException("Office Address only one");
            }
            if (CollectionUtils.isNotEmpty(businessPartner.getBilltoList()) && businessPartner.getBilltoList().size() > 1) {
                throw new RuntimeException("Billing Address only one");
            }
            //modify address
            updateAddress(businessPartner.getOfficeList(), nowTime, companyCode, Util.SUB_TYPE_ADDRESS_BP_OFFICE, businessPartnerOne.getBpNumber());
            updateAddress(businessPartner.getBilltoList(), nowTime, companyCode, Util.SUB_TYPE_ADDRESS_BP_BILLTO, businessPartnerOne.getBpNumber());
            updateAddress(businessPartner.getShiptoList(), oldAddressList, companyCode, businessPartnerOne.getBpNumber(), nowTime);
            return businessPartnerMapper.update(null, businessPartnerLambdaUpdateWrapper);
        } catch (Exception e) {
            log.error("modify bp failed", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int createOrModifyBp(BusinessPartner businessPartner) {
        try {
            String companyCode = businessPartner.getCompanyCode();
            BusinessPartner businessPartnerOne = businessPartnerMapper.selectOne(new LambdaQueryWrapper<BusinessPartner>()
                    .eq(BusinessPartner::getBpNumberEx, businessPartner.getBpNumberEx())
                    .eq(BusinessPartner::getCompanyCode, companyCode));

            if (businessPartnerOne == null) {
                log.info("bp 不存在，调用create 方法");
                return createBp(businessPartner) == null ? 0 : 1;
            } else {
                return modifyBp(businessPartner);
            }
        } catch (Exception e) {
            log.error("modify bp failed", e);
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

    public List<Company> companyList() {
        try {
            List<Address> addressList = addressMapper.selectList(new LambdaQueryWrapper<Address>()
                    .eq(Address::getType, Util.TYPE_ADDRESS_COMPANY)
                    .eq(Address::getSubType, Util.SUB_TYPE_ADDRESS_COMPANY)
                    .eq(Address::getIsDeleted, "0"));
            List<Company> companyList = companyMapper.selectList(new LambdaQueryWrapper<Company>()
                    .eq(Company::getIsDeleted, "0"));
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

    public Company getCompanyByCode(String code) {
        try {
            Company company = companyMapper.selectOne(new LambdaQueryWrapper<Company>()
                    .eq(Company::getCompanyCode, code));
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
}
