package com.atyichen.yiojbackendjudgeservice;

import com.atyichen.yiojbackendjudgeservice.init.MqInitMain;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.atyichen") // 如果不加 扫不到 common model下的bean
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.atyichen.yiojbackendserviceclient.service"})
public class YiojBackendJudgeServiceApplication {

	public static void main(String[] args) {
		MqInitMain.main(new String[]{"1"});
		SpringApplication.run(YiojBackendJudgeServiceApplication.class, args);
	}
}
