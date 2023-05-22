package com.inossem.sco.common.core.constant;

public class Constants {
    public static final String UTF8 = "UTF-8";
    public static final String GBK = "GBK";
    public static final String LOOKUP_RMI = "rmi:";
    public static final String LOOKUP_LDAP = "ldap:";
    public static final String LOOKUP_LDAPS = "ldaps:";
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final Integer SUCCESS = 200;
    public static final Integer FAIL = 500;
    public static final String LOGIN_SUCCESS_STATUS = "0";
    public static final String LOGIN_FAIL_STATUS = "1";
    public static final String LOGIN_SUCCESS = "Success";
    public static final String LOGOUT = "Logout";
    public static final String REGISTER = "Register";
    public static final String LOGIN_FAIL = "Error";
    public static final String PAGE_NUM = "pageNum";
    public static final String PAGE_SIZE = "pageSize";
    public static final String ORDER_BY_COLUMN = "orderByColumn";
    public static final String IS_ASC = "isAsc";
    public static final long CAPTCHA_EXPIRATION = 3L;
    public static final String RESOURCE_PREFIX = "/profile";
    public static final String[] JOB_WHITELIST_STR = new String[]{"com.inossem.sco"};
    public static final String[] JOB_ERROR_STR = new String[]{"java.net.URL", "javax.naming.InitialContext", "org.yaml.snakeyaml", "org.springframework", "org.apache", "com.inossem.sco.common.core.utils.file"};

    public Constants() {
    }
}
