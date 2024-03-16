package com.sky.controller.user;


import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.DishVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Api(tags = "用户端订单相关接口")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    @ApiOperation("提交订单")
    public Result<OrderSubmitVO> list(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        OrderSubmitVO result = orderService.submit(ordersSubmitDTO);
        return Result.success(result);
    }

    @ApiOperation("历史订单分页查询")
    @GetMapping("/historyOrders")
    public Result<PageResult> page(OrdersPageQueryDTO ordersPageQueryDTO)
    {
        log.info("历史订单分页查询");
        PageResult  re = orderService.page(ordersPageQueryDTO);
        return Result.success(re);
    }


    @GetMapping("/orderDetail/{id}")
    @ApiOperation("根据OrderId查询订单详情")
    public Result<OrderVO> details(@PathVariable long id){
        log.info("根据OrderId查询订单详情,id: {}", id);
        OrderVO  re = orderService.GetOrderVObyOrderId(id);
        return Result.success(re);
    }


    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result cancelOrder(@PathVariable long id)
    {
        log.info("取消订单 id: {}", id);
        orderService.cancel(id);
        return Result.success();
    }



    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable long id)
    {
        log.info("再来一单 id: {}", id);
        orderService.repetition(id);
        return Result.success();
    }
}
