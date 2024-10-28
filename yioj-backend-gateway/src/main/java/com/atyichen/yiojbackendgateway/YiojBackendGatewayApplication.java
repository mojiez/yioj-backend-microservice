package com.atyichen.yiojbackendgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient // nacos注册中心
public class YiojBackendGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(YiojBackendGatewayApplication.class, args);
	}

}
