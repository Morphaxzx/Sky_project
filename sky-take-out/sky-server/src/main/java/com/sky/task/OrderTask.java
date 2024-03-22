package com.sky.task;

import com.sky.constant.MessageConstant;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j

public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron = "0 * * * * ?")
//    @Scheduled(cron = "0/5 * * * * ?")
    public void ProcessTimeoutOrder()
    {
        log.info("超时未支付订单处理，time:{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> ordersList = orderMapper.QueryOrdersByStatusandTime(Orders.PENDING_PAYMENT,time);

        if(ordersList!=null && !ordersList.isEmpty())
        for (Orders orders : ordersList) {
            orders.setStatus(Orders.CANCELLED);
            orders.setCancelTime(LocalDateTime.now());
            orders.setCancelReason("订单超时，自动取消");
            orderMapper.update(orders);
        }
    }


    @Scheduled(cron = "0 0 1 * * ?")
//    @Scheduled(cron = "1/5 * * * * ?")
    public void ProcessDeliveryOrder()
    {
        log.info("过久派送中订单处理，time:{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> ordersList = orderMapper.QueryOrdersByStatusandTime(Orders.DELIVERY_IN_PROGRESS,time);

        if(ordersList!=null && !ordersList.isEmpty())
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
    }
}