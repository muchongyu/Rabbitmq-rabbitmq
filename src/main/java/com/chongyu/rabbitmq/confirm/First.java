package com.chongyu.rabbitmq.confirm;

import com.rabbitmq.client.Channel;
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
 * @author chongyu 本例使用方式一
 */
public class First {
    public static final String QUEQUE_NAME = "first";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.140");
        factory.setUsername("root");
        factory.setPassword("root");
        Connection connection = factory.newConnection();
        // 创建信道
        Channel channel = connection.createChannel();
        // 声明队列
        channel.queueDeclare(QUEQUE_NAME,false,false,false,null);
        // 开启发送方确认模式
        channel.confirmSelect();
        String message = String.format("时间 => %s",new Date().getTime());
        channel.basicPublish("",QUEQUE_NAME,null,message.getBytes("UTF-8"));
        if(channel.waitForConfirms()){
            System.out.println("消息发送成功");
        }

        //关闭通道和连接
        channel.close();
        connection.close();
    }
}
