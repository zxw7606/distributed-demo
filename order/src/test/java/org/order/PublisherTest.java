package org.order;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class PublisherTest {

	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Autowired
	private OrderMapper orderMapper;

	@Test
	@Transactional(transactionManager = "rabbitTransactionManager", rollbackFor = Exception.class)
	public void txTest() {
		rabbitTemplate.setChannelTransacted(true);
		rabbitTemplate.convertAndSend("com.zxw.publish.order", "this is a tx message" + System.currentTimeMillis());
		testDb();
	}

	@Test
	@Transactional(value = "dataSourceTransactionManager", rollbackFor = Exception.class)
	public void testDb() {
		Order order = new Order();
		order.setOrderId(String.valueOf(System.currentTimeMillis()));
		orderMapper.insertSelective(order);
		int i = 1 / 0;
	}

	@Transactional(value = "dataSourceTransactionManager", rollbackFor = Exception.class)
	public Object add() {
		Order order = new Order();
		order.setOrderId(String.valueOf(System.currentTimeMillis()));
		orderMapper.insertSelective(order);
		int i = 1 / 0;
		return order;
	}

	@Test
	public void txReturnTest() {
		System.out.println(add());
	}
}
