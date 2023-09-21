package com.inossem.oms.svc.service;

import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.inossem.oms.base.svc.domain.DTO.PoItemSearchFormDTO;
import com.inossem.oms.base.svc.domain.PoHeader;
import com.inossem.oms.base.svc.domain.PoItem;
import com.inossem.oms.base.svc.domain.SkuMaster;
import com.inossem.oms.base.svc.mapper.PoItemMapper;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author shigf
 * @date 2022-11-04
 */
@Service
@Slf4j
public class PoItemService
{
    @Resource
    private PoItemMapper poItemMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public PoItem selectPoItemById(Long id)
    {
        return poItemMapper.selectPoItemById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param poItem 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    public List<PoItem> selectPoItemList(PoItem poItem)
    {
        return poItemMapper.selectPoItemList(poItem);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param poItem 【请填写功能名称】
     * @return 结果
     */
    public int insertPoItem(PoItem poItem)
    {
        return poItemMapper.insertPoItem(poItem);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param poItem 【请填写功能名称】
     * @return 结果
     */
    public int updatePoItem(PoItem poItem)
    {
        return poItemMapper.updatePoItem(poItem);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    public int deletePoItemByIds(Long[] ids)
    {
        return poItemMapper.deletePoItemByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deletePoItemById(Long id)
    {
        return poItemMapper.deletePoItemById(id);
    }

    public List<PoItem> getList(PoItemSearchFormDTO form) {
        log.info(">>>查询列表，入参：[{}]", form);
        MPJLambdaWrapper<PoItem> wrapper = new MPJLambdaWrapper<>();
        if (form.getItemType() == null) {
            form.setItemType(new ArrayList<>());
        }
        wrapper.selectAll(PoItem.class);
        // 指定 company code数据范围
        wrapper.eq(PoItem::getCompanyCode, form.getCompanyCode());
        // 保留有效的数据
        wrapper.eq(PoItem::getIsDeleted, 0);
//        // 拼接 itemType
//        wrapper.in(StringUtils.isNotEmpty(form.getItemType()), PoItem::getItemType, form.getItemType());
        // itemType全选或者全不选
        if (form.getItemType().size() == 2 || form.getItemType().size() == 0) {
            // warehouse不是空的
            // 那么需要筛选 itemType=INVENTORY，WAREHOUSE满足条件的 + 所有的itemType=SERVICE的
            if (StringUtils.isNotEmpty(form.getWarehouseCode())) {
                wrapper.nested(i -> {
                   i.eq(PoItem::getItemType, "IN").in(PoItem::getWarehouseCode, form.getWarehouseCode())
                           .or().eq(PoItem::getItemType, "SE");
                });
            }
            //如果warehouse是空的
            // 那么需要筛选 所有
        } else {
//            itemType只有一种, 先只筛这一种，然后如果是IN的，还需要再筛选warehouse
            wrapper.in(PoItem::getItemType, form.getItemType());
            if (form.getItemType().contains("IN")) {
                wrapper.in(StringUtils.isNotEmpty(form.getWarehouseCode()), PoItem::getWarehouseCode, form.getWarehouseCode());
            }
        }
        // 拼接unit price
        wrapper.between(StringUtils.isNotNull(form.getUnitPriceStart()), PoItem::getUnitPrice, form.getUnitPriceStart(), form.getUnitPriceEnd());
        // 拼接purchase qty
        wrapper.between(StringUtils.isNotNull(form.getPurchaseQtyStart()), PoItem::getPurchaseQty, form.getPurchaseQtyStart(), form.getPurchaseQtyEnd());
        // 拼接currency code
        wrapper.in(StringUtils.isNotEmpty(form.getCurrencyCode()), PoItem::getCurrencyCode, form.getCurrencyCode());
        // 拼接 tax exmpt
        wrapper.in(StringUtils.isNotEmpty(form.getTaxExmpt()), PoItem::getTaxExmpt, form.getTaxExmpt());
        // join skuMaster，查询skuName，并根据searchText进行过滤
        wrapper.leftJoin(SkuMaster.class, SkuMaster::getSkuNumber, PoItem::getSkuNumber, ext -> {
            ext.selectAs(SkuMaster::getSkuName, PoItem::getSkuName);
            ext.nested(StringUtils.isNotEmpty(form.getSearchText()), i ->
                    i.like(SkuMaster::getSkuNumber, form.getSearchText())
                            .or().like(SkuMaster::getSkuName, form.getSearchText())
                            .or().like(PoItem::getPoNumber, form.getSearchText()));
            return ext;
        });
        // join PoHeader 查询orderStatus
        wrapper.leftJoin(PoHeader.class, PoHeader::getPoNumber, PoItem::getPoNumber,
                ext -> ext.selectAs(PoHeader::getOrderStatus,PoItem::getOrderStatus));
        // 拼接order by
        wrapper.orderBy(StringUtils.isNotNull(form.getOrderBy()), form.getIsAsc(), StringUtils.toUnderScoreCase(form.getOrderBy()));
        // 排序的字段值相同则按照id倒序
        wrapper.orderBy(true, false, PoItem::getId);
        List<PoItem> poItems = poItemMapper.selectJoinList(PoItem.class, wrapper);
        return poItems;
    }
}
