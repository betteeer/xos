package com.inossem.oms.base.svc.mapper;


import com.github.yulichang.base.MPJBaseMapper;
import com.inossem.oms.base.svc.domain.DeliveryHeader;
import com.inossem.oms.base.svc.domain.DeliveryItem;
import com.inossem.oms.base.svc.domain.VO.DeliveryedListQuery;
import com.inossem.oms.base.svc.domain.VO.DeliveryedListResp;
import com.inossem.oms.base.svc.domain.VO.PoDeliveryedListQuery;
import com.inossem.oms.base.svc.domain.VO.PoDeliveryedListResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author shigf
 * @date 2022-10-20
 */
public interface DeliveryHeaderMapper extends MPJBaseMapper<DeliveryHeader> {

    List<DeliveryedListResp> selectDeliveryList(DeliveryedListQuery deliveryedListQueryVo);

    List<DeliveryedListResp> selectDeliveryListByDeliveryNumber(@Param("list") List<DeliveryItem> list);

    List<PoDeliveryedListResp> selectPoDeliveryList(PoDeliveryedListQuery poDeliveryedListQuery);

    List<PoDeliveryedListResp> selectPoDeliveryListByDeliveryNumber(@Param("list") List<DeliveryItem> list);

    DeliveryHeader checkDeliveryComplateBill(@Param("companyCode") String companyCode, @Param("deliveryNumber") String deliveryNumber);

    List<DeliveryHeader> getUnComplateBill(@Param("soNumber")String soNumber,@Param("companyCode") String companyCode);

    List<DeliveryHeader> getDeliveryNumber(@Param("soNumber")String soNumber,@Param("companyCode") String companyCode);

}
