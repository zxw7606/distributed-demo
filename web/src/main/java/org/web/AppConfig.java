package org.web;

import java.io.IOException;
import javax.sql.DataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * @description: RootApplicationContext配置
 * @create: 2019-08-12 12:29
 * @author: zxw
 **/
@Configuration
@MapperScan("org.web.mapper")
// @ComponentScan(value = "org.order.mapper", excludeFilters = {
// @ComponentScan.Filter(type = FilterType.ANNOTATION, value =
// Configuration.class) })
@EnableTransactionManagement
@PropertySource("classpath:spring.properties")
public class AppConfig {

	@Bean
	public DataSource dataSource(@Value("${datasource.username}") String username,
			@Value("${datasource.password}") String password, @Value("${datasource.driver}") String driver,
			@Value("${datasource.url}") String url) {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setDriverClassName(driver);
		return dataSource;
	}

	@Bean
	public SqlSessionFactoryBean SqlSessionFactory(DataSource dataSource) throws IOException {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(dataSource);

		PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
		Resource[] mapperLocations = pathMatchingResourcePatternResolver
				.getResources("classpath*:org/web/mapper/xml/*.xml");
		factoryBean.setMapperLocations(mapperLocations);
		factoryBean.setTypeAliasesPackage("org.web.vo");
		return factoryBean;
	}

	/**
	 * @Title：dataSourceTransactionManager 。
	 * 
	 * @Description：数据库事务。 @param ：@param dataSource 。
	 */
	@Bean
	public PlatformTransactionManager dataSourceTransactionManager(DataSource dataSource) {
		DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
		return dataSourceTransactionManager;

	}

	/**
	 * 
	 * @Title：rabbitTransactionManager
	 * @Description：mq事务
	 */
	@Bean
	public PlatformTransactionManager rabbitTransactionManager(ConnectionFactory connectionFactory) {
		RabbitTransactionManager rabbitTransactionManager = new RabbitTransactionManager();
		rabbitTransactionManager.setConnectionFactory(connectionFactory);
		return rabbitTransactionManager;
	}

	/**
	 * @param ：@return bean
	 * @return ：CachingConnectionFactory
	 * @Title：connectionFactory
	 * @Description：配置连接工厂
	 */
	@Bean
	public CachingConnectionFactory connectionFactory(@Value("${host}") String HOST,
			@Value("${username}") String USER_NAME, @Value("${password}") String PASSWORD,
			@Value("${virtualhost}") String VIRTUAL_HOST) {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setHost(HOST);
		// connectionFactory.setPort(PORT);
		connectionFactory.setVirtualHost(VIRTUAL_HOST);
		connectionFactory.setUsername("root");
		connectionFactory.setPassword("root");

		connectionFactory.setPublisherReturns(true);
		return connectionFactory;

	}

	@Bean
	public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
		return new RabbitAdmin(connectionFactory);
	}

	@Bean
	public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate();
		rabbitTemplate.setConnectionFactory(connectionFactory);
		// 默认3次重试
		rabbitTemplate.setRetryTemplate(new RetryTemplate());
		return rabbitTemplate;
	}

	@Bean
	public Exchange exchangeEx1(@Value("${exchanges.1.name}") String name, @Value("${exchanges.1.type}") String type) {
		return new DirectExchange(name, true, false, null);
		// return new ExchangeBuilder(name, type).durable(false).build();
	}

	@Autowired
	private Exchange exchangeEx1;

	@Bean
	public Exchange exchangeEx2(@Value("${exchanges.2.name}") String name, @Value("${exchanges.2.type}") String type) {
		return new FanoutExchange(name, true, false, null);
		// return new ExchangeBuilder(name, type).durable(false).build();
	}

	@Autowired
	private Exchange exchangeEx2;

	@Bean
	public Queue queueQue1(@Value("${queues.1.name}") String name) {
		return new Queue(name, true, false, false, null);

	}

	@Autowired
	private Queue queueQue1;

	@Bean
	public Queue queueQue2(@Value("${queues.2.name}") String name) {
		return new Queue(name, true, false, false, null);
	}

	@Autowired
	private Queue queueQue2;

	@Bean
	public Binding ex1ToQue1() {
		return new Binding(queueQue1.getName(), DestinationType.QUEUE, exchangeEx1.getName(), "com.zxw.publish.order",
				null);
	}

	@Bean
	public Binding ex1ToEx2() {
		return new Binding(exchangeEx2.getName(), DestinationType.EXCHANGE, exchangeEx1.getName(), "", null);
	}

	@Bean
	public Binding ex2ToQue2() {
		return new Binding(queueQue2.getName(), DestinationType.QUEUE, exchangeEx2.getName(), "com.zxw.fanout", null);
	}
}
