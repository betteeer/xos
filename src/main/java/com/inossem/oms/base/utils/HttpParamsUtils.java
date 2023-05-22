package com.inossem.oms.base.utils;

import com.inossem.sco.common.core.utils.StringUtils;

import java.util.Map;

/**
 * @Title: HttpParamsUtils
 * @Description: <Describe this class>
 * @Author: guoh
 * @Create: 2023/3/31 16:11
 **/
public class HttpParamsUtils {


    /**
     * GET请求 添加参数到url
     *
     * @param paramsMap
     * @return
     */
    public static String getBodyParams(Map<String, Object> paramsMap) {
        if (paramsMap != null && paramsMap.size() > 0) {
            StringBuffer stringBuffer = new StringBuffer();
            for (String key : paramsMap.keySet()) {
                if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(paramsMap.get(key).toString())) {
                    if (stringBuffer.length() == 0) {
                        stringBuffer.append("?");
                    } else {
                        stringBuffer.append("&");
                    }
                    stringBuffer.append(key);
                    stringBuffer.append("=");
                    stringBuffer.append(paramsMap.get(key));
                }
            }
            return stringBuffer.toString();
        } else {
            return "";
        }
    }

}
