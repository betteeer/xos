package com.inossem.oms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author czh
 * @description
 * @date 2022/12/7
 */

@SpringBootApplication
@EnableAspectJAutoProxy
@MapperScan("com.inossem.oms.base.**.mapper")
@EnableSwagger2
@EnableRetry
@EnableFeignClients(basePackages = {"com.inossem.oms.api", "com.inossem.sco.system"})
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

}
