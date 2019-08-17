package org.order.rmq;

import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AMQPConnectionListener implements ConnectionListener {

	@Override
	public void onCreate(Connection connection) {
		log.info("Thread {} create AMQP connection..", Thread.currentThread().getName());

	}

	@Override
	public void onClose(Connection connection) {
		log.info("Thread {} create AMQP closed..", Thread.currentThread().getName());
	}

}
