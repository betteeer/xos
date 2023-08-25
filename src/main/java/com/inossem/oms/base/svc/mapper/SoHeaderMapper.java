package com.inossem.oms.base.svc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inossem.oms.base.svc.domain.SoHeader;
import com.inossem.oms.base.svc.domain.VO.OrderHeaderResp;
import com.inossem.oms.base.svc.domain.VO.SalesOrderListQyery;
import com.inossem.oms.base.svc.domain.dashboard.vo.BestSellerVo;
import com.inossem.oms.base.svc.domain.dashboard.req.DashboardReq;
import com.inossem.oms.base.svc.domain.dashboard.vo.SalesPercentageVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * So_Header Mapper接口
 *
 * @author shigf
 * @date 2022-10-17
 */
public interface SoHeaderMapper extends BaseMapper<SoHeader> {


    /**
     * 根据查询条件 获取Sales Order List Info
     *
     * @param soHeaderQueryVO
     * @return
     */
    List<SoHeader> selectListBySoHeaderVo(SalesOrderListQyery soHeaderQueryVO);

    /**
     * 查询无发运记录deliveryHeader
     * @param soNumber
     * @param companyCode
     * @return
     */
    OrderHeaderResp getOrderHeader(@Param("soNumber") String soNumber, @Param("companyCode") String companyCode);

    /**
     * 查询有发运记录deliveryHeader
     * @param soNumber
     * @param companyCode
     * @return
     */
    OrderHeaderResp getOrderHeaders(@Param("soNumber") String soNumber, @Param("companyCode") String companyCode);

    /**
     * wareHouseCode 查询so_item中是否占用该仓库的单子  有  则查询so_header  不为完全发运则 不允许修
     * @param wareHouseCode
     * @param companyCode
     * @return
     */
    List<SoHeader> checkWareHoseCode(@Param("wareHouseCode")String wareHouseCode,@Param("companyCode") String companyCode);

    List<SoHeader> checkSku(@Param("skuCode") String skuCode,@Param("companyCode") String companyCode);

    List<SalesPercentageVo> getSalesAmountAndCount(@Param("res") DashboardReq res);

    List<BestSellerVo> getSkuSoldAmountAndQuantity(@Param("res") DashboardReq res);

}
