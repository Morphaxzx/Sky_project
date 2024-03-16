package com.sky.service;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    PageResult page(OrdersPageQueryDTO ordersPageQueryDTO);


    OrderVO GetOrderVObyOrderId(long id);

    void cancel(long id);

    void repetition(long id);
}
