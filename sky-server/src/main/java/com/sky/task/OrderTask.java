package com.sky.task;


import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;


    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder(){
        log.info("处理超时订单,{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        //查询超时订单
        List<Orders> timeoutOrders = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT,time);

        //处理超时订单
        if(timeoutOrders != null && !timeoutOrders.isEmpty()){
            for (Orders order : timeoutOrders) {
                order.setStatus(Orders.CANCELLED);
                order.setCancelTime(LocalDateTime.now());
                order.setCancelReason("超时未支付");
                orderMapper.update(order);
            }
        }

    }

    /**
     * 定时处理待配送订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){
        log.info("处理待配送订单,{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);

        //查询待配送订单
        List<Orders> deliveryOrders = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS,time);

        //处理待配送订单
        if(deliveryOrders != null && !deliveryOrders.isEmpty()){
            for (Orders order : deliveryOrders) {
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            }
        }
    }

}
