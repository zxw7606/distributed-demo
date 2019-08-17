package org.order;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.Confirm.SelectOk;
import com.rabbitmq.client.AMQP.Exchange.DeclareOk;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * @author 0
 * @Description：MQProducerTest
 * @date 2019年8月14日 下午2:00:07
 */

public class MQtests {

    private static final String EXCHANGE_NAME = "cxchange_demo";
    private static final String ROUTING_KEY = "routingkey_demo";
    private static final String QUEUE = "queue_demo";
    private static final String IP_ADDRESS = "192.168.213.128";
    private static final String USER_NAME = "root";
    private static final String PASSWORD = "root";
    private static final int PORT = 5672;

    @Test
    public void producerTest() throws Exception {

        // factory
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(IP_ADDRESS);
        factory.setPort(PORT);
        factory.setUsername(USER_NAME);
        factory.setPassword(PASSWORD);

        // connection
        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        // channel 创建一个持久的 不自动删除 没有额外属性的 direct 类型 exchange

        // fanout 它会把所有发送到该交换器的消息路由到所有与该交换器绑定的队列中。

        // direct 类型的交换器路由规则也很简单，它会把消息路由到那些 BindingKey 和 RoutingKey完全匹配的队列中。

        // topic binding_key： = "*com.zxw.xxxx.xxxx#"

        // headers 类型的交换器不依赖于路由键的匹配规则来路由消息，而是根据发送的消息内容中
        // 的 headers 属性进行匹配
        channel.exchangeDeclare(EXCHANGE_NAME, "direct", true, false, null);

        // 创建一个持久的 非独一的 不会自动删除的 没有二外属性的 队列
        channel.queueDeclare(QUEUE, true, false, false, null);

        // 绑定 通过 queue exchange route_key
        // 这里的ROUTING_KEY 可以说是BINGING_KEY
        channel.queueBind(QUEUE, EXCHANGE_NAME, ROUTING_KEY);
        String message = "Hello World";

        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes());

        System.out.println("publish a message success... ");
        channel.close();
        connection.close();
    }

    @Test
    public void consumerTest() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(USER_NAME);
        factory.setPassword(PASSWORD);

        Address[] addresses = new Address[]{new Address(IP_ADDRESS, PORT)};

        Connection connection = factory.newConnection(addresses);
        Channel channel = connection.createChannel();

        // 设置最多能够接收 64 个未被确认接收的消息
        channel.basicQos(64);

        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
                    throws IOException {
                System.out.println(new String(body));
                // 确认单条消息
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        // 生成一个消费者 绑定一条队列
        channel.basicConsume("com.zxw.publish.order", consumer);
        System.in.read();
    }


    /**
     * @param ：@throws Exception
     * @return ：void
     * @throws
     * @Title：producerAsyncTest
     * @Description：TODO
     */
    @Test
    public void producerAsyncTest() throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(IP_ADDRESS);
        factory.setPort(PORT);
        factory.setUsername(USER_NAME);
        factory.setPassword(PASSWORD);
        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "direct", true, false, null);
        // 创建一个持久的 非独一的 不会自动删除的 没有二外属性的 队列
        channel.queueDeclare(QUEUE, true, false, false, null);
        // 绑定 通过 queue exchange route_key
        // 这里的ROUTING_KEY 可以说是BINGING_KEY
        channel.queueBind(QUEUE, EXCHANGE_NAME, ROUTING_KEY);
        String message = "Hello World";


        SelectOk confirmSelect = channel.confirmSelect();
        channel.addConfirmListener(new ConfirmListener() {

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                // TODO Auto-generated method stub

            }

            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                // TODO Auto-generated method stub

            }
        });


        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes());

        System.out.println("publish a message success... ");
        channel.close();
        connection.close();
    }
}
