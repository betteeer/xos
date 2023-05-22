package com.inossem.oms.base.svc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inossem.oms.base.svc.domain.BusinessPartner;
import com.inossem.oms.base.svc.domain.VO.BPListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zoutong
 * @date 2022/10/17
 **/
public interface BusinessPartnerMapper extends BaseMapper<BusinessPartner>  {


    List<BusinessPartner> selectPageList(BPListVO bpListVO);

//    List<BusinessPartner> selectBusinessPartnerInfoByBpList(@Param("bp") List<BusinessPartner> businessPartners,@Param("bpListVO") BPListVO bpListVO);
    List<BusinessPartner> selectBusinessPartnerInfoByBpList(@Param("bpListVO") BPListVO bpListVO);

}
