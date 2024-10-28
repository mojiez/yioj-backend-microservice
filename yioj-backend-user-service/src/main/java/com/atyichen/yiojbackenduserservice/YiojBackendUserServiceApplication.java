package com.atyichen.yiojbackenduserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@MapperScan("com.atyichen.yiojbackenduserservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.atyichen") // 如果不加 扫不到 common model下的bean
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.atyichen.yiojbackendserviceclient.service"})
@EnableRedisHttpSession
public class YiojBackendUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(YiojBackendUserServiceApplication.class, args);
	}

}
