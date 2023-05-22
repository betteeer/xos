package com.inossem.oms.svc.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.inossem.oms.api.bk.api.BookKeepingService;
import com.inossem.oms.base.svc.domain.BkCoaRel;
import com.inossem.oms.base.svc.domain.CoaList;
import com.inossem.oms.base.svc.domain.Company;
import com.inossem.oms.base.svc.mapper.BkCoaRelMapper;
import com.inossem.oms.mdm.service.CompanyService;
import com.inossem.sco.common.core.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * FI COA mappingService业务层处理
 *
 * @author ruoyi
 * @date 2022-12-10
 */
@Service
@Slf4j
public class BkCoaRelService {
    @Resource
    private BkCoaRelMapper bkCoaRelMapper;

    @Resource
    private BookKeepingService bookKeepingService;

    @Resource
    private CoaListService coaListService;

    @Resource
    private CompanyService companyService;
    /**
     * 查询FI COA mapping
     *
     * @param id FI COA mapping主键
     * @return FI COA mapping
     */
    public BkCoaRel selectBkCoaRelById(Long id) {
        return bkCoaRelMapper.selectBkCoaRelById(id);
    }

    /**
     * 查询FI COA mapping列表
     *
     * @param bkCoaRel FI COA mapping
     * @return FI COA mapping
     */
    public List<BkCoaRel> selectBkCoaRelList(BkCoaRel bkCoaRel) {
        return bkCoaRelMapper.selectBkCoaRelList(bkCoaRel);
    }

    /**
     * 新增FI COA mapping
     *
     * @param bkCoaRel FI COA mapping
     * @return 结果
     */
    public int insertBkCoaRel(BkCoaRel bkCoaRel) {
        bkCoaRel.setCreateTime(DateUtils.getNowDate());
        return bkCoaRelMapper.insertBkCoaRel(bkCoaRel);
    }

    /**
     * 修改FI COA mapping
     *
     * @param bkCoaRel FI COA mapping
     * @return 结果
     */
    public int updateBkCoaRel(BkCoaRel bkCoaRel) {
        bkCoaRel.setUpdateTime(DateUtils.getNowDate());
        return bkCoaRelMapper.updateBkCoaRel(bkCoaRel);
    }

    /**
     * 批量删除FI COA mapping
     *
     * @param ids 需要删除的FI COA mapping主键
     * @return 结果
     */
    public int deleteBkCoaRelByIds(Long[] ids) {
        return bkCoaRelMapper.deleteBkCoaRelByIds(ids);
    }

    /**
     * 删除FI COA mapping信息
     *
     * @param id FI COA mapping主键
     * @return 结果
     */
    public int deleteBkCoaRelById(Long id) {
        return bkCoaRelMapper.deleteBkCoaRelById(id);
    }

    /**
     * 同步数据
     *
     * @param companyCode
     */
    public void sync(String companyCode) {

//        Company company = remoteMdmService.getCompanyByCode(companyCode).getData();
        Company company = companyService.getCompany(companyCode);
        try {
            JSONArray array = bookKeepingService.coaList(company.getCompanyCodeEx(), Integer.parseInt(company.getOrgidEx()));
            if (array == null || array.isEmpty()) {
                log.info("调用bk没有拿到数据，不更新coa相关表数据");
                return;
            }

            // 找到这个公司下的coamapping ，然后删除
            BkCoaRel rel = new BkCoaRel();
            rel.setCompanyCode(companyCode);
            List<BkCoaRel> rels = selectBkCoaRelList(rel);
            for (BkCoaRel r : rels) {
                deleteBkCoaRelById(r.getId());
            }

            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                BkCoaRel bk = new BkCoaRel();
                bk.setCompanyId(obj.getInteger("company_id"));
                bk.setCompanyCode(companyCode);
                bk.setCoaCode(obj.getString("coa_code"));
                bk.setCoaId(obj.getInteger("coa_id"));
                bk.setCode(obj.getString("code"));
                bk.setCoaName(obj.getString("coa_name"));
                bk.setCodeCategory(obj.getInteger("code_category"));
                bk.setCompanyCodeEx(obj.getString("company_code"));
                bk.setCreateTime(new Date());
//                bk.setCreator(UserInfoUtils.getSysUserName());
                bk.setCreator("admin");
                // ###todo###
                bk.setDebitCoaCode(obj.getString("debit_coa_code"));
                bk.setDebitCoaId(obj.getInteger("debit_coa_id"));
                bk.setDebitCoaName(obj.getString("debit_coa_name"));
                bk.setDelFlag(0);
                bk.setType(obj.getString("type"));
                insertBkCoaRel(bk);
            }

            JSONArray a = bookKeepingService.glList(company.getCompanyCodeEx());
            if (a == null || a.isEmpty()) {
                log.info("调用bk没有拿到数据，不更新coa相关表数据");
                return;
            }
            List<CoaList> cls = coaListService.selectCoaListList(new CoaList());
            for (CoaList c : cls) {
                coaListService.deleteCoaListById(c.getId());
            }
            for (int i = 0; i < a.size(); i++) {
                JSONObject o = a.getJSONObject(i);
                CoaList cl = new CoaList();
                cl.setAccountAlias(null);
                cl.setAccountDes(o.getString("name"));
                cl.setAccountId(o.getString("id"));
                cl.setAccountName(o.getString("account_code"));
                cl.setCompanyCode(companyCode);
                cl.setCompanyCodeEx(Long.parseLong(o.getString("company_code")));
                cl.setGmtCreate(new Date());
                cl.setGmtModified(new Date());
                cl.setIsDeleted(0);
                // ###todo###
                cl.setModifiedBy("1");
                cl.setCreateBy("1");
//                cl.setModifiedBy(UserInfoUtils.getSysUserName());
//                cl.setCreateBy(UserInfoUtils.getSysUserName());

                coaListService.insertCoaList(cl);
            }

        } catch (IOException e) {
            log.error("sync coa info err", e);
        }
    }
}
