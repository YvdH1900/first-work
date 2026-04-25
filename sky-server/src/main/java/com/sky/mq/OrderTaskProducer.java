package com.sky.mq;

/**
 * 订单任务生产者
 */

import com.sky.config.RabbitMQConfiguration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OrderTaskProducer { // 订单任务生产者

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendDelayMessage(Long orderId) { // 发送延迟消息
        // 构建消息内容
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", orderId);
        message.put("type", "TIMEOUT_CHECK");

        rabbitTemplate.convertAndSend( // 发送消息
            RabbitMQConfiguration.ORDER_DELAY_EXCHANGE, // 交换机
            RabbitMQConfiguration.ORDER_DELAY_QUEUE, // 队列
            message, // 消息内容
            msg -> {
                msg.getMessageProperties().setExpiration("900000"); // 过期时间900秒
                return msg;
            }
        );
    }
}