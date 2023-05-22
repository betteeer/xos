package com.inossem.oms.mdm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inossem.oms.base.svc.domain.Carrier;
import com.inossem.oms.base.svc.domain.PictureTable;
import com.inossem.oms.base.svc.mapper.CarrierMapper;
import com.inossem.oms.base.svc.mapper.PictureTableMapper;
import com.inossem.oms.base.utils.UserInfoUtils;
import com.inossem.oms.mdm.common.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zoutong
 * @date 2022/10/17
 **/
@Service
@Slf4j
public class CarrierService {

    @Resource
    private CarrierMapper carrierMapper;

    @Resource
    private PictureTableMapper pictureTableMapper;

    @Transactional(rollbackFor = Exception.class)
    public Carrier create(Carrier carrier) {
        try {
            LambdaQueryWrapper<Carrier> carrierLambdaQueryWrapper = new LambdaQueryWrapper<Carrier>()
                    .eq(Carrier::getCompanyCode,carrier.getCompanyCode())
                    .eq(Carrier::getCarrierCode,carrier.getCarrierCode());
            Carrier oldCarrier = carrierMapper.selectOne(carrierLambdaQueryWrapper);
            if (oldCarrier != null){
                throw new RuntimeException("The company already has the same carrier code!");
            }
            /*String oldCarrierCode = "0";
            if (oldCarrier != null) {
                oldCarrierCode = oldCarrier.getCarrierCode();
            }*/
            //carrier.setCarrierCode(new BigDecimal(oldCarrierCode).add(BigDecimal.ONE).toPlainString());
            carrier.setCarrierCode(carrier.getCarrierCode());
            carrierMapper.insert(carrier);
            saveCarrierPicture(carrier.getCompanyCode(),carrier.getCarrierCode(),carrier.getPictureList());
            return carrier;
        } catch (Exception e) {
            log.error("create carrier failed", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveCarrierPicture(String companyCode,String carrierCode,List<String> pictures){
        try {
            pictureTableMapper.delete(new LambdaQueryWrapper<PictureTable>()
                    .eq(PictureTable::getCompanyCode,companyCode)
                    .eq(PictureTable::getReferenceKey,carrierCode)
                    .eq(PictureTable::getType,Util.TYPE_PICTURE_CARRIER));
            Date nowTime = new Date();
            pictures.forEach(picture ->{
                PictureTable pictureTable = new PictureTable();
                pictureTable.setCompanyCode(companyCode);
                pictureTable.setType(Util.TYPE_PICTURE_CARRIER);
                pictureTable.setReferenceKey(carrierCode);
                pictureTable.setUrlAddress(picture);
                pictureTable.setGmtCreate(nowTime);
                pictureTable.setGmtModified(nowTime);
                pictureTable.setCreateBy(String.valueOf(UserInfoUtils.getSysUserId()));
                pictureTable.setModifiedBy(String.valueOf(UserInfoUtils.getSysUserId()));
                pictureTableMapper.insert(pictureTable);
            });
        } catch (Exception e) {
            log.error("carrier save pictures failed",e);
            throw new RuntimeException(e);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public Carrier modify(Carrier carrier) {
        try {
            if (null == carrier.getId()){
                throw new RuntimeException("carrier id is not null");
            }
            LambdaQueryWrapper<Carrier> carrierLambdaQueryWrapper = new LambdaQueryWrapper<Carrier>()
                    .eq(Carrier::getCompanyCode,carrier.getCompanyCode())
                    .eq(Carrier::getCarrierCode,carrier.getCarrierCode());
            Carrier oldCarrier = carrierMapper.selectOne(carrierLambdaQueryWrapper);
            if (null != oldCarrier){
                throw new RuntimeException("The company already has the same carrier code!");
            }
            Carrier carrierOld = carrierMapper.selectById(carrier.getId());
            carrier.setCarrierCode(carrierOld.getCarrierCode());
            saveCarrierPicture(carrier.getCompanyCode(),carrier.getCarrierCode(),carrier.getPictureList());
            carrierMapper.updateById(carrier);
            return carrier;
        } catch (Exception e) {
            log.error("modify carrier failed", e);
            throw new RuntimeException(e);
        }
    }

    public List<Carrier> list(String companyCode){
        try {
            List<PictureTable> pictureTables = pictureTableMapper.selectList(
                    new LambdaQueryWrapper<PictureTable>()
                            .eq(PictureTable::getCompanyCode,companyCode)
                            .eq(PictureTable::getType,Util.TYPE_PICTURE_CARRIER));
            List<Carrier> carriers = carrierMapper.selectList(new LambdaQueryWrapper<Carrier>()
                    .eq(Carrier::getCompanyCode,companyCode));
            carriers.forEach(carrier -> {
                List<String> pictures = new ArrayList<>();
                List<PictureTable> pictureTableList = pictureTables.stream().filter(picture->picture.getReferenceKey().equals(carrier.getCarrierCode())).collect(Collectors.toList());
                pictureTableList.forEach(picture -> pictures.add(picture.getUrlAddress()));
                carrier.setPictureList(pictures);
            });
            return carriers;
        } catch (Exception e) {
            log.error("get carrier list failed",e);
            throw new RuntimeException(e);
        }
    }

    public Carrier getCarrier(Long id){
        try {
            Carrier carrier = carrierMapper.selectById(id);
            List<String> pictures = new ArrayList<>();
            List<PictureTable> pictureTables = pictureTableMapper.selectList(
                    new LambdaQueryWrapper<PictureTable>()
                            .eq(PictureTable::getCompanyCode,carrier.getCompanyCode())
                            .eq(PictureTable::getReferenceKey,carrier.getCarrierCode())
                            .eq(PictureTable::getType,Util.TYPE_PICTURE_CARRIER));
            pictureTables.forEach(picture -> pictures.add(picture.getUrlAddress()));
            carrier.setPictureList(pictures);
            return carrier;
        } catch (Exception e) {
            log.error("get one carrier failed",e);
            throw new RuntimeException(e);
        }
    }

    public Object delete(Long id) {
        return carrierMapper.deleteById(id);
    }
}
