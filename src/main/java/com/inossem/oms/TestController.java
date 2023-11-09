package com.inossem.oms;

import com.alibaba.fastjson2.JSONObject;
import com.inossem.oms.api.bk.api.BkCoaMappingService;
import com.inossem.oms.api.bk.api.ConnectionUtils;
import com.inossem.oms.api.file.api.FileService;
import com.inossem.oms.base.svc.domain.SystemConnect;
import com.inossem.oms.selftest.AppVersion;
import com.inossem.oms.selftest.AppVersionMapper;
import com.inossem.oms.svc.service.StockBalanceNewService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    FileService fileService;

    @Resource
    AppVersionMapper appVersionMapper;

    @Resource
    BkCoaMappingService bkCoaMappingService;

    @Resource
    private StockBalanceNewService stockBalanceNewService;
    @PostMapping(value="/file")
    public JSONObject test(@RequestPart("file") MultipartFile file) throws IOException {

//        System.out.println(file.getOriginalFilename());
        JSONObject upload = fileService.upload("3002", file);
        System.out.println(upload.getString("url"));
        return upload;

    }

    @GetMapping(value="appVersions")
    public List<AppVersion> getAppVersions() {
        List<AppVersion> appVersions = appVersionMapper.selectList(null);
        return appVersions;
    }
    @GetMapping(value="connection")
    public void getConnection() {
        SystemConnect connection = ConnectionUtils.getConnection("3002");
        System.out.println(connection);
    }
    @GetMapping(value="token")
    public String getToken(@RequestHeader(name="X-UserId") String userId) throws IOException {
//        BkCoaMappingModel s001 = bkCoaMappingService.getOrderTypeMapping("3002", "S001");
//        System.out.println(s001.getCoaJson().get(0).getSkuGroup());
        return userId;
    }

    @GetMapping(value = "stock")
    public Object getStock() {
        return stockBalanceNewService.getSkuStockInWarehouse(Arrays.asList("55888A888", "981190"), Arrays.asList("3002", "3003"), "3002");
    }
}
