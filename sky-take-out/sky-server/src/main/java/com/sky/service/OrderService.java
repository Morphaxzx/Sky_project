package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    PageResult page(OrdersPageQueryDTO ordersPageQueryDTO);


    OrderVO GetOrderVObyOrderId(long id);

    void cancel(long id);

    void repetition(long id);

    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO statistics();

    /**
     * 管理端取消订单
     * @param ordersCancelDTO
     */
    void adminCancel(OrdersCancelDTO ordersCancelDTO);

    void delivery(Long id);

    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    void complete(Long id);
}
