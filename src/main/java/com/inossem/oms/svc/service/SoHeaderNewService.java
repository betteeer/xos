package com.inossem.oms.svc.service;

import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.inossem.oms.base.svc.domain.BlockReason;
import com.inossem.oms.base.svc.domain.DTO.SoHeaderSearchForm;
import com.inossem.oms.base.svc.domain.SalesChannel;
import com.inossem.oms.base.svc.domain.SoHeader;
import com.inossem.oms.base.svc.mapper.BlockReasonMapper;
import com.inossem.oms.base.svc.mapper.SoHeaderMapper;
import com.inossem.sco.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class SoHeaderNewService {
    @Resource
    private SoHeaderMapper soHeaderMapper;

    @Resource
    private BlockReasonMapper blockReasonMapper;

    public List<SoHeader> getNewList(SoHeaderSearchForm form) {
        log.info(">>>查询列表，入参：[{}]", form);
        MPJLambdaWrapper<SoHeader> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(SoHeader.class);

        // 查询 channel name
        wrapper.leftJoin(SalesChannel.class, SalesChannel::getChannelCode, SoHeader::getChannelId, ext ->
                ext.selectAs(SalesChannel::getChannelDes, SoHeader::getChannelName));

        // 指定 company code数据范围
        wrapper.eq(SoHeader::getCompanyCode, form.getCompanyCode());
        wrapper.eq(SoHeader::getIsDeleted, 0);
        // 根据searchText，匹配soNumber,customer no,bp name, reference number
        wrapper.nested(StringUtils.isNotEmpty(form.getSearchText()), i -> {
            i.like(SoHeader::getSoNumber, form.getSearchText())
                    .or().like(SoHeader::getBpCustomer, form.getSearchText())
                    .or().like(SoHeader::getBpName, form.getSearchText())
                    .or().like(SoHeader::getReferenceNumber, form.getSearchText());
        });
        // 订单类型
        wrapper.in(StringUtils.isNotEmpty(form.getOrderType()), SoHeader::getOrderType, form.getOrderType());
        // 订单状态
        wrapper.in(StringUtils.isNotEmpty(form.getOrderStatus()), SoHeader::getOrderStatus, form.getOrderStatus());
        // 订单发货状态
        wrapper.in(StringUtils.isNotEmpty(form.getDeliveryStatus()), SoHeader::getDeliveryStatus, form.getDeliveryStatus());
        //订单开票状态
        wrapper.in(StringUtils.isNotEmpty(form.getInvoiceStatus()), SoHeader::getBillingStatus, form.getInvoiceStatus());
        // 订单日期
        wrapper.between(StringUtils.isNotNull(form.getOrderDateStart()), SoHeader::getOrderDate, form.getOrderDateStart(), form.getOrderDateEnd());
        // gross amount
        wrapper.between(StringUtils.isNotNull(form.getGrossAmountStart()), SoHeader::getGrossAmount, form.getGrossAmountStart(), form.getGrossAmountEnd());
        // net amount
        wrapper.between(StringUtils.isNotNull(form.getNetAmountStart()), SoHeader::getNetAmount, form.getNetAmountStart(), form.getNetAmountEnd());
        // currency code
        wrapper.in(StringUtils.isNotEmpty(form.getCurrencyCode()), SoHeader::getCurrencyCode, form.getCurrencyCode());
        // channel
        wrapper.in(StringUtils.isNotEmpty(form.getChannelIds()), SoHeader::getChannelId, form.getChannelIds());
        // payment term
        wrapper.in(StringUtils.isNotEmpty(form.getPaymentTerm()), SoHeader::getPaymentTerm, form.getPaymentTerm());
        // is delivery block
        wrapper.in(StringUtils.isNotEmpty(form.getIsDeliveryBlock()), SoHeader::getIsDeliveryBlock, form.getIsDeliveryBlock());
        // is billing block
        wrapper.in(StringUtils.isNotEmpty(form.getIsBillingBlock()), SoHeader::getIsBillingBlock, form.getIsBillingBlock());
        // create by
        wrapper.in(StringUtils.isNotEmpty(form.getCreateBy()), SoHeader::getCreateBy, form.getCreateBy());
        // modified by
        wrapper.in(StringUtils.isNotEmpty(form.getModifiedBy()), SoHeader::getModifiedBy, form.getModifiedBy());

        // 拼接order by
        wrapper.orderBy(StringUtils.isNotNull(form.getOrderBy()), form.getIsAsc(), StringUtils.toUnderScoreCase(form.getOrderBy()));
        // 排序的字段值相同则按照id倒序
        wrapper.orderBy(true, false, SoHeader::getId, SoHeader::getId);

        return soHeaderMapper.selectJoinList(SoHeader.class, wrapper);
    }

    public List<BlockReason> getBlockReason(String type) {
        MPJLambdaWrapper<BlockReason> wrapper = JoinWrappers.lambda(BlockReason.class)
            .selectAll(BlockReason.class)
            .eq(BlockReason::getBlockType, type);
        return blockReasonMapper.selectList(wrapper);
    }
}
