package com.inossem.oms.base.svc.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inossem.oms.base.svc.domain.DeliveryItem;
import com.inossem.oms.base.svc.domain.VO.DeliveryShippedResp;
import com.inossem.oms.base.svc.domain.VO.PoDeliveryShippedResp;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author shigf
 * @date 2022-10-20
 */
public interface DeliveryItemMapper extends BaseMapper<DeliveryItem> {

    List<DeliveryShippedResp> selectShippedHeader(@Param("soNumber") String soNumber, @Param("companyCode") String companyCode);

    List<DeliveryItem> selectShippedItems(@Param("deliveryNumber") String deliveryNumber,@Param("companyCode") String companyCode);

    List<DeliveryItem> getDeliveyItemsInfo(@Param("soNumber") String soNumber, @Param("companyCode") String companyCode);

    List<String> getShippedDateIsNull(@Param("soNumber") String soNumber, @Param("companyCode") String companyCode, @Param("deliveryType") String deliveryType);

    Long getShippedDateIsNullItemId(@Param("soNumber") String soNumber, @Param("companyCode") String companyCode, @Param("skuNumber") String skuNumber, @Param("deliveryType") String deliveryType, @Param("kittingSku") String kittingSku,@Param("soItemNum") String soItemNum);

    BigDecimal getShippedQTY(@Param("skuNumber") String skuNumber, @Param("soNumber") String soNumber, @Param("companyCode") String companyCode, @Param("kittingSku") String kittingSku,@Param("soItemNum") String soItemNum);

    BigDecimal getShippedQTYIsDelivery(@Param("skuNumber") String skuNumber, @Param("soNumber") String soNumber, @Param("companyCode") String companyCode, @Param("deliveryNumber") String deliveryNumber);

    int insertBatch(@Param("list") List<DeliveryItem> list);

    List<PoDeliveryShippedResp> selectPoShippedHeader(@Param("poNumber") String poNumber, @Param("companyCode") String companyCode);


    List<DeliveryShippedResp> selectUnBillShippedHeader(@Param("soNumber") String soNumber, @Param("companyCode") String companyCode);

    List<DeliveryShippedResp> selectFullyBillList(@Param("soNumber") String soNumber, @Param("companyCode") String companyCode);

    BigDecimal getPoShippedQTY(@Param("skuNumber") String skuNumber, @Param("poNumber") String soNumber, @Param("companyCode") String companyCode,@Param("poItemNum") String poItemNum);
}
