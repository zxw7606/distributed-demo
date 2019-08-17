package org.order;



import com.fasterxml.jackson.databind.ObjectMapper;

import org.order.json.DateJsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.datetime.DateTimeFormatAnnotationFormatterFactory;
import org.springframework.format.number.money.MonetaryAmountFormatter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @description: WebApplicationContext 配置
 * @create: 2019-08-12 12:20
 * @author: zxw
 **/

@EnableWebMvc
@ComponentScan
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

	private final static Logger logger = LoggerFactory.getLogger(MvcConfig.class);

	@Bean
	public LocaleResolver localeResolver() {
		AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
		localeResolver.setDefaultLocale(Locale.getDefault());
		return localeResolver;
	}

	/**
	 * @Description: 配置JacksonMessageConvert
	 * @Date: 2019/8/13
	 * @Param:
	 * @return:
	 * @Author: zxw
	 * 
	 */

	@Autowired
	private DateJsonSerializer dateJsonSerializer;

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
				.serializerByType(Date.class, dateJsonSerializer)
//				.serializerByType(MonetaryAmount.class, moneySerializer)
				.build();
		converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
	}

	/***************************************************************************************************/

	/**
	 * @Description: 对输出结果和输入的格式转换 formatter 和 convert 没有区别 formatter 最后也会被转化成
	 *               convert
	 * @Date: 2019/8/12
	 * @Param:
	 * @return:
	 * @Author: zxw
	 */
	@Bean
	public DateFormatter dateFormatter() {
		return new DateFormatter("yyyy-MM-dd HH:mm:ss");
	}

	@Bean
	public MonetaryAmountFormatter monetaryAmountFormatter() {
		return new MonetaryAmountFormatter();
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
//		registry.addFormatter(monetaryAmountFormatter());
		registry.addFormatter(dateFormatter());
		registry.addFormatterForFieldAnnotation(new DateTimeFormatAnnotationFormatterFactory());
	}

	/***************************************************************************************************/

	/**
	 * 配置谈判视图解析器
	 *
	 * @param configurer
	 */
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.defaultContentType(MediaType.APPLICATION_JSON);
		configurer.mediaType("json", MediaType.APPLICATION_JSON);
		// configurer.mediaType("xml",MediaType.APPLICATION_XML);
	}

	/**
	 * jsp配置
	 *
	 * @param registry 注册视图,和视图解析器
	 */
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		MappingJackson2JsonView jackson2JsonView = new MappingJackson2JsonView();
		registry.enableContentNegotiation(jackson2JsonView);
		registry.jsp("/WEB-INF/", ".jsp");

	}

	@Override
	public Validator getValidator() {
		return new LocalValidatorFactoryBean();
	}
}
