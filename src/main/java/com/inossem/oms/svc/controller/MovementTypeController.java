package com.inossem.oms.svc.controller;

import com.inossem.oms.base.svc.domain.MovementType;
import com.inossem.oms.svc.service.MovementTypeService;
import com.inossem.sco.common.core.web.controller.BaseController;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/svc/movementType")
public class MovementTypeController extends BaseController {

    @Resource
    private MovementTypeService movementTypeService;

    @GetMapping("/list")
    public AjaxResult<MovementType> getList(
            @RequestParam(required = false, value = "type") String type,
            @RequestParam(required = false, value = "companyCode") String companyCode) {
        if (type == null) {
            type = "";
        }
        if (!StringUtils.isEmpty(type) && !ArrayUtils.contains(new String[]{"2", "3"}, type))
            return AjaxResult.error("bad type");
        if (StringUtils.isEmpty(companyCode)) {
            return AjaxResult.error("missing companyCode");
        }
        return AjaxResult.success().withData(movementTypeService.getListByType(type, companyCode));
    }
}
