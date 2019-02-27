package com.chongyu.rabbitmq.topic;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * Topic模式
 * <p>
 * 声明交换机为topic实现模式匹配
 * #匹配一个或多个, *匹配一个
 *
 * @author chongyu
 */
public class ReceiveLogsTopic {
    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.140");
        factory.setUsername("root");
        factory.setPassword("root");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME,BuiltinExchangeType.TOPIC);
        String queueName = channel.queueDeclare().getQueue();

        if(args.length<1){
            System.err.println("Usage: ReceiveLogsDirect [info] [warning] [error]");
            System.exit(1);
        }

        for(String severity:args){
            channel.queueBind(queueName,EXCHANGE_NAME,severity);
        }

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag,Envelope envelope,AMQP.BasicProperties properties,byte[] body) throws IOException {
                String message = new String(body,"UTF-8");
                System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
            }
        };
        channel.basicConsume(queueName,true,consumer);
    }
}
