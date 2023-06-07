package com.inossem.oms.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ConfigReader {

    private static Environment environment;

    @Autowired
    public void setEnvironment(Environment environment) {
        ConfigReader.environment = environment;
    }
    public static String getConfig(String key) {
        return environment.getProperty(key);
    }
    public static String getActiveProfile() {
        return getConfig("spring.profiles.active");
    }
}
