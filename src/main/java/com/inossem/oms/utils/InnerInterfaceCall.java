package com.inossem.oms.utils;

public class InnerInterfaceCall {
    public static boolean isInner() {
        return "true".equals(ConfigReader.getConfig("useInnerInterfaceCall"));
    }
}
