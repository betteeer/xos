package com.inossem.oms.mdm.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.inossem.oms.api.file.api.FileService;
import com.inossem.oms.base.svc.domain.*;
import com.inossem.oms.base.svc.domain.VO.SkuListReqVO;
import com.inossem.oms.base.svc.domain.VO.SkuUomConversionVo;
import com.inossem.oms.base.svc.domain.VO.SkuVO;
import com.inossem.oms.base.svc.mapper.*;
import com.inossem.oms.base.svc.vo.ImportSKUVo;
import com.inossem.oms.base.utils.UserInfoUtils;
import com.inossem.oms.base.utils.poi.ExcelUtil;
import com.inossem.oms.mdm.common.Util;
import com.inossem.oms.svc.service.SvcFeignService;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.inossem.sco.common.core.utils.PageUtils.startPage;

/**
 * @author zoutong
 * @date 2022/10/15
 **/
@Service
@Slf4j
public class SkuService {

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
    private PictureTableMapper pictureTableMapper;

    @Value("${file.uri}")
    private String fileUri;

    @Resource
    FileService fileService;

    @Resource
    private SvcFeignService svcFeignService;

    @Transactional(rollbackFor = Exception.class)
    public synchronized SkuMaster create(SkuVO skuVO) {
        try {
            log.info(">>>sku新增,入参:{}", skuVO.toString());
            String companyCode = skuVO.getCompanyCode();
            String skuNumber = skuVO.getSkuNumber();
            log.info(">>>>sku 新增 获取到的前端传入SkuNumber为:{}", skuNumber);
            List<SkuMaster> skuMasterList;
            if (StringUtils.isEmpty(skuNumber)) {
                log.info(">>>sku新增 前端未入skuNumber,说明该sku为内部生成sku号");
                skuMasterList = skuMasterMapper.selectList(new LambdaQueryWrapper<SkuMaster>()
                        .eq(SkuMaster::getSkuName, skuVO.getSkuName())
                        .eq(SkuMaster::getCompanyCode, companyCode)
                        .eq(SkuMaster::getSkuSystemGenerated, 0));
            } else {
                log.info(">>>sku新增 前端传入skuNumber,说明该sku为用户自定义输入sku号");
                boolean digit = isDigit(skuNumber);
                if (digit) {
                    log.info(">>>如果用户输入的值为数字,String转换后的数字为:{}", Long.valueOf(skuNumber));
                    if (Long.valueOf(skuNumber) <= 1000000) {
                        throw new RuntimeException("Please fill in a value greater than 1 million");
                    }
                }
                skuMasterList = skuMasterMapper.selectList(new LambdaQueryWrapper<SkuMaster>()
                        .eq(SkuMaster::getSkuNumber, skuVO.getSkuNumber())
                        .or().eq(SkuMaster::getSkuName, skuVO.getSkuName())
                        .eq(SkuMaster::getCompanyCode, companyCode)
                        .eq(SkuMaster::getSkuSystemGenerated, 1));
            }

            if (skuMasterList.size() > 0) {
                throw new RuntimeException("sku number or sku name  already exists");
            }

            String oldSkuCode = "0";
            if (StringUtils.isEmpty(skuVO.getSkuNumber())) {
                QueryWrapper<SkuMaster> skuMasterLambdaQueryWrapper = new QueryWrapper<SkuMaster>()
                        .select("max(sku_number+0) as skuNumber")
                        .eq("company_code", skuVO.getCompanyCode())
                        .eq("sku_system_generated", 0);

                SkuMaster oldSkuMaster = skuMasterMapper.selectOne(skuMasterLambdaQueryWrapper);

                if (oldSkuMaster != null) {
                    oldSkuCode = oldSkuMaster.getSkuNumber();
                }
            }

            SkuMaster skuMaster = new SkuMaster();
            skuMaster.setCompanyCode(companyCode);
            if (StringUtils.isEmpty(skuVO.getSkuNumber())) {
                skuMaster.setSkuNumber(new BigDecimal(oldSkuCode).add(BigDecimal.ONE).intValue() + "");
            } else {
                skuMaster.setSkuNumber(skuVO.getSkuNumber());
                skuMaster.setSkuSystemGenerated(1);
            }
            if (StringUtils.isNotBlank(skuVO.getSkuName())) {
                skuMaster.setSkuName(skuVO.getSkuName());
            }
            if (StringUtils.isNotBlank(skuVO.getSkuNumberEx())) {
                skuMaster.setSkuNumberEx(skuVO.getSkuNumberEx());
            }
            skuMaster.setSkuDescription(skuVO.getSkuDescription());
            skuMaster.setBasicUom(skuVO.getBasicUom());
            Date nowTime = new Date();
            skuMaster.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
            skuMaster.setGmtCreate(nowTime);
            skuMaster.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
            skuMaster.setGmtModified(nowTime);
            skuMaster.setIsDeleted(0);
            skuMaster.setSkuGroupCode(skuVO.getSkuGroupCode());
            skuMaster.setSkuOwnerCode(skuVO.getSkuOwnerCode());
            skuMaster.setFsnIndicator(skuVO.getFsnIndicator());
            skuMaster.setStorageIndicator(skuVO.getStorageIndicator());
            skuMaster.setIsHuTraceable(skuVO.getIsHuTraceable());
            skuMaster.setPreserveDescription(skuVO.getPreserveDescription());
            skuMaster.setDisposeDescription(skuVO.getDisposeDescription());
            skuMaster.setSkuSatetyStock(skuVO.getSkuSatetyStock());
            if (CollectionUtils.isNotEmpty(skuVO.getPictureList())) {
                if (skuVO.getPictureList().size() > 8) {
                    throw new RuntimeException("No more than 8 pictures!");
                }
                saveSKUPicture(companyCode, skuMaster.getSkuNumber(), skuVO.getPictureList());
            }
            if (Util.TYPE_SERVICE_SKU.equals(skuVO.getSkuType())) {
                skuMaster.setSkuType(Util.TYPE_SERVICE_SKU);
            } else if (Util.TYPE_INVENTORY_SKU.equals(skuVO.getSkuType())) {
                skuMaster.setSkuType(Util.TYPE_INVENTORY_SKU);
                skuMaster.setUpcNumber(skuVO.getUpc());
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
    public void delSKUPicture(String companyCode, String skuCode) {
        pictureTableMapper.delete(new LambdaQueryWrapper<PictureTable>()
                .eq(PictureTable::getReferenceKey, skuCode)
                .eq(PictureTable::getCompanyCode, companyCode)
                .eq(PictureTable::getType, Util.TYPE_PICTURE_SKU));
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveSKUPicture(String companyCode, String skuCode, List<String> pictures) {
        try {
            pictureTableMapper.delete(new LambdaQueryWrapper<PictureTable>()
                    .eq(PictureTable::getReferenceKey, skuCode)
                    .eq(PictureTable::getCompanyCode, companyCode)
                    .eq(PictureTable::getType, Util.TYPE_PICTURE_SKU));
            Date nowTime = new Date();
            pictures.forEach(picture -> {
                PictureTable pictureTable = new PictureTable();
                pictureTable.setCompanyCode(companyCode);
                pictureTable.setType(Util.TYPE_PICTURE_SKU);
                pictureTable.setReferenceKey(skuCode);
                pictureTable.setUrlAddress(picture);
                pictureTable.setGmtCreate(nowTime);
                pictureTable.setGmtModified(nowTime);
                pictureTable.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                pictureTable.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
                pictureTableMapper.insert(pictureTable);
            });
        } catch (Exception e) {
            log.error("sku save pictures failed", e);
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
        log.info(">>>>sku 修改,入参:{}", skuVO.toString());
        try {
            String companyCode = skuVO.getCompanyCode();
            List<SkuMaster> skuMasterList = skuMasterMapper.selectList(new LambdaQueryWrapper<SkuMaster>()
                    .eq(SkuMaster::getSkuName, skuVO.getSkuName())
                    .eq(SkuMaster::getCompanyCode, companyCode)
                    .ne(SkuMaster::getId, skuVO.getId()));

            if (skuMasterList.size() > 0) {
                throw new RuntimeException("sku name already exists");
            }
            Date nowTime = new Date();
            SkuMaster skuMaster = skuMasterMapper.selectById(skuVO.getId());
            LambdaUpdateWrapper<SkuMaster> skuMasterLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            skuMasterLambdaUpdateWrapper
                    .set(SkuMaster::getSkuName, skuVO.getSkuName())
                    .set(SkuMaster::getBasicUom, skuVO.getBasicUom())
                    .set(SkuMaster::getSkuDescription, skuVO.getSkuDescription())
                    .set(SkuMaster::getGmtModified, nowTime)
                    //.set(SkuMaster::getModifiedBy, "test 01")
                    .set(SkuMaster::getModifiedBy, String.valueOf(UserInfoUtils.getSysUserId()))
                    .set(SkuMaster::getSkuSatetyStock, skuVO.getSkuSatetyStock())
                    .eq(SkuMaster::getId, skuVO.getId());
            if (StringUtils.isNotBlank(skuVO.getSkuNumberEx())) {
                skuMasterLambdaUpdateWrapper.set(SkuMaster::getSkuNumberEx, skuVO.getSkuNumberEx());
            }
            if (StringUtils.isNotBlank(skuVO.getSkuGroupCode())) {
                skuMasterLambdaUpdateWrapper.set(SkuMaster::getSkuGroupCode, skuVO.getSkuGroupCode());
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
            if (StringUtils.isNotBlank(skuVO.getPreserveDescription())) {
                skuMasterLambdaUpdateWrapper.set(SkuMaster::getPreserveDescription, skuVO.getPreserveDescription());
            }
            if (StringUtils.isNotBlank(skuVO.getDisposeDescription())) {
                skuMasterLambdaUpdateWrapper.set(SkuMaster::getDisposeDescription, skuVO.getDisposeDescription());
            }
            if (CollectionUtils.isNotEmpty(skuVO.getPictureList())) {
                if (skuVO.getPictureList().size() > 8) {
                    throw new RuntimeException("No more than 8 pictures!");
                }
                saveSKUPicture(companyCode, skuMaster.getSkuNumber(), skuVO.getPictureList());
            } else {
                delSKUPicture(companyCode, skuMaster.getSkuNumber());
            }
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
                        .set(SkuMaster::getPurchaseBasicRate, skuVO.getPurchaseBasicRate())
                        .eq(SkuMaster::getCompanyCode, companyCode);
                List<SkuCharacter> characters = skuVO.getCharacters();
//                if (CollectionUtils.isNotEmpty(skuVO.getCharacters())) {
                modifySkuCharacter(skuMaster.getSkuNumber(), characters, nowTime, companyCode);
//                }
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
    public void modifySkuCharacter(String skuCode, List<SkuCharacter> characters, Date nowTime, String companyCode) {
        try {
            List<SkuCharacter> oldSkuCharacters = skuCharacterMapper.selectList(new LambdaQueryWrapper<SkuCharacter>()
                    .eq(SkuCharacter::getSkuNumber, skuCode)
                    .eq(SkuCharacter::getCompanyCode, companyCode));
            LambdaUpdateWrapper<SkuCharacter> skuCharacterLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            if (CollectionUtils.isNotEmpty(characters)) {
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
            }
            List<Long> newIds = characters.stream().map(SkuCharacter::getId).collect(Collectors.toList());
            log.info(">>>>sku newIds:{}", newIds);
            newIds.removeIf(Objects::isNull);
            log.info(">>>>sku newIds.removeIf:{}", newIds);
            List<Long> delIds = new ArrayList<>(); //修改时删除的
            oldSkuCharacters.forEach(skuCharacter -> {
                long count = newIds.stream().filter(id -> Objects.equals(skuCharacter.getId(), id)).count();
                if (count < 1) {
                    delIds.add(skuCharacter.getId());
                }
            });
            log.info(">>>delIds:{}", delIds);
            delIds.forEach(id -> {
                skuCharacterLambdaUpdateWrapper.clear();
                skuCharacterLambdaUpdateWrapper.eq(SkuCharacter::getId, id)
                        .set(SkuCharacter::getIsDeleted, 1)
                        .set(SkuCharacter::getModifiedBy, String.valueOf(UserInfoUtils.getSysUserId()));
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

    public List<SkuMaster> getList(SkuListReqVO skuListReqVO) {
        Long startTime = System.currentTimeMillis();
        try {
            startPage();
            LambdaQueryWrapper<SkuMaster> skuMasterLambdaQueryWrapper = new LambdaQueryWrapper<>();
            skuMasterLambdaQueryWrapper.eq(SkuMaster::getCompanyCode, skuListReqVO.getCompanyCode());
            skuMasterLambdaQueryWrapper.eq(SkuMaster::getIsDeleted, 0);

            //如果是oms系统查询  则只查oms 不查外部
            if (StringUtils.isNotBlank(skuListReqVO.getWmsIndicator())) {
                log.info(">>>> oms查询sku,排除外部系统sku");
                skuMasterLambdaQueryWrapper.ne(SkuMaster::getWmsIndicator, 1);
                skuMasterLambdaQueryWrapper.or().isNull(SkuMaster::getWmsIndicator);
            }

            if (1 == skuListReqVO.getIsKitting()) {
                skuMasterLambdaQueryWrapper.eq(SkuMaster::getIsKitting, skuListReqVO.getIsKitting());
            }

            if (StringUtils.isNotBlank(skuListReqVO.getSkuType())) {
                skuMasterLambdaQueryWrapper.eq(SkuMaster::getSkuType, skuListReqVO.getSkuType());
            }
            if (StringUtils.isNotBlank(skuListReqVO.getSearchText())) {
                skuMasterLambdaQueryWrapper.and(skuMasterWrapper ->
                        skuMasterWrapper.like(SkuMaster::getSkuNumber, skuListReqVO.getSearchText())
                                .or()
                                .like(SkuMaster::getSkuName, skuListReqVO.getSearchText()));
            }
            if (StringUtils.isNotBlank(skuListReqVO.getSkuName())) {
                skuMasterLambdaQueryWrapper.like(SkuMaster::getSkuName, skuListReqVO.getSkuName());
            }
            if (StringUtils.isNotBlank(skuListReqVO.getSkuNumberEx())) {
                skuMasterLambdaQueryWrapper.eq(SkuMaster::getSkuNumberEx, skuListReqVO.getSkuNumberEx());
            }

            if (skuListReqVO.getSkus() != null && !skuListReqVO.getSkus().isEmpty()) {
                skuMasterLambdaQueryWrapper.and(sw -> {
                    List<SkuListReqVO.SkuListQueryVo> skus = skuListReqVO.getSkus();
                    for (SkuListReqVO.SkuListQueryVo sku : skus) {
                        sw.or(wa -> {
                            wa.eq(SkuMaster::getSkuNumberEx, sku.getSkuNumberEx())
                                    .and(x -> {
                                        x.eq(SkuMaster::getCompanyCode, sku.getCompanyCode());
                                    });
                        });
                    }
                });
            }

            skuMasterLambdaQueryWrapper.orderByDesc(SkuMaster::getGmtCreate);
            //执行分页查询
            //return skuMasterMapper.selectPage(page, skuMasterLambdaQueryWrapper);
            List<SkuMaster> res = skuMasterMapper.selectList(skuMasterLambdaQueryWrapper);

            // 查询skuUomConversion
            LambdaQueryWrapper<SkuUomConversion> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SkuUomConversion::getCompanyCode, skuListReqVO.getCompanyCode());
            List<SkuUomConversion> skuUomConversionList = skuUomConversionMapper.selectList(wrapper);
            log.info("查到的uom list数量为：" + skuUomConversionList.size());


            // SkuCharacter
            LambdaQueryWrapper<SkuCharacter> skuCharacterLambdaQueryWrapper = new LambdaQueryWrapper<>();
            skuCharacterLambdaQueryWrapper.eq(SkuCharacter::getCompanyCode, skuListReqVO.getCompanyCode());
            skuCharacterLambdaQueryWrapper.eq(SkuCharacter::getIsDeleted, 0);
            List<SkuCharacter> skuCharacterList = skuCharacterMapper.selectList(skuCharacterLambdaQueryWrapper);
            log.info("查到的SkuCharacter list数量为：" + skuCharacterList.size());


            res.forEach(r -> {

                // 如果是kitting再查询组合产品相关信息
                r.setKittingItems(Collections.EMPTY_LIST);
                if (r.getIsKitting() == 1) {
                    List<SkuKitting> skuKittingList;
                    LambdaQueryWrapper<SkuKitting> lambdaQueryWrapper = new LambdaQueryWrapper<SkuKitting>()
                            .eq(SkuKitting::getKittingSku, r.getSkuNumber())
                            .eq(SkuKitting::getCompanyCode, skuListReqVO.getCompanyCode())
                            .eq(SkuKitting::getIsDeleted, 0);

                    skuKittingList = skuKittingMapper.selectList(lambdaQueryWrapper);
                    int maxVersion = 1;
                    OptionalInt maxOp = skuKittingList.stream().mapToInt(SkuKitting::getVersion).max();
                    if (maxOp.isPresent()) {
                        maxVersion = maxOp.getAsInt();
                    }
                    int finalMaxVersion = maxVersion;
                    skuKittingList = skuKittingList.stream().filter(skuKitting -> finalMaxVersion == skuKitting.getVersion()).collect(Collectors.toList());
                    r.setKittingItems(skuKittingList);
                }

                // 查询skuUomConversion
                List<SkuUomConversion> conversions = new ArrayList<>();
                for (SkuUomConversion uomConversion : skuUomConversionList) {
                    if (uomConversion.getSkuNumber().equals(r.getSkuNumber())) {
                        conversions.add(uomConversion);
                    }
                }
                r.setSkuUomConversionList(conversions);


                // SkuUomConversion
                List<SkuCharacter> skuCharacterList1 = new ArrayList<>();
                for (SkuCharacter skuCharacter : skuCharacterList) {
                    if (skuCharacter.getSkuNumber().equals(r.getSkuNumber())) {
                        skuCharacterList1.add(skuCharacter);
                    }
                }
                r.setCharacters(skuCharacterList1);

//                LambdaQueryWrapper<SkuUomConversion> wrapper = new LambdaQueryWrapper<>();
//                wrapper.eq(SkuUomConversion::getSkuNumber, r.getSkuNumber());
//                List<SkuUomConversion> skuUomConversionList = skuUomConversionMapper.selectList(wrapper);
            });

            log.info("查询耗时：" + (System.currentTimeMillis() - startTime) + " ms");
            return res;
        } catch (Exception e) {
            log.error("get sku list failed", e);
            throw new RuntimeException(e);
        }
    }

    public List<SkuMaster> getFeignList(SkuListReqVO skuListReqVO) {
        try {
            LambdaQueryWrapper<SkuMaster> skuMasterLambdaQueryWrapper = new LambdaQueryWrapper<>();
            skuMasterLambdaQueryWrapper.eq(SkuMaster::getSkuName, skuListReqVO.getSkuName());
            skuMasterLambdaQueryWrapper.eq(SkuMaster::getCompanyCode, skuListReqVO.getCompanyCode());
            skuMasterLambdaQueryWrapper.eq(SkuMaster::getIsDeleted, 0);

            List<SkuMaster> res = skuMasterMapper.selectList(skuMasterLambdaQueryWrapper);
            return res;
        } catch (Exception e) {
            log.error("get sku list failed", e);
            throw new RuntimeException(e);
        }
    }

    public SkuMaster getSku(String skuCode, String version, String companyCode) {
        try {
            SkuMaster skuMaster = skuMasterMapper.selectOne(new LambdaQueryWrapper<SkuMaster>()
                    .eq(SkuMaster::getSkuNumber, skuCode)
                    .eq(SkuMaster::getCompanyCode, companyCode)
                    .eq(SkuMaster::getIsDeleted, 0));
            // ###todo###
            if (skuMaster == null) {
                return null;
            }
            skuMaster.setPictureList(pictureTableMapper.selectList(new LambdaQueryWrapper<PictureTable>()
                    .eq(PictureTable::getCompanyCode, skuMaster.getCompanyCode())
                    .eq(PictureTable::getReferenceKey, skuCode)
                    .eq(PictureTable::getType, Util.TYPE_PICTURE_SKU)
                    .eq(PictureTable::getIsDeleted, 0)));

            if (Util.TYPE_INVENTORY_SKU.equals(skuMaster.getSkuType())) {
                if (1 == skuMaster.getIsKitting()) {
                    List<SkuKitting> skuKittingList;
                    LambdaQueryWrapper<SkuKitting> lambdaQueryWrapper = new LambdaQueryWrapper<SkuKitting>()
                            .eq(SkuKitting::getKittingSku, skuCode)
                            .eq(SkuKitting::getCompanyCode, companyCode)
                            .eq(SkuKitting::getIsDeleted, 0);
                    if (StringUtils.isNotBlank(version)) {
                        lambdaQueryWrapper.eq(SkuKitting::getVersion, Integer.parseInt(version));
                        skuKittingList = skuKittingMapper.selectList(lambdaQueryWrapper);
                    } else {
                        skuKittingList = skuKittingMapper.selectList(lambdaQueryWrapper);
                        int maxVersion = 1;
                        OptionalInt maxOp = skuKittingList.stream().mapToInt(SkuKitting::getVersion).max();
                        if (maxOp.isPresent()) {
                            maxVersion = maxOp.getAsInt();
                        }
                        int finalMaxVersion = maxVersion;
                        skuKittingList = skuKittingList.stream().filter(skuKitting -> finalMaxVersion == skuKitting.getVersion()).collect(Collectors.toList());
                    }

                    LambdaQueryWrapper<SkuMaster> queryWrapper = new LambdaQueryWrapper<SkuMaster>()
                            .eq(SkuMaster::getCompanyCode, companyCode)
                            .eq(SkuMaster::getIsDeleted, 0);
                    List<SkuMaster> skuMasterList = skuMasterMapper.selectList(queryWrapper);
                    skuKittingList.forEach(skuKitting -> {
                        Optional<SkuMaster> skuMasterOptional = skuMasterList.stream().filter(sku -> sku.getSkuNumber().equals(skuKitting.getComponentSku())).findFirst();
                        skuMasterOptional.ifPresent(master -> {
                            skuKitting.setSkuDescription(master.getSkuDescription());
                            skuKitting.setSkuName(master.getSkuName());
                            skuKitting.setBasicUom(master.getBasicUom());
                        });
                    });
                    skuMaster.setKittingItems(skuKittingList);
                }
                skuMaster.setCharacters(skuCharacterMapper.selectList(new LambdaQueryWrapper<SkuCharacter>()
                        .eq(SkuCharacter::getSkuNumber, skuCode)
                        .eq(SkuCharacter::getCompanyCode, companyCode)
                        .eq(SkuCharacter::getIsDeleted, 0)));
                skuMaster.setBPDetails(skuBpMapper.selectList(new LambdaQueryWrapper<SkuBp>()
                        .eq(SkuBp::getSkuNumber, skuCode)
                        .eq(SkuBp::getCompanyCode, companyCode)
                        .eq(SkuBp::getIsDeleted, 0)));
                skuMaster.setFTradeDetails(skuTariffMapper.selectList(new LambdaQueryWrapper<SkuTariff>()
                        .eq(SkuTariff::getSkuNumber, skuCode)
                        .eq(SkuTariff::getCompanyCode, companyCode)
                        .eq(SkuTariff::getIsDeleted, 0)));
            }
            //查询skuUomComversion
            LambdaQueryWrapper<SkuUomConversion> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SkuUomConversion::getSkuNumber, skuMaster.getSkuNumber());
            wrapper.eq(SkuUomConversion::getCompanyCode, skuMaster.getCompanyCode());
            List<SkuUomConversion> skuUomConversionList = skuUomConversionMapper.selectList(wrapper);
            skuMaster.setSkuUomConversionList(skuUomConversionList);
            //查询当前sku在so  po 中是否有使用  , 是否已经进库存
            log.info(">>>>sku查询详细信息,查询sku是否可以修改");
//            R<Boolean> checkData = remoteSvcService.checkSku(skuCode, companyCode);
            Boolean checkData = svcFeignService.checkSku(skuCode,companyCode);
            log.info(">>>>查询结果,checkData:{}",checkData);
            if (checkData == false) {
                skuMaster.setIsUpdate(1);
            }
            return skuMaster;
        } catch (Exception e) {
            log.error("get one sku failed", e);
            throw new RuntimeException(e);
        }
    }

    public List<String> galleryUpload(String skuCode, MultipartFile[] pictures, HttpServletRequest req) {

        if (pictures.length > 8) {
            throw new RuntimeException("The number of pictures cannot be more than eight");
        }
        long count = Arrays.stream(pictures)
                .map(MultipartFile::getOriginalFilename)
                .filter(Objects::nonNull)
                .filter(String::isEmpty).count();
        if (count == pictures.length) {
            throw new RuntimeException("No pictures uploaded");
        }
        return savePictures(skuCode, pictures, req);
    }

    private List<String> savePictures(String skuCode, MultipartFile[] pictures, HttpServletRequest req) {
        try {
            String realPath = Util.getPicturesPath() + skuCode;
            log.info("realPath" + realPath);
            File file = new File(realPath);

            if (file.exists()) {
                FileUtils.cleanDirectory(file);
                FileUtils.forceDelete(file);
            }
            FileUtils.forceMkdir(file);

            List<String> filePaths = new ArrayList<>();
            for (MultipartFile multipartFile : pictures) {

                multipartFile.transferTo(new File(file, Objects.requireNonNull(multipartFile.getOriginalFilename())));
                String filePath = req.getScheme() + "://" + req.getServerName()
                        + ":" + req.getServerPort() + fileUri + skuCode + "/"
                        + multipartFile.getOriginalFilename();
                filePaths.add(filePath);
            }
            return filePaths;
        } catch (Exception e) {
            log.error("gallery upload save pictures failed", e);
            throw new RuntimeException(e);
        }
    }

    public List<String> galleryList(String skuCode, HttpServletRequest req) {
        try {
            String realPath = Util.getPicturesPath() + skuCode;
            log.info("realPath" + realPath);
            List<String> result = new ArrayList<>();
            File file = new File(realPath);
            if (!file.exists()) {
                return result;
            }
            Arrays.stream(Objects.requireNonNull(file.listFiles())).forEach(f -> {
                String filePath = req.getScheme() + "://" + req.getServerName()
                        + ":" + req.getServerPort() + fileUri + skuCode + "/"
                        + f.getName();
                result.add(filePath);
            });
            return result;
        } catch (Exception e) {
            log.error("get gallery list failed", e);
            throw new RuntimeException(e);
        }
    }

    public List<Map<String, Object>> upload(MultipartFile[] files) {
        log.info(">>>>sku 图片上传接收到的参数为:{}", files);
        List<Map<String, Object>> result = new ArrayList<>();
        if (files.length > 8) {
            throw new RuntimeException("The number of pictures cannot be more than 8");
        }
        // todo file
        for (MultipartFile file : files) {
            log.info(">>>>sku 图片上传开始:file", file);
            Map<String, Object> data = new HashMap<>();

            JSONObject rt = null;
            try {
                rt = fileService.upload("3002", file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            log.info(">>>>sku 图片上传结束结果:rt", rt);
            if (null == rt) {
                data.put("status", 0);
                data.put("err", "图片服务器返回null");
                data.put("url", "");
                continue;
            }
            //检查结果
            if (null != rt.getString("url")) {
                data.put("status", 1);
                data.put("err", "");
                data.put("url", rt.getString("url"));
            } else {
                data.put("status", 0);
                data.put("err", "上传图片失败");
                data.put("url", "");
            }
            result.add(data);
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public void importSKU(MultipartFile file, String companyCode) {
        try {
            List<SkuMaster> skuMasterList = skuMasterMapper.selectList(new LambdaQueryWrapper<SkuMaster>()
                    .eq(SkuMaster::getCompanyCode, companyCode));
            ExcelUtil<ImportSKUVo> util = new ExcelUtil<>(ImportSKUVo.class);
            List<ImportSKUVo> importSKUVoList = util.importExcel(file.getInputStream(), 1);
            List<SkuMaster> saveSkuMasterList = new ArrayList<>();
            for (int i = 0; i < importSKUVoList.size(); i++) {
                ImportSKUVo skuVo = importSKUVoList.get(i);
                if (StringUtils.isBlank(skuVo.getSkuNumber())) {
                    throw new RuntimeException("Line " + (i + 2) + " sku_number is a required field");
                }
                if (StringUtils.isBlank(skuVo.getUpcNumber())) {
                    throw new RuntimeException("Line " + (i + 2) + " upc_number is a required field");
                }
                if (StringUtils.isBlank(skuVo.getSkuName())) {
                    throw new RuntimeException("Line " + (i + 2) + " sku_name is a required field");
                }
                if (StringUtils.isBlank(skuVo.getSkuType())) {
                    throw new RuntimeException("Line " + (i + 2) + " sku_type is a required field");
                }
                if (StringUtils.isBlank(skuVo.getBasicUom())) {
                    throw new RuntimeException("Line " + (i + 2) + " basic_uom is a required field");
                }
                if (StringUtils.isBlank(skuVo.getIsKitting())) {
                    throw new RuntimeException("Line " + (i + 2) + " is_kitting is a required field");
                }
                Optional<SkuMaster> skuMasterOptional = skuMasterList.stream().filter(skuMaster ->
                        skuVo.getSkuNumber().equals(skuMaster.getSkuNumber())).findAny();
                Optional<SkuMaster> skuMasterOptional2 = skuMasterList.stream().filter(skuMaster ->
                        skuVo.getSkuName().equals(skuMaster.getSkuName())).findAny();
                if (skuMasterOptional2.isPresent()) {
                    throw new RuntimeException("Line " + (i + 2) + " sku name: [" + skuVo.getSkuName() + "] Already exists");
                }
                if (!skuMasterOptional.isPresent()) {
                    SkuMaster skuMaster = new SkuMaster();
                    skuMaster.setCompanyCode(companyCode);
                    skuMaster.setSkuNumber(skuVo.getSkuNumber());
                    skuMaster.setUpcNumber(skuVo.getUpcNumber());
                    skuMaster.setSkuName(skuVo.getSkuName());
                    skuMaster.setSkuType(skuVo.getSkuType());
                    skuMaster.setBasicUom(skuVo.getBasicUom());
                    skuMaster.setIsKitting("Y".equals(skuVo.getIsKitting()) ? 1 : 0);
                    skuMaster.setSkuDescription(skuVo.getSkuDescription());
                    if (null != skuVo.getWidth()) {
                        skuMaster.setWidth(skuVo.getWidth());
                    }
                    if (null != skuVo.getHeight()) {
                        skuMaster.setHeight(skuVo.getHeight());
                    }
                    if (null != skuVo.getLength()) {
                        skuMaster.setLength(skuVo.getLength());
                    }
                    skuMaster.setWhlUom(skuVo.getWhlUom());
                    if (null != skuVo.getNetWeight()) {
                        skuMaster.setNetWeight(skuVo.getNetWeight());
                    }
                    if (null != skuVo.getWidth() && null != skuVo.getHeight() && null != skuVo.getLength()) {
                        BigDecimal grossWeight = skuVo.getWidth().multiply(skuVo.getHeight()).multiply(skuVo.getLength());
                        skuMaster.setGrossVolume(grossWeight);
                    }
                    skuMaster.setWeightUom(skuVo.getWeightUom());
                    Date nowTime = new Date();
                    skuMaster.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                    skuMaster.setGmtCreate(nowTime);
                    skuMaster.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
                    skuMaster.setGmtModified(nowTime);
                    skuMaster.setIsDeleted(0);
                    saveSkuMasterList.add(skuMaster);
                }
            }
            saveSkuMasterList.forEach(skuMaster -> skuMasterMapper.insert(skuMaster));
        } catch (Exception e) {
            log.error("import sku failed");
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用类型转换判断是否为数字
     */
    private static boolean isDigit(String str) {
        try {
            Long aLong = Long.valueOf(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
