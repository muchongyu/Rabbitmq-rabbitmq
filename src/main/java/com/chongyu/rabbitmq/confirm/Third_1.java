package com.chongyu.rabbitmq.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;

public class Third_1 {
    private static final String QUEUE_NAME= "test_queue_confirm";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.140");
        factory.setUsername("root");
        factory.setPassword("root");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        //开启confirm模式
        channel.confirmSelect();

        //未确认的消息放入该集合（有序集合SortedSet）
        SortedSet<Long> confirmSet = Collections.synchronizedSortedSet(new TreeSet<Long>());

        //添加监听通道
        channel.addConfirmListener(new ConfirmListener() {
            //没有问题的handleAck 成功
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                if(multiple){//多个的
                    System.out.println("handleAck:multiple");
                    confirmSet.headSet(deliveryTag+1).clear();
                }else {//单个的
                    System.out.println("handleAck:multiple false");
                    confirmSet.remove(deliveryTag);
                }
            }
            //有问题的反馈Nack 失败
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                if(multiple){
                    System.out.println("handleNack:multiple");
                    confirmSet.headSet(deliveryTag+1).clear();
                }else {
                    System.out.println("handleNack:multiple false");
                    confirmSet.remove(deliveryTag);
                }
            }
        });

        String msg = "hello confirm ok";
        while(true){
            long seqNo = channel.getNextPublishSeqNo();
            channel.basicPublish("",QUEUE_NAME,null,msg.getBytes("UTF-8"));
            confirmSet.add(seqNo);
        }
    }
}
