package com.inossem.oms.svc.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inossem.oms.base.svc.domain.DeliveryItem;
import com.inossem.oms.base.svc.domain.PoHeader;
import com.inossem.oms.base.svc.domain.SoHeader;
import com.inossem.oms.base.svc.mapper.DeliveryItemMapper;
import com.inossem.oms.base.svc.mapper.PoHeaderMapper;
import com.inossem.oms.base.svc.mapper.SoHeaderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 【仓库】Service业务层处理
 *
 * @author guoh
 * @date 2022-11-20
 */
@Service
@Slf4j
public class WareHouseApiService {

    @Resource
    private DeliveryItemMapper deliveryItemMapper;

    @Resource
    private SoHeaderMapper soHeaderMapper;

    @Resource
    private PoHeaderMapper poHeaderMapper;

    /**
     * wareHouse停用查询是否还有delivery未发运  或者 so / po 未发运的订单
     * 无  true   可停用
     * 有  false  不可停用
     *
     * @param wareHouseCode
     * @param companyCode
     * @return
     */
    public Boolean check(String wareHouseCode, String companyCode) {
        //wareHouseCode 在delivery_item中是否存在未完成的单子  不区分SO/PO
        LambdaQueryWrapper<DeliveryItem> dItemQuery = new LambdaQueryWrapper<DeliveryItem>()
                .and(di -> di.eq(DeliveryItem::getWarehouseCode, wareHouseCode))
                .and(di -> di.eq(DeliveryItem::getCompanyCode, companyCode))
                //delivery完结状态为 0-未完结 未生成物料凭证    但选用的仓库为当前查询的仓库  (标识该单子为空so delivery单或者空po delivery单)
                //delivery完结状态为 1-已完结 已经生成物料凭证
                .and(di -> di.eq(DeliveryItem::getCompleteDelivery, 0))
                .and(di -> di.eq(DeliveryItem::getIsDeleted, 0));
        List<DeliveryItem> dItemList = deliveryItemMapper.selectList(dItemQuery);
        if (dItemList.size() > 0) {
            return false;
        }

        //wareHouseCode 查询so_item中是否占用该仓库的单子  有  则查询so_header  不为完全发运则 不允许修改
        List<SoHeader> soList = soHeaderMapper.checkWareHoseCode(wareHouseCode, companyCode);
        if (soList.size() > 0) {
            return false;
        }

        //wareHouseCode 查询po_item中是否占用该仓库的单子  有  则查询po_header  不为完全发运则 不允许修改
        List<PoHeader> poList = poHeaderMapper.checkWareHoseCode(wareHouseCode, companyCode);
        return poList.size() <= 0;
    }


}
