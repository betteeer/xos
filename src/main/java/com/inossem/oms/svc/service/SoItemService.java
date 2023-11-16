package com.inossem.oms.svc.service;

import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.inossem.oms.base.svc.domain.*;
import com.inossem.oms.base.svc.domain.DTO.SoItemSearchFormDTO;
import com.inossem.oms.base.svc.mapper.SoItemMapper;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SoItemService {
    @Resource
    private SoItemMapper soItemMapper;

    public List<?> getList(SoItemSearchFormDTO form) {
        log.info(">>>查询列表，入参：[{}]", form);
        MPJLambdaWrapper<SoItem> wrapper = new MPJLambdaWrapper<>();
        if (form.getItemType() == null) {
            form.setItemType(new ArrayList<>());
        }
        wrapper.selectAll(SoItem.class);
        // 指定 company code数据范围
        wrapper.eq(SoItem::getCompanyCode, form.getCompanyCode());
        // 保留有效的数据
        wrapper.eq(SoItem::getIsDeleted, 0);
//        // 拼接 itemType
//        wrapper.in(StringUtils.isNotEmpty(form.getItemType()), PoItem::getItemType, form.getItemType());
        // itemType全选或者全不选
        if (form.getItemType().size() == 2 || form.getItemType().isEmpty()) {
            // warehouse不是空的
            // 那么需要筛选 itemType=INVENTORY，WAREHOUSE满足条件的 + 所有的itemType=SERVICE的
            if (StringUtils.isNotEmpty(form.getWarehouseCode())) {
                wrapper.nested(i -> {
                    i.eq(SoItem::getSkuType, "IN").in(SoItem::getWarehouseCode, form.getWarehouseCode())
                            .or().eq(SoItem::getSkuType, "SE");
                });
            }
            //如果warehouse是空的
            // 那么需要筛选 所有
        } else {
//            itemType只有一种, 先只筛这一种，然后如果是IN的，还需要再筛选warehouse
            wrapper.in(SoItem::getSkuType, form.getItemType());
            if (form.getItemType().contains("IN")) {
                wrapper.in(StringUtils.isNotEmpty(form.getWarehouseCode()), SoItem::getWarehouseCode, form.getWarehouseCode());
            }
        }
        // 拼接unit price
        wrapper.between(StringUtils.isNotNull(form.getUnitPriceStart()), SoItem::getUnitPrice, form.getUnitPriceStart(), form.getUnitPriceEnd());
        // 拼接purchase qty
        wrapper.between(StringUtils.isNotNull(form.getSalesQtyStart()), SoItem::getSalesQty, form.getSalesQtyStart(), form.getSalesQtyEnd());
        // 拼接currency code
        wrapper.in(StringUtils.isNotEmpty(form.getCurrencyCode()), SoItem::getCurrencyCode, form.getCurrencyCode());
        // 拼接 tax exmpt
        wrapper.in(StringUtils.isNotEmpty(form.getTaxExmpt()), SoItem::getTaxExmpt, form.getTaxExmpt());
        // join skuMaster，查询skuName，并根据searchText进行过滤
        wrapper.leftJoin(SkuMaster.class, SkuMaster::getSkuNumber, SoItem::getSkuNumber, ext -> {
            ext.selectAs(SkuMaster::getSkuName, SoItem::getSkuName);
            ext.nested(StringUtils.isNotEmpty(form.getSearchText()), i ->
                    i.like(SkuMaster::getSkuNumber, form.getSearchText())
                            .or().like(SkuMaster::getSkuName, form.getSearchText())
                            .or().like(SoItem::getSoNumber, form.getSearchText()));
            return ext;
        });
        // join PoHeader 查询orderStatus
        wrapper.leftJoin(SoHeader.class, SoHeader::getSoNumber, SoItem::getSoNumber,
                ext -> ext.selectAs(SoHeader::getOrderStatus,SoItem::getOrderStatus));
        // 拼接order by
        wrapper.orderBy(StringUtils.isNotNull(form.getOrderBy()), form.getIsAsc(), StringUtils.toUnderScoreCase(form.getOrderBy()));
        // 排序的字段值相同则按照id倒序
        wrapper.orderBy(true, false, SoItem::getId);
        List<SoItem> soItems = soItemMapper.selectJoinList(SoItem.class, wrapper);
        return soItems;
    }
}
