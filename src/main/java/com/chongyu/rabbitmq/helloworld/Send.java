package com.chongyu.rabbitmq.helloworld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.concurrent.TimeoutException;

/**
 * helloworld 模型
 * 简单队列
 * 匿名发送，不指定交换机，直接发送到队列中。
 * <p>
 * 1 创建连接工厂 ConnectionFactory
 * 2 配置共创 config
 * 3 获取连接 Connection
 * 4 获取信道 Channel
 * 5 从信道声明 queueDeclare
 * 6 发送消息 basicPublish
 * 7 释放资源
 *
 * @author chongyu
 */
public class Send {

    private final static String QUEQUE_NAME = "hello.august";

    public static void main(String[] args) throws java.io.IOException, TimeoutException {
        //定义工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.140");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("root");

        //从工厂获取连接
        Connection connection = connectionFactory.newConnection();
        //从连接获取信道
        Channel channel = connection.createChannel();
        //利用channel声明第一个队列
        channel.queueDeclare(QUEQUE_NAME, false, false, false, null);
        //发送消息
        /*
         * 向server发布一条消息
         * 参数1：exchange名字，若为空则使用默认的exchange
         * 参数2：routing key
         * 参数3：其他的属性
         * 参数4：消息体
         * RabbitMQ默认有一个exchange，叫default exchange，它用一个空字符串表示，它是direct exchange类型，
         * 任何发往这个exchange的消息都会被路由到routing key的名字对应的队列上，如果没有对应的队列，则消息会被丢弃
         */
        String message = "hello world ++++";
        channel.basicPublish("", QUEQUE_NAME, null, message.getBytes());

        System.out.println("send message: " + message);

        //关闭通道和连接
        channel.close();
        connection.close();
    }

}
