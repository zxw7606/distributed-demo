package org.order.service;

import java.io.IOException;
import java.util.Map;

import org.order.mapper.OrderMapper;
import org.order.vo.Order;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.amqp.support.AmqpHeaders;

import com.alibaba.dubbo.config.annotation.Service;
import com.rabbitmq.client.Channel;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description：暴露服务
 * @author 0
 * @date 2019年8月15日 下午6:53:40
 */
@Service(timeout = 20000)
@Slf4j
public class DefaultOrderSerivce implements OrderService {

	@Autowired
	private OrderMapper orderMapper;

	@Override
	public int insert(Order record) {
		return insertSelective(record);
	}

	@Override
	public int insertSelective(Order record) {
		log.info("创建订单: ", record.toString());
		orderMapper.insertSelective(record);
		return 0;
	}

	@RabbitListener(queues = "com.zxw.publish.order")
//	@Transactional
	public void orderpublishSuccessListener(@Payload Map<String, String> map,
			@Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
		log.info("接收到完成支付订单");
		String out_trade_no = map.get("out_trade_no");// 商家订单
		String trade_no = map.get("out_trade_no");// 支付宝交易号
		String status = map.get("status");// 商家订单

		Order order = orderMapper.selectByPrimaryKey(out_trade_no);
		order.setStatus(status);
		orderMapper.updateByPrimaryKeySelective(order);
		channel.basicAck(deliveryTag, true);
		if (StringUtils.hasText(order.getStatus()) && order.getStatus().equals("已支付")) {
			channel.basicAck(deliveryTag, true);
		}
		order.setStatus(status);
		orderMapper.updateByPrimaryKeySelective(order);
		channel.basicAck(deliveryTag, true);
	}

}
