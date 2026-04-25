package com.sky.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    public static final String ORDER_DELAY_EXCHANGE = "order_delay_exchange"; // 延迟交换机
    public static final String ORDER_DELAY_QUEUE = "order_delay_queue"; // 延迟队列
    public static final String ORDER_DEAD_QUEUE = "order_dead_queue"; // 死信队列
    public static final String ORDER_FAIL_QUEUE = "order_fail_queue"; // 失败队列

    @Bean
    public MessageConverter messageConverter() { // 消息转换器
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) { // RabbitTemplate
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public DirectExchange orderDelayExchange() { // 延迟交换机
        return new DirectExchange(ORDER_DELAY_EXCHANGE);
    }

    @Bean
    public Queue orderDelayQueue() { // 延迟队列
        return QueueBuilder.durable(ORDER_DELAY_QUEUE)
                .deadLetterExchange("") 
                .deadLetterRoutingKey(ORDER_DEAD_QUEUE)
                .build();
    }

    @Bean
    public Queue orderDeadQueue() { // 死信队列
        return QueueBuilder.durable(ORDER_DEAD_QUEUE)
                .deadLetterExchange("") 
                .deadLetterRoutingKey(ORDER_FAIL_QUEUE) // 死信队列绑定
                .build();
    }

    @Bean
    public Queue orderFailQueue() { // 失败队列
        return QueueBuilder.durable(ORDER_FAIL_QUEUE).build(); // 失败队列
    }

    @Bean
    public Binding orderDelayBinding() { // 延迟队列绑定
        return BindingBuilder.bind(orderDelayQueue()) // 延迟队列
                .to(orderDelayExchange()) // 延迟交换机
                .with(ORDER_DELAY_QUEUE); // 延迟队列绑定
    }
}