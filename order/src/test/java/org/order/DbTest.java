package org.order;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.order.mapper.OrderMapper;
import org.order.vo.Order;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactoryUtils;
import org.springframework.amqp.rabbit.connection.RabbitResourceHolder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
// @ContextConfiguration(loader = AnnotationConfigWebContextLoader.class,
// classes = { AppConfig.class})
@ContextConfiguration(classes = AppConfig.class)
public class DbTest {

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private ConnectionFactory connectionFactory;

	@Test
	// @Transactional
	public void testDb() throws Exception {
		Order order = new Order();
		order.setOrderId(String.valueOf(System.currentTimeMillis()));
		orderMapper.insertSelective(order);

	}
}
