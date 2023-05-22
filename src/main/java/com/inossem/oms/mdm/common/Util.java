package com.inossem.oms.mdm.common;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Util {
    public static final String TYPE_SERVICE_SKU = "SE";
    public static final String TYPE_INVENTORY_SKU = "IN";
    public static final String TYPE_ADDRESS_WH = "WH";
    public static final String SUB_TYPE_ADDRESS_WH = "office";
    public static final String TYPE_ADDRESS_BP = "bp";
    public static final String TYPE_ADDRESS_SO = "so";
    public static final String TYPE_ADDRESS_SODN = "SODN";
    public static final String SUB_TYPE_ADDRESS_COMPANY = "office";
    public static final String TYPE_ADDRESS_COMPANY = "company";
    public static final String SUB_TYPE_ADDRESS_BP_OFFICE = "office";
    public static final String SUB_TYPE_ADDRESS_BP_BILLTO = "billto";
    public static final String SUB_TYPE_ADDRESS_BP_SHIPTO = "shipto";
    public static final String TYPE_PICTURE_SKU = "SKU";
    public static final String TYPE_PICTURE_CARRIER = "carrier";

    public static final String DEPT_INSERT = "DEPT_INSERT";
    public static final String DEPT_UPDATE = "DEPT_UPDATE";
    public static final String DEPT_DELETE = "DEPT_DELETE";

    public static final Map<String, String> SKU_TYPE_MAP = new HashMap<String, String>() {{
        put("IN", "Inventory");
        put("SE", "Service");
    }};

    public static String getPicturesPath() {
        return System.getProperty("user.dir") + File.separator + "oms-mdm"
                + File.separator + "src" + File.separator + "main"
                + File.separator + "resources" + File.separator +
                "pictures" + File.separator;
    }
}
