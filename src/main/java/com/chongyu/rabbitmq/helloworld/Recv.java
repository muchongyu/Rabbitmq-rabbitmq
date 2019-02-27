package com.chongyu.rabbitmq.helloworld;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * helloworld 模型
 * 简单队列
 * 匿名发送，不指定交换机，直接发送到队列中。
 *
 * @author mcy
 */
public class Recv {
    private final static String QUEUE_NAME = "hello.august";

    public static void main(String[] args) throws IOException, TimeoutException {
        //定义工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.140");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("root");

        //获得连接
        Connection connection = connectionFactory.newConnection();
        //获取通道
        Channel channel = connection.createChannel();
        //队列声明
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        //定义消费者
        Consumer consumer = new DefaultConsumer(channel) {
            @Override //有消息则触发这个方法
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("[x] receiverd '" + message + "'");
            }
        };

        /*
         * 监听队列
         * 参数1:队列名称
         * 参数2：是否发送ack包，不发送ack消息会持续在服务端保存，直到收到ack。  可以通过channel.basicAck手动回复ack
         * 参数3：消费者
         */
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
