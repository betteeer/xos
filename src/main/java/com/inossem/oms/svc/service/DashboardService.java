package com.inossem.oms.svc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.inossem.oms.base.svc.domain.PoHeader;
import com.inossem.oms.base.svc.domain.SalesChannel;
import com.inossem.oms.base.svc.domain.SoHeader;
import com.inossem.oms.base.svc.domain.dashboard.dto.MostInventoryDto;
import com.inossem.oms.base.svc.domain.dashboard.dto.SalesRevenueAxisYDto;
import com.inossem.oms.base.svc.domain.dashboard.req.DashboardReq;
import com.inossem.oms.base.svc.domain.dashboard.req.MostInventoryReq;
import com.inossem.oms.base.svc.domain.dashboard.vo.*;
import com.inossem.oms.base.svc.mapper.PoHeaderMapper;
import com.inossem.oms.base.svc.mapper.SalesChannelMapper;
import com.inossem.oms.base.svc.mapper.SoHeaderMapper;
import com.inossem.oms.base.svc.mapper.StockBalanceMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
public class DashboardService {

    @Resource
    private SoHeaderMapper soHeaderMapper;

    @Resource
    private PoHeaderMapper poHeaderMapper;

    @Resource
    private SalesChannelMapper salesChannelMapper;

    @Resource
    private StockBalanceMapper stockBalanceMapper;

    public TitleBarVo getTitleBar(DashboardReq req) {
        /*
        *   Sales：select sum(gross_amount) from so_header where order_date in Date and is_deleted = 0
            Purchase：select sum(gross_amount) from po_header where order_date in Date and is_deleted = 0
            Profit：Sales - Purchase
            Sales Orders：select count(so_number)  from so_header where order_date in Date and is_deleted = 0
            Unfinished Orders：select count(so_number)  from so_header where order_date in Date and is_deleted = 0 and billing_status NE 'FUIN'
        * */
        QueryWrapper<SoHeader> soHeaderWrapper = new QueryWrapper<>();
        soHeaderWrapper.between("order_date", req.getStartDate(), req.getEndDate())
                .eq("is_deleted", 0)
                .eq("company_code", req.getCompanyCode());
        SoHeader salesOrder = soHeaderMapper.selectOne(soHeaderWrapper.select("IFNULL(SUM(gross_amount),0) AS grossAmount,COUNT(so_number) AS id"));
        SoHeader unfinishedOrder = soHeaderMapper.selectOne(soHeaderWrapper.select("COUNT(so_number) AS id").ne("billing_status ", "FUIN"));

        QueryWrapper<PoHeader> poHeaderWrapper = new QueryWrapper<>();
        poHeaderWrapper.select("IFNULL(SUM(gross_amount),0) as grossAmount")
                .between("order_date", req.getStartDate(), req.getEndDate())
                .eq("is_deleted", 0)
                .eq("company_code", req.getCompanyCode());
        BigDecimal purchases = poHeaderMapper.selectOne(poHeaderWrapper).getGrossAmount();

        TitleBarVo titleBarVo = new TitleBarVo();
        titleBarVo.setSales(salesOrder.getGrossAmount());
        titleBarVo.setPurchases(purchases);
        titleBarVo.setProfit(salesOrder.getGrossAmount().subtract(purchases));
        titleBarVo.setSalesOrders(salesOrder.getId());
        titleBarVo.setUnfinishedOrders(unfinishedOrder.getId());
        return titleBarVo;
    }

/*    public SalesRevenueVo getSalesRevenue1(DashboardReq req) {

        //startDate - endDate之间的所有日期
        LocalDate startDate = LocalDate.parse(req.getStartDate());
        LocalDate endDate = LocalDate.parse(req.getEndDate());
        List<LocalDate> rangeDates = getDatesInRange(startDate, endDate);

        SalesRevenueVo salesRevenueVo = new SalesRevenueVo();
        salesRevenueVo.setLabels(rangeDates);
        ArrayList<SalesRevenueAxisYDto> yAxisDtos = new ArrayList();

        LambdaQueryWrapper<SalesChannel> salesChannelWrapper = Wrappers.lambdaQuery();
        salesChannelWrapper.eq(SalesChannel::getCompanyCode, req.getCompanyCode())
                .eq(SalesChannel::getStatus, 1);
        List<SalesChannel> channels = salesChannelMapper.selectList(salesChannelWrapper);

        for (SalesChannel channel : channels) {
//            QueryWrapper<SoHeader> soHeaderWrapper = new QueryWrapper<>();
//            soHeaderWrapper.select("order_date AS orderDate,IFNULL(SUM(gross_amount),0) AS grossAmount")
//                    .between("order_date", req.getStartDate(), req.getEndDate())
//                    .eq("is_deleted", 0)
//                    .eq("company_code", req.getCompanyCode())
//                    .groupBy("order_date")
//                    .orderBy(true, true, "order_date");
//            soHeaderWrapper.eq("channel_id", channel.getId());
//            List<SoHeader> soAmountList = soHeaderMapper.selectList(soHeaderWrapper);

            double[] amounts = new double[rangeDates.size()];//有默认值0

            Random random = new Random();
            for (int i = 0; i < amounts.length; i++) {

                // 生成一个带有两位小数的随机数
                double randomNumber = random.nextDouble() * 100000; // 生成0到100之间的随机数
                randomNumber = Math.round(randomNumber * 100.0) / 100.0; // 保留两位小数
                amounts[i] = randomNumber;
            }
//            if (!CollectionUtils.isEmpty(soAmountList)) {
//                for (SoHeader soAmount : soAmountList) {
//                    LocalDate localDate = soAmount.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//                    int i = (int)ChronoUnit.DAYS.between(startDate,localDate);
//                    amounts[i] = soAmount.getGrossAmount().doubleValue();
//                }
//            }

            SalesRevenueAxisYDto yAxisDto = new SalesRevenueAxisYDto();
            yAxisDto.setLabel(channel.getChannelDes());
            yAxisDto.setData(amounts);
            yAxisDtos.add(yAxisDto);
        }
        salesRevenueVo.setDatasets(yAxisDtos);
        return salesRevenueVo;
    }*/

    public SalesRevenueVo getSalesRevenue(DashboardReq req) {

        SalesRevenueVo salesRevenueVo = new SalesRevenueVo();
        //1.X轴：startDate - endDate之间的所有日期
        List<LocalDate> rangeDates = getDatesInRange(LocalDate.parse(req.getStartDate()), LocalDate.parse(req.getEndDate()));
        salesRevenueVo.setLabels(rangeDates);

        //2.Y轴
        ArrayList<SalesRevenueAxisYDto> yAxisDtos = new ArrayList();
        //2.1获取该companycode下的所有channel
        LambdaQueryWrapper<SalesChannel> salesChannelWrapper = Wrappers.lambdaQuery();
        salesChannelWrapper.eq(SalesChannel::getCompanyCode, req.getCompanyCode())
                .eq(SalesChannel::getStatus, 1);
        List<SalesChannel> channels = salesChannelMapper.selectList(salesChannelWrapper);

        for (SalesChannel channel : channels) {
            //2.2获取每个channel的Y轴数值
            yAxisDtos.add(getChannelAmountsPerDate(req, rangeDates.size(), channel));
        }
        //2.3 Others-channel：channel_id为null
        SalesChannel othersChannel = new SalesChannel();
        othersChannel.setChannelDes("Others");
        yAxisDtos.add(getChannelAmountsPerDate(req, rangeDates.size(), othersChannel));

        //3.塞值&返回
        salesRevenueVo.setDatasets(yAxisDtos);
        return salesRevenueVo;
    }

    @NotNull
    private SalesRevenueAxisYDto getChannelAmountsPerDate(DashboardReq req, int size, SalesChannel channel) {

        //1.去so_header表中查询数据
        QueryWrapper<SoHeader> soHeaderWrapper = new QueryWrapper<>();
        soHeaderWrapper.select("order_date AS orderDate,IFNULL(SUM(gross_amount),0) AS grossAmount")
                .between("order_date", req.getStartDate(), req.getEndDate())
                .eq("is_deleted", 0)
                .eq("company_code", req.getCompanyCode())
                .groupBy("order_date")
                .orderBy(true, true, "order_date");
        if (Objects.nonNull(channel.getId())) {
            soHeaderWrapper.eq("channel_id", channel.getId());
        } else {
            soHeaderWrapper.isNull("channel_id");
        }
        List<SoHeader> soAmountList = soHeaderMapper.selectList(soHeaderWrapper);

        //2.X轴的所有日期都要有对应的值，没有填充0
        double[] amounts = new double[size];//有默认值0
        LocalDate startDate = LocalDate.parse(req.getStartDate());
        if (!CollectionUtils.isEmpty(soAmountList)) {
            for (SoHeader soAmount : soAmountList) {
                LocalDate localDate = soAmount.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                int i = (int) ChronoUnit.DAYS.between(startDate, localDate);
                amounts[i] = soAmount.getGrossAmount().doubleValue();
            }
        }
        SalesRevenueAxisYDto yAxisDto = new SalesRevenueAxisYDto();
        yAxisDto.setLabel(channel.getChannelDes());
        yAxisDto.setData(amounts);
        return yAxisDto;
    }

    private List<LocalDate> getDatesInRange(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> datesInRange = new ArrayList<>();
        long numOfDays = ChronoUnit.DAYS.between(startDate, endDate);
        for (long i = 0; i <= numOfDays; i++) {
            LocalDate date = startDate.plusDays(i);
            datesInRange.add(date);
        }
        return datesInRange;
    }

    public List<SalesPercentageVo> getSalesPercentage(DashboardReq req) {
        List<SalesPercentageVo> salesPercentageVos = soHeaderMapper.getSalesAmountAndCount(req);
        return salesPercentageVos;
    }

    public List<BestSellerVo> getBestSeller(DashboardReq req) {
        List<BestSellerVo> bestSellerVos = soHeaderMapper.getSkuSoldAmountAndQuantity(req);
/*        Random random = new Random();
        double randomNumber = random.nextDouble() * 100000; // 生成0到100之间的随机数
        randomNumber = Math.round(randomNumber * 100.0) / 100.0; // 保留两位小数
        for (int i = 0; i < bestSellerVos.size(); i++) {
            bestSellerVos.get(i).setAmount(randomNumber - i * 100);
        }*/
        return bestSellerVos;
    }

    public MostInventoryVo getMostInventory(MostInventoryReq req) {

        MostInventoryVo mostInventoryVo = stockBalanceMapper.getStockTotalAmountAndQuantity(req);
        List<MostInventoryDto> mostInventoryDtos = stockBalanceMapper.getStockAmountAndQuantity(req);
        BigDecimal top20Amount = new BigDecimal(0);
        BigDecimal top20Quantity = new BigDecimal(0);
        for (MostInventoryDto mostInventoryDto : mostInventoryDtos) {
            top20Amount = top20Amount.add(mostInventoryDto.getAmount());
            top20Quantity = top20Quantity.add(mostInventoryDto.getQuantity());
        }
        mostInventoryVo.setTop20Amount(top20Amount);
        mostInventoryVo.setTop20Quantity(top20Quantity);
        mostInventoryVo.setDatasets(mostInventoryDtos);
        return mostInventoryVo;
    }
}