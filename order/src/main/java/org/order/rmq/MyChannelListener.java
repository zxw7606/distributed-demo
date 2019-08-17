package org.order.rmq;

import org.springframework.amqp.rabbit.connection.ChannelListener;

import com.rabbitmq.client.Channel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyChannelListener implements ChannelListener {

	@Override
	public void onCreate(Channel channel, boolean transactional) {
		log.info("Thread {} create AMQP channel..", Thread.currentThread().getName());
	}

}
