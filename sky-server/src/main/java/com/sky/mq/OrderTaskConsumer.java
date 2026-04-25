package com.sky.mq;


import com.sky.config.RabbitMQConfiguration;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@Slf4j
public class OrderTaskConsumer { // 订单任务消费者

    @Autowired
    private OrderMapper orderMapper;

    @RabbitListener(queues = RabbitMQConfiguration.ORDER_DEAD_QUEUE) // 死信队列
    public void handleDelayMessage(Map<String, Object> message) { // 处理延迟消息
        try {
            Long orderId = Long.valueOf(message.get("orderId").toString());
            String type = message.get("type").toString();

            log.info("收到订单超时检查消息, orderId: {}, type: {}", orderId, type);

            Orders order = orderMapper.getById(orderId);
            if (order == null) {
                return;
            }

            if ("TIMEOUT_CHECK".equals(type) && order.getStatus() == Orders.PENDING_PAYMENT) {
                log.info("订单{}超时未支付，取消订单", orderId);
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时，自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
            }
        } catch (Exception e) {
            log.error("处理订单超时消息失败: {}", e.getMessage()); // 记录错误日志
            throw e;
        }
    }
}