package org.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;

@PropertySource("classpath:spring.properties")
@Configuration
@EnableDubbo
public class DubboConfig {

	@Bean
	public ApplicationConfig applicationConfig(@Value("${spring.application.name}") String name) {
		ApplicationConfig applicationConfig = new ApplicationConfig();
		applicationConfig.setName(name);
		// applicationConfig.setRegistry(registry);
		return applicationConfig;
	}

	@Bean
	public RegistryConfig registryConfig(@Value("${zookeeper.address}") String address) {
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setProtocol("zookeeper");
		registryConfig.setAddress(address);
		return registryConfig;
	}

	@Bean
	public ProtocolConfig protocolConfig() {
		ProtocolConfig protocolConfig = new ProtocolConfig();
		// protocolConfig.setHost("192.168.213.128");
		// protocolConfig.setPort(20880);
		protocolConfig.setName("dubbo");
		return protocolConfig;
	}

}
