// 使用rabbitmq处理超时订单和配送中的订单

// package com.sky.task;

// import com.sky.entity.Orders;
// import com.sky.mapper.OrderMapper;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;

// import java.time.LocalDateTime;
// import java.util.List;

// @Component
// @Slf4j
// public class OrderTask {

//     @Autowired
//     private OrderMapper orderMapper;
//      /**
//       * 处理超时订单
//       */

//      @Scheduled(cron = "0 * * * * ?")
//     public void handleTimeoutOrders(){
//         log.info("处理超时订单:{}", LocalDateTime.now());
//         LocalDateTime now = LocalDateTime.now().plusMinutes(-15);
//         List<Orders> orders = orderMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT, now);
//         if(orders != null && orders.size() > 0){
//             orders.forEach(order -> {
//                 log.info("处理超时订单:{}", order);
//                 order.setStatus(Orders.CANCELLED);
//                 order.setCancelReason("订单超时，自动取消");
//                 order.setCancelTime(LocalDateTime.now());
//                 orderMapper.update(order);
//             });
//         }
//     }
//      /**
//       * 处理配送中的订单
//       */

//     @Scheduled(cron = "0 0 1 * * ?")
//     public void handleDeliverdOrders(){
//         log.info("处理配送中的订单:{}", LocalDateTime.now());
//         LocalDateTime now = LocalDateTime.now().plusMinutes(-60);
//         List<Orders> orders = orderMapper.getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, now);
//         if(orders != null && orders.size() > 0){
//             orders.forEach(order -> {
//                 log.info("处理配送中的订单:{}", order);
//                 order.setStatus(Orders.COMPLETED);
//                 orderMapper.update(order);
//             });
//         }
//     }

// }
