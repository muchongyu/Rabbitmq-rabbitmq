package com.chongyu.rabbitmq.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

/**
 * Confirm的三种实现方式：
 * <p>
 * 方式一：channel.waitForConfirms()普通发送方确认模式；
 * <p>
 * 方式二：channel.waitForConfirmsOrDie()批量确认模式；
 * <p>
 * 方式三：channel.addConfirmListener()异步监听发送方确认模式；
 *
 * @author chongyu 本例使用方式三
 */
public class Third {
    public static final String QUEUE_NAME = "third";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.140");
        factory.setUsername("root");
        factory.setPassword("root");
        Connection connection = factory.newConnection();
        // 创建信道
        Channel channel = connection.createChannel();
        // 声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // 开启发送方确认模式
        channel.confirmSelect();

        //异步监听确认和未确认的消息
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println(String.format("已确认消息，标识：%d，多个消息：%b", deliveryTag, multiple));
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("未确认消息，标识：" + deliveryTag);
            }
        });

        for (int i = 0; i < 10; i++) {
            String message = String.format("时间 => %s", new Date().getTime());
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
        }

        //关闭通道和连接
        //channel.close();
        //connection.close();
    }
}
