package org.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource("classpath:spring.properties")
@Component
public class AlipayConfig {
	@Value("${ali.appid}")
	public String appid;
	@Value("${ali.rsa_priavte_key}")
	public String rsa_priavte_key;
	@Value("${notify_url}")
	public String notify_url;
	@Value("${return_url}")
	public String return_url;
	@Value("${ali.gateway_url}")
	public String gateway_url;
	@Value("${ali.charset}")
	public String charset;
	@Value("${ali.format}")
	public String format;
	@Value("${ali.public_key}")
	public String public_key;
	@Value("${ali.logpath}")
	public String logPath;
	@Value("${ali.sign_type}")
	public String signType;

	
}
