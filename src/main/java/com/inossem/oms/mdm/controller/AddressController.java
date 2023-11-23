package com.inossem.oms.mdm.controller;

import com.inossem.oms.base.svc.domain.Address;
import com.inossem.oms.base.svc.domain.VO.AddressQueryVo;
import com.inossem.oms.base.svc.domain.VO.AddressSaveVo;
import com.inossem.oms.mdm.service.AddressService;
import com.inossem.sco.common.core.domain.R;
import com.inossem.sco.common.core.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author kgh
 * @date 2022-11-05 15:32
 */
@RestController
@RequestMapping("/mdm/address")
@Slf4j
@Api(tags = {"address"})
public class AddressController extends BaseController {

    @Autowired
    private AddressService addressService;

    /**
     * 保存地址到数据库
     *
     * @param address
     * @return
     */
    @ApiOperation(value = "save address", notes = "save address to database")
    @PostMapping("/save")
    public R<Address> saveAddress(@RequestBody AddressSaveVo address) {
        addressService.save(address);
        return R.ok();
    }

    /**
     * 更新地址
     *
     * @param address
     * @return
     */
    @ApiOperation(value = "modify address", notes = "modify address to database")
    @PostMapping("/modify")
    public R<Address> modifyAddress(@RequestBody AddressSaveVo address) {
        addressService.modifyAddress(address);
        return R.ok();
    }

    /**
     * 获取地址信息
     *
     * @param address
     * @return
     */
    @ApiOperation(value = "get address info", notes = "get address info")
    @PostMapping("/getAddressInfo")
    public R<List<Address>> getAddressInfo(@RequestBody AddressQueryVo address) {
        return R.ok(addressService.getAddress(address));
    }

}
