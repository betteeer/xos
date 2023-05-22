package com.inossem.oms.api.bk.api;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.inossem.oms.api.bk.model.PoInvoiceModel;
import com.inossem.oms.api.bk.model.SoInvoiceModel;
import com.inossem.oms.base.svc.domain.BusinessPartner;
import com.inossem.oms.base.svc.domain.Company;
import com.inossem.oms.base.svc.domain.SystemConnect;
import com.inossem.oms.base.svc.domain.VO.AddressVO;
import com.inossem.oms.base.utils.HttpParamsUtils;
import com.inossem.oms.mdm.service.CompanyService;
import com.inossem.oms.svc.service.SystemConnectService;
import com.inossem.sco.common.core.utils.StringUtils;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author kgh
 * @date 2022-12-08 14:29
 */
@Service
public class BookKeepingService {
    private static final Logger logger = LoggerFactory.getLogger(BookKeepingService.class);

    @Resource
    private SystemConnectService systemConnectService;

    @Resource
    private CompanyService companyService;
    /**
     * 获取连接信息
     *
     * @param companyCode ex外部
     * @return
     */
    public SystemConnect getConnect(String companyCode) {
        SystemConnect connect = new SystemConnect();
        connect.setCompanyCodeEx(Long.parseLong(companyCode));
        connect.setExSystem("bk");
//        List<SystemConnect> connects = remoteSvcService.connectLists(connect);
        List<SystemConnect> connects = systemConnectService.selectSyctemConectList(connect);
        if (connects == null || connects.isEmpty()) {
            throw new RuntimeException("获取TOken失败，" + companyCode + " 未查到bk账号信息。");
        }
        return connects.get(0);
    }

    /**
     * 获取连接信息
     *
     * @param companyCode 内部
     * @return
     */
    public SystemConnect getConnectCom(String companyCode) {
        SystemConnect connect = new SystemConnect();
        connect.setCompanyCode(companyCode);
        connect.setExSystem("bk");
        List<SystemConnect> connects = systemConnectService.selectSyctemConectList(connect);
        if (connects == null || connects.isEmpty()) {
            throw new RuntimeException("获取TOken失败，" + companyCode + " 未查到bk账号信息。");
        }
        return connects.get(0);
    }

    /**
     * 获取token接口
     *
     * @return
     * @throws IOException
     */
    public String getToken(SystemConnect connect) throws IOException {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType mediaType = MediaType.parse("application/json");

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("strategy", "local");
        paramsMap.put("account", connect.getUserNameEx());
        paramsMap.put("password", connect.getPasswordEx());

        String params = JSONObject.toJSONString(paramsMap,
                JSONWriter.Feature.WriteNullStringAsEmpty);
        logger.info("params:{}", params);

        RequestBody body = RequestBody.create(mediaType, params);

        Request request = new Request.Builder()
                .url(connect.getApiUrl() + "/users/api/authentication")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();

        String response = client
                .newCall(request)
                .execute().body().string();
        JSONObject obj = JSONObject.parseObject(response);
        logger.info("accessToken:{}", obj.getString("accessToken"));
        if (StringUtils.isNotBlank(obj.getString("accessToken"))) {
            return "Bearer " + obj.getString("accessToken");
        } else {
            throw new RuntimeException("获取Token失败，" + response);
        }
    }

    public JSONArray coaList(String companyCodeEx, Integer companyIdEx) throws IOException {
        SystemConnect connect = getConnect(companyCodeEx);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/json");

        /*JSONObject data = new JSONObject();
        data.put("companyCode", companyCodeEx);
        data.put("companyId", companyIdEx);
        // oms固定为2
        data.put("type", "2");
        data.put("pageSize", 1000);
        String reqBody = data.toJSONString();
        logger.info("请求的参数为：{}", reqBody);*/

        //RequestBody body = RequestBody.create(mediaType, reqBody);
        logger.info("调用bk v2 coa mapping");
        Request request = new Request.Builder()
                .url(connect.getApiUrl() + "/system-preferences/api/v1/coa-rel?company_id=" + companyIdEx + "&company_code=" + companyCodeEx + "&type=2&$limit=-1")
                .method("GET", null)
                .addHeader("Authorization", getToken(connect))
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200) {
            throw new RuntimeException("调用BK接口失败");
        }
        String responseBody = response.body().string();
        logger.info("接收到的数据为：{}", responseBody);
        //JSONObject resObj = JSONObject.parseObject(responseBody);
        JSONArray resObj = JSONArray.parseArray(responseBody);
        return resObj;
    }

    public JSONArray glList(String companyCodeEx) throws IOException {
        SystemConnect connect = getConnect(companyCodeEx);
        logger.info("调用bk v2 coa list");

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(connect.getApiUrl() + "/bkp-engine/bk/coa" + "?company=" + companyCodeEx)
                .get()
                .addHeader("Authorization", getToken(connect))
                .build();
        Response response = client.newCall(request).execute();

        if (response.code() != 200) {
            throw new RuntimeException("调用BK接口失败");
        }
        String responseBody = response.body().string();
        logger.info("接收到的数据为：{}", responseBody);
        JSONObject resObj = JSONObject.parseObject(responseBody);
        return resObj.getJSONObject("data").getJSONArray("rows");
    }

    /**
     * 获取科目表
     *
     * @param type
     * @param companyId
     * @param companyCode
     * @param coaCode
     * @throws IOException
     */
    public void getCoa(String type, String companyId, String companyCode, String coaCode) throws IOException {
        SystemConnect connect = getConnect(companyCode);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        JSONObject requestBody = new JSONObject();
        requestBody.put("type", type);
        requestBody.put("companyId", companyId);
        requestBody.put("companyCode", companyCode);
        requestBody.put("coaCode", coaCode);
        requestBody.put("pageSize", 999999999);
        requestBody.put("pageIndex", 1);


        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, requestBody.toString());
        Request request = new Request.Builder()
                .url(connect.getApiUrl() + "/web/bk/coa/rel/list")
                .method("POST", body)
                .addHeader("token", getToken(connect))
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();

    }

    public JSONObject bpCustomerList(String companyId, String companyCode, String bpName) throws IOException {
        SystemConnect connect = getConnectCom(companyCode);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("company_id", companyId);
        paramsMap.put("company_code", companyCode);
        paramsMap.put("contact_name", bpName);

        String url = connect.getApiUrl() + "/system-preferences/api/v1/contact";

        url += HttpParamsUtils.getBodyParams(paramsMap);
        logger.info(">>> 查询bp详情,请求地址url:{}", url);

        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", getToken(connect))
                .build();

        Response response = client.newCall(request).execute();
        String bo = response.body().string();
        logger.info("接收到的数据为：{}", bo);

        JSONObject res = JSONObject.parseObject(bo);

//        JSONArray list = res.getJSONObject("data").getJSONArray("list");
        JSONArray list = res.getJSONArray("data");
        if (list == null || list.isEmpty()) {
            return null;
        }

        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = list.getJSONObject(i);
            String customerName = obj.getString("contact_name");
            if (bpName.equals(customerName)) {
                return obj;
            } else {
                logger.info("获取 bp customer list not match : {},  {}", bpName, customerName);
            }
        }

        return null;
    }

    public JSONArray bpCustomerListLike(String companyId, String companyCode) throws IOException {
        SystemConnect connect = getConnectCom(companyCode);

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("company_id", companyId);
        paramsMap.put("company_code", companyCode);
        paramsMap.put("$limit", -1);


        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        String url = connect.getApiUrl() + "/system-preferences/api/v1/contact";

        url += HttpParamsUtils.getBodyParams(paramsMap);
        logger.info(">>> 查询bp详情,请求地址url:{}", url);

        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", getToken(connect))
                .build();

        Response response = client.newCall(request).execute();
        String bo = response.body().string();
        logger.info("接收到的数据为：{}", bo);

        return JSONArray.parseArray(bo);
    }

//    public JSONArray bpCustomerListLike(String companyCode, String bpName) throws IOException {
//        SystemConnect connect = getConnectCom(companyCode);
//
//        JSONObject data = new JSONObject();
//        data.put("customerName", bpName);
//        data.put("pageSize", "99999");
//        logger.info("请求的参数为：{}", data.toJSONString());
//
//
//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .build();
//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, data.toString());
//        Request request = new Request.Builder()
//                .url(connect.getApiUrl() + "/web/bk/customer/list")
//                .method("POST", body)
//                .addHeader("token", getToken(connect))
//                .addHeader("Content-Type", "application/json")
//                .build();
//        Response response = client.newCall(request).execute();
//        String bo = response.body().string();
//        logger.info("接收到的数据为：{}", bo);
//        JSONObject res = JSONObject.parseObject(bo);
//
//        return res.getJSONObject("data").getJSONArray("list");
//    }

    public JSONObject bpVendorList(String companyCode, String bpName) throws IOException {
        SystemConnect connect = getConnectCom(companyCode);

        JSONObject data = new JSONObject();
        data.put("supplierName", bpName);
        data.put("pageSize", "99999");
        logger.info("请求的参数为：{}", data.toJSONString());


        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, data.toString());
        Request request = new Request.Builder()
                .url(connect.getApiUrl() + "/web/bk/supplier/list")
                .method("POST", body)
                .addHeader("token", getToken(connect))
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        String bo = response.body().string();
        logger.info("接收到的数据为：{}", bo);
        JSONObject res = JSONObject.parseObject(bo);

        JSONArray list = res.getJSONObject("data").getJSONArray("list");
        if (list == null || list.isEmpty()) {
            return null;
        }

        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = list.getJSONObject(i);
            String customerName = obj.getString("supplierName");
            if (bpName.equals(customerName)) {
                return obj;
            } else {
                logger.info("获取 bp vendor list not match : {},  {}", bpName, customerName);
            }
        }

        return null;
    }

    public JSONArray bpVendorListList(String companyCode, String bpName) throws IOException {
        SystemConnect connect = getConnectCom(companyCode);

        JSONObject data = new JSONObject();
        data.put("supplierName", bpName);
        data.put("pageSize", "99999");
        logger.info("请求的参数为：{}", data.toJSONString());


        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, data.toString());
        Request request = new Request.Builder()
                .url(connect.getApiUrl() + "/web/bk/supplier/list")
                .method("POST", body)
                .addHeader("token", getToken(connect))
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        String bo = response.body().string();
        logger.info("接收到的数据为：{}", bo);
        JSONObject res = JSONObject.parseObject(bo);

        JSONArray list = res.getJSONObject("data").getJSONArray("list");
        return list;

    }

    public String soBill(SoInvoiceModel model) throws IOException {
        SystemConnect connect = getConnect(model.getCompany_code());
        model.setCreator(Integer.valueOf(connect.getBkCreator()));
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/json");

        String url = connect.getApiUrl() + "/invoice-statement/api/v1/ar";
        logger.info("请求的地址为：" + url);

        String param = JSONObject.toJSONString(model,
                JSONWriter.Feature.WriteNullStringAsEmpty);
        param = handleParam(param);
        logger.info("请求的参数为：" + param);
        RequestBody body = RequestBody.create(mediaType, param);
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Authorization", getToken(connect))
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        String msg = response.body().string();
        logger.info("接收到的数据为：" + msg);
        JSONObject obj = JSONObject.parseObject(msg);
        if (!"200".equals(obj.getString("statusCode"))) {
            logger.info("So开票请求BookKeeping异常,原因msg:" + obj.getString("msg"));
            throw new RuntimeException("So开票请求BookKeeping异常,原因msg:" + obj.getString("message"));
        } else {
            return obj.getJSONObject("data").getString("invoice_no");
        }
    }

    public String poBill(PoInvoiceModel model) throws IOException {
        SystemConnect connect = getConnect(model.getCompany_code());
        model.setCreator(connect.getBkCreator());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/json");

        String url = connect.getApiUrl() + "/invoice-statement/api/v1/ap";
        logger.info("请求的地址为：" + url);

        String param = JSONObject.toJSONString(model,
                JSONWriter.Feature.WriteNullStringAsEmpty);
        param = handleParam(param);
        logger.info("请求的参数为：" + param);
        RequestBody body = RequestBody.create(mediaType, param);
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Authorization", getToken(connect))
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        String msg = response.body().string();
        logger.info("接收到的数据为：" + msg);
        JSONObject obj = JSONObject.parseObject(msg);
        if (!"200".equals(obj.getString("statusCode"))) {
            logger.info("Po开票请求BookKeeping异常,原因msg:" + obj.getString("message"));
            throw new RuntimeException("Po开票请求BookKeeping异常,原因msg:" + obj.getString("message"));
        } else {
            return obj.getJSONObject("data").getString("invoice_no");
        }
    }

    private String handleParam(String param) {
//        String s = param.substring(0, param.length() - 1);
//        StringBuilder builder = new StringBuilder(s)
//          .append(",\"exchangeRate\":null")
//          .append("}");

        return param;
    }

    public String postBkGL(JSONObject requestBody) throws IOException {
        logger.info(">>>>>postBkGL,requestBody:{}", requestBody);
        SystemConnect connect = getConnect(requestBody.getString("company_code"));
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        String param = handleBkGlParam(requestBody.toString());
        logger.info("远程调用BkGL请求参数:[{}]", param);
        RequestBody body = RequestBody.create(mediaType, requestBody.toString());
        Request request = new Request.Builder()
//                .url(connect.getApiUrl() + "/web/bk/gl/post")
                .url(connect.getApiUrl() + "/bkp-engine/bk/post-journal-entry")
                .method("POST", body)
                .addHeader("Authorization", getToken(connect))
                .addHeader("Content-Type", "application/json")
                .build();
        Response response;
        response = client.newCall(request).execute();
        logger.info("远程调用BkGL结果:[{}]", response);

        String msg = response.body().string();
        logger.info("接收到的数据为：" + msg);
        JSONObject obj = JSONObject.parseObject(msg);
//        if ("2000".equals(obj.getString("data"))) {
        if (StringUtils.isEmpty(obj.getJSONObject("data").getString("document_no"))) {
            logger.info("物料创建同步Bookkeeping错误");
            throw new RuntimeException("物料创建同步Bookkeeping错误");
        } else {
            return obj.getJSONObject("data").getString("document_no");
        }
    }

    /**
     * 保存bp到bk
     *
     * @param bp
     * @throws IOException
     */
    public JSONObject saveBp(BusinessPartner bp, JSONObject op) throws IOException {
        SystemConnect connect = getConnectCom(bp.getCompanyCode());
        JSONObject requestData = getBpData(bp, op);
        String param = requestData.toJSONString(JSONWriter.Feature.WriteNullStringAsEmpty);
        param = param.replaceAll("\"_NULL_\"", "null");
        logger.info("保存bp到bk 请求的参数为：{}", param);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,
                param);

        Request request = new Request.Builder()
                .url(connect.getApiUrl() + "/system-preferences/api/v1/contact")
                .method("POST", body)
                .addHeader("Authorization", getToken(connect))
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();

        String msg = response.body().string();
        logger.info("接收到的数据为：" + msg);
        JSONObject obj = JSONObject.parseObject(msg);
//        if ("2000".equals(obj.getString("code"))) {
        if (StringUtils.isEmpty(obj.getString("contact_id"))) {
            throw new RuntimeException("请求BookKeeping异常");
        } else {
            String cusId = obj.getString("contact_id");
            JSONObject robj = new JSONObject();
            robj.put("contact_id", cusId);
            return robj;
        }
    }


    /**
     * @description: 同步bk v2 参数封装    -- otherParam在v2中已经不需要  但为了不改动太大 所以接口参数列表上保留
     */
    private JSONObject getBpData(BusinessPartner bp, JSONObject otherParam) {
        //根据companyCode查询company表得到orgid_ex和company_code_ex
        Company company = companyService.getCompany(bp.getCompanyCode());
        JSONObject data = new JSONObject();
        data.put("id", 0);
        data.put("company_id", company.getOrgidEx());
        data.put("company_code", company.getCompanyCodeEx());
        data.put("contact_id", "");
        data.put("contact_name", bp.getBpName());
        data.put("gl_account", "");

        List<AddressVO> addresses = bp.getOfficeList();
        AddressVO address = null;
        if (addresses == null || addresses.isEmpty()) {
            address = new AddressVO();
        } else {
            address = addresses.get(0);
        }
        data.put("office_receiver", bp.getBpContact() == null ? "" : bp.getBpContact());
        data.put("office_street", address.getStreet() == null ? "" : address.getStreet());
        data.put("office_city", address.getCity() == null ? "" : address.getCity());
        data.put("office_province", address.getProvince() == null ? "" : address.getProvince());
        data.put("office_country", address.getCountry() == null ? "" : address.getCountry());
        data.put("office_postal_code", address.getPostCode() == null ? "" : address.getPostCode());

        addresses = bp.getBilltoList();
        if (addresses == null || addresses.isEmpty()) {
            address = new AddressVO();
        } else {
            address = addresses.get(0);
        }
        data.put("billing_receiver", "");
        data.put("billing_street", address.getStreet() == null ? "" : address.getStreet());
        data.put("billing_city", address.getCity() == null ? "" : address.getCity());
        data.put("billing_province", address.getProvince() == null ? "" : address.getProvince());
        data.put("billing_country", address.getCountry() == null ? "" : address.getCountry());
        data.put("billing_postal_code", address.getPostCode() == null ? "" : address.getPostCode());
        data.put("billing_status", "0");

        address = null;
        addresses = bp.getShiptoList();
        if (addresses == null || addresses.isEmpty()) {
            address = new AddressVO();
        } else {
            for (AddressVO av : addresses) {
                if (av.getIsDefault() == 1) {
                    address = av;
                }
            }
            if (address == null) {
                address = addresses.get(0);
            }
        }
        data.put("shipping_receiver", "");
        data.put("shipping_street", address.getStreet() == null ? "" : address.getStreet());
        data.put("shipping_city", address.getCity() == null ? "" : address.getCity());
        data.put("shipping_province", address.getProvince() == null ? "" : address.getProvince());
        data.put("shipping_country", address.getCountry() == null ? "" : address.getCountry());
        data.put("shipping_postal_code", address.getPostCode() == null ? "" : address.getPostCode());
        data.put("shipping_status", "0");
        data.put("tel", bp.getBpTel());
        data.put("email", bp.getBpEmail());
        return data;
    }


//     同步bk v1版本 参数注销
//    private JSONObject getBpData(BusinessPartner bp, JSONObject otherParam) {
//        JSONObject data = new JSONObject();
//
//        JSONObject customer = new JSONObject();
//        data.put("customer", customer);
//        JSONObject supplier = new JSONObject();
//        data.put("supplier", supplier);
//
//        customer.put("clientType", "CUSTOMER");
//        customer.put("customerName", bp.getBpName());
//
//        supplier.put("clientType", "SUPPLIER");
//        supplier.put("supplierName", bp.getBpName());
//
//        List<AddressVO> addresses = bp.getOfficeList();
//        AddressVO address = null;
//        if (addresses == null || addresses.isEmpty()) {
//            address = new AddressVO();
//        } else {
//            address = addresses.get(0);
//        }
//        customer.put("officeReceiver", address.getStreet() == null ? "" : address.getStreet());
//        customer.put("officeStreet", address.getStreet() == null ? "" : address.getStreet());
//        customer.put("officeCity", address.getCity() == null ? "" : address.getCity());
//        customer.put("officeProvince", address.getProvince() == null ? "" : address.getProvince());
//        customer.put("officeCountry", address.getCountry() == null ? "" : address.getCountry());
//        customer.put("officePostalCode", address.getPostCode() == null ? "" : address.getPostCode());
//
//        supplier.put("officeReceiver", address.getStreet() == null ? "" : address.getStreet());
//        supplier.put("officeStreet", address.getStreet() == null ? "" : address.getStreet());
//        supplier.put("officeCity", address.getCity() == null ? "" : address.getCity());
//        supplier.put("officeProvince", address.getProvince() == null ? "" : address.getProvince());
//        supplier.put("officeCountry", address.getCountry() == null ? "" : address.getCountry());
//        supplier.put("officePostalCode", address.getPostCode() == null ? "" : address.getPostCode());
//
//        addresses = bp.getBilltoList();
//        if (addresses == null || addresses.isEmpty()) {
//            address = new AddressVO();
//        } else {
//            address = addresses.get(0);
//        }
//        customer.put("billingReceiver", address.getStreet() == null ? "" : address.getStreet());
//        customer.put("billingStreet", address.getStreet() == null ? "" : address.getStreet());
//        customer.put("billingCity", address.getCity() == null ? "" : address.getCity());
//        customer.put("billingProvince", address.getProvince() == null ? "" : address.getProvince());
//        customer.put("billingCountry", address.getCountry() == null ? "" : address.getCountry());
//        customer.put("billingPostalCode", address.getPostCode() == null ? "" : address.getPostCode());
//
//        supplier.put("billingReceiver", address.getStreet() == null ? "" : address.getStreet());
//        supplier.put("billingStreet", address.getStreet() == null ? "" : address.getStreet());
//        supplier.put("billingCity", address.getCity() == null ? "" : address.getCity());
//        supplier.put("billingProvince", address.getProvince() == null ? "" : address.getProvince());
//        supplier.put("billingCountry", address.getCountry() == null ? "" : address.getCountry());
//        supplier.put("billingPostalCode", address.getPostCode() == null ? "" : address.getPostCode());
//
//        address = null;
//        addresses = bp.getShiptoList();
//        if (addresses == null || addresses.isEmpty()) {
//            address = new AddressVO();
//        } else {
//            for (AddressVO av : addresses) {
//                if (av.getIsDefault() == 1) {
//                    address = av;
//                }
//            }
//            if (address == null) {
//                address = addresses.get(0);
//            }
//        }
//
//        customer.put("shippingReceiver", address.getStreet() == null ? "" : address.getStreet());
//        customer.put("shippingStreet", address.getStreet() == null ? "" : address.getStreet());
//        customer.put("shippingCity", address.getCity() == null ? "" : address.getCity());
//        customer.put("shippingProvince", address.getProvince() == null ? "" : address.getProvince());
//        customer.put("shippingCountry", address.getCountry() == null ? "" : address.getCountry());
//        customer.put("shippingPostalCode", address.getPostCode() == null ? "" : address.getPostCode());
//
//        supplier.put("shippingReceiver", address.getStreet() == null ? "" : address.getStreet());
//        supplier.put("shippingStreet", address.getStreet() == null ? "" : address.getStreet());
//        supplier.put("shippingCity", address.getCity() == null ? "" : address.getCity());
//        supplier.put("shippingProvince", address.getProvince() == null ? "" : address.getProvince());
//        supplier.put("shippingCountry", address.getCountry() == null ? "" : address.getCountry());
//        supplier.put("shippingPostalCode", address.getPostCode() == null ? "" : address.getPostCode());
//
//        customer.put("tel", bp.getBpTel());
//        customer.put("email", bp.getBpEmail());
//        customer.put("expenseAccount", otherParam.getInteger("customerAccount"));
//        customer.put("bankId", 15);
//
//        supplier.put("tel", bp.getBpTel());
//        supplier.put("email", bp.getBpEmail());
////        supplier.put("expenseAccount", "_NULL_");
//
//
//        supplier.put("expenseAccount", otherParam.getInteger("supplierAccount"));
//        supplier.put("bankId", 15);
//
//        return data;
//    }

    private String handleBkGlParam(String param) {
        String s = param.substring(0, param.length() - 1);
        StringBuilder builder = new StringBuilder(s)
                .append(",\"exchangeRate\":null")
                .append(",\"totalDebitExchangeCAD\":null")
                .append(",\"totalCreditExchangeCAD\":null")
                .append("}");

        return builder.toString();
    }


}
