package com.chongyu.rabbitmq.routing;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Routing模式
 * 一个生产者P，一个交换机X，多个消息队列Q以及多个消费者C
 * 实现了定向派发，而不再是订阅模式那种广播式的派发。同一条消息既可以派发给一个Queue，也可以同时派发给两个或者多个Queue
 *
 * @author chongyu
 */
public class EmitLogDirect {
    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.140");
        factory.setUsername("root");
        factory.setPassword("root");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明exchange
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        String severity = getSeverity(args);
        String message = getMessage(args);
        //severity为路由key（消息带哪个key，就路由到哪个队列。可以一个队列绑定多个key）
        channel.basicPublish(EXCHANGE_NAME, severity, null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + severity + "':'" + message + "'");

        channel.close();
        connection.close();

    }

    private static String getSeverity(String[] strings) {
        if (strings.length < 1) return "info";
        return strings[0];
    }

    private static String getMessage(String[] strings) {
        if (strings.length < 2) return "hello world";
        return joinStrings(strings, " ", 1);
    }

    private static String joinStrings(String[] strings, String delimiter, int startIndex) {
        int length = strings.length;
        if (length == 0) return "";
        if (length < startIndex) return "";
        StringBuilder words = new StringBuilder(strings[startIndex]);
        for (int i = startIndex + 1; i < length; i++) {
            words.append(delimiter).append(strings[i]);
        }
        return words.toString();
    }
}
