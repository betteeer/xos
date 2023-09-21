package com.inossem.oms.svc.service;

import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.inossem.oms.base.svc.domain.*;
import com.inossem.oms.base.svc.domain.DTO.DeliveryHeaderFormDTO;
import com.inossem.oms.base.svc.domain.DTO.DeliveryItemFormDTO;
import com.inossem.oms.base.svc.mapper.DeliveryHeaderMapper;
import com.inossem.oms.base.svc.mapper.DeliveryItemMapper;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DeliveryHeaderService {

    @Resource
    private DeliveryHeaderMapper deliveryHeaderMapper;

    @Resource
    private DeliveryItemMapper deliveryItemMapper;

    public List<DeliveryHeader> getList(DeliveryHeaderFormDTO form) {
        log.info(">>>查询列表，入参：[{}]", form);
        MPJLambdaWrapper<DeliveryHeader> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(DeliveryHeader.class);
        // 指定 company code数据范围
        wrapper.eq(DeliveryHeader::getCompanyCode, form.getCompanyCode());
        // 保留有效的数据
        wrapper.eq(DeliveryHeader::getIsDeleted, 0);
        // 查询 status
        wrapper.in(StringUtils.isNotEmpty(form.getStatus()), DeliveryHeader::getCompleteDelivery, form.getStatus());
        // 查询 warehouse
        wrapper.in(StringUtils.isNotEmpty(form.getWarehouseCode()), DeliveryHeader::getWarehouseCode, form.getWarehouseCode());
        // 查询delivery date
        wrapper.between(StringUtils.isNotNull(form.getDeliveryDateStart()), DeliveryHeader::getDeliveryDate, form.getDeliveryDateStart(), form.getDeliveryDateEnd());
        // 查询posting date
        wrapper.between(StringUtils.isNotNull(form.getPostDateStart()), DeliveryHeader::getPostingDate, form.getPostDateStart(), form.getPostDateEnd());
        // 查询carrier
        wrapper.in(StringUtils.isNotEmpty(form.getCarrierCode()), DeliveryHeader::getCarrierCode, form.getCarrierCode());
        wrapper.nested(StringUtils.isNotEmpty(form.getSearchText()), i -> {
            i.like(DeliveryHeader::getDeliveryNumber, form.getSearchText()).or().like(DeliveryHeader::getTrackingNumber, form.getSearchText());
        });
        wrapper.leftJoin(DeliveryItem.class, DeliveryItem::getDeliveryNumber, DeliveryHeader::getDeliveryNumber, ext -> {
            ext.leftJoin(PoHeader.class, PoHeader::getPoNumber, DeliveryItem::getReferenceDoc)
                    .selectAs(PoHeader::getBpName, DeliveryHeader::getBpName)
                    .selectAs(PoHeader::getOrderType, DeliveryHeader::getOrderType)
                    .selectAs(PoHeader::getPoNumber, DeliveryHeader::getPoNumber);
            ext.nested(StringUtils.isNotEmpty(form.getOrderType()), i -> i.in(PoHeader::getOrderType, form.getOrderType()));
            return ext;
        });
        // 拼接order by
        wrapper.orderBy(StringUtils.isNotNull(form.getOrderBy()), form.getIsAsc(), StringUtils.toUnderScoreCase(form.getOrderBy()));
        // 排序的字段值相同则按照id倒序
        wrapper.orderBy(true, false, DeliveryHeader::getId);
        return deliveryHeaderMapper.selectJoinList(DeliveryHeader.class, wrapper);

    }

    public List<DeliveryItem> getItem(DeliveryItemFormDTO form) {
        log.info(">>>查询列表，入参：[{}]", form);
        MPJLambdaWrapper<DeliveryItem> wrapper = new MPJLambdaWrapper<>();
        if (form.getSkuType() == null) {
            form.setSkuType(new ArrayList<>());
        }
        wrapper.selectAll(DeliveryItem.class);
        // 指定 company code数据范围
        wrapper.eq(DeliveryItem::getCompanyCode, form.getCompanyCode());
        // 保留有效的数据
        wrapper.eq(DeliveryItem::getIsDeleted, 0);
        // 查询 status
        wrapper.in(StringUtils.isNotEmpty(form.getStatus()), DeliveryItem::getCompleteDelivery, form.getStatus());
        // 查询 warehouse
        wrapper.leftJoin(SkuMaster.class, SkuMaster::getSkuNumber, DeliveryItem::getSkuNumber, ext -> {
            ext.nested(StringUtils.isNotEmpty(form.getSearchText()),
                    i -> i.like(SkuMaster::getSkuName, form.getSearchText())
                            .or().like(SkuMaster::getSkuNumber, form.getSearchText())
                            .or().like(DeliveryItem::getDeliveryNumber, form.getSearchText()));
            if ((form.getSkuType().size() == 2 || form.getSkuType().size() == 0)) {
                // warehouse不是空的
                // 那么需要筛选 itemType=INVENTORY，WAREHOUSE满足条件的 + 所有的itemType=SERVICE的
                if (StringUtils.isNotEmpty(form.getWarehouseCode())) {
                    ext.nested(i -> {
                        i.eq(SkuMaster::getSkuType, "IN").in(DeliveryItem::getWarehouseCode, form.getWarehouseCode())
                                .or().eq(SkuMaster::getSkuType, "SE");
                    });
                }
                //如果warehouse是空的
                // 那么需要筛选 所有
            } else {
//            skuType只有一种, 先只筛这一种，然后如果是IN的，还需要再筛选warehouse
                ext.in(SkuMaster::getSkuType, form.getSkuType());
                if (form.getSkuType().contains("IN")) {
                    ext.in(StringUtils.isNotEmpty(form.getWarehouseCode()), DeliveryItem::getWarehouseCode, form.getWarehouseCode());
                }
            }
            return ext.selectAs(SkuMaster::getSkuName, DeliveryItem::getSkuName).selectAs(SkuMaster::getSkuType, DeliveryItem::getSkuType);
        });
        wrapper.leftJoin(SkuMaster.class, SkuMaster::getSkuNumber, DeliveryItem::getKittingSku, ext -> ext.selectAs(SkuMaster::getSkuName, DeliveryItem::getKittingSkuName));
        wrapper.leftJoin(DeliveryHeader.class, DeliveryHeader::getDeliveryNumber, DeliveryItem::getDeliveryNumber, ext -> {
            ext.nested(StringUtils.isNotNull(form.getPostingDateStart()), i -> i.between(DeliveryHeader::getPostingDate, form.getPostingDateStart(), form.getPostingDateEnd()));
            return ext.selectAs(DeliveryHeader::getPostingDate, DeliveryItem::getPostingDate);
        });

        // 拼接order by
        wrapper.orderBy(StringUtils.isNotNull(form.getOrderBy()), form.getIsAsc(), StringUtils.toUnderScoreCase(form.getOrderBy()));
        // 排序的字段值相同则按照id倒序
        wrapper.orderBy(true, false, DeliveryHeader::getId);
        return deliveryItemMapper.selectJoinList(DeliveryItem.class, wrapper);
    }
}
