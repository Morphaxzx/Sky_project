package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.WebSocket.WebSocketServer;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import com.sky.service.OrderService;
import com.sky.vo.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.util.Json;
import org.apache.commons.collections4.Get;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {


    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private WebSocketServer webSocketServer;


    @Transactional
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        //**处理业务异常情况**（地址簿或者购物车为空）

        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook==null)
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        List<ShoppingCart> lists = shoppingCartMapper.queryByUserId(BaseContext.getCurrentId());
        if (lists==null || lists.isEmpty())
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);

        //赋值给order，补全所有的变量，存到数据库中
        Orders order=new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,order);
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setPayStatus(Orders.UN_PAID);
        order.setUserId(BaseContext.getCurrentId());
        order.setOrderTime(LocalDateTime.now());

        order.setPhone(addressBook.getPhone());
        order.setAddress(addressBook.getDetail());
        order.setConsignee(addressBook.getConsignee());

        orderMapper.insert(order);

        Long id = order.getId();

        //order_detail也要补全
        List<OrderDetail> details=new ArrayList<>();
        for (ShoppingCart list : lists) {
            OrderDetail od = new OrderDetail();
            od.setOrderId(id);
            BeanUtils.copyProperties(list,od);
            details.add(od);
        }
        orderDetailMapper.insertBatch(details);

        //**清空用户购物车**
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());


        //返回OrderSubmitVO
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder().orderAmount(order.getAmount())
                .orderNumber(order.getNumber())
                .orderTime(order.getOrderTime())
                .build();
        return orderSubmitVO;

    }

    @Override
    public PageResult page(OrdersPageQueryDTO ordersPageQueryDTO) {

        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        //这里返回的不是Orders,而是OrderVO,因为orderdetails也要返回
        List<OrderVO> list= new ArrayList<>();
        if(page!=null &&page.getTotal()>0)
        {
            for (Orders orders : page) {
                OrderVO orderVO =new OrderVO();
                BeanUtils.copyProperties(orders,orderVO);
                List<OrderDetail> details = orderDetailMapper.queryByOrderId(orders.getId());
                orderVO.setOrderDetailList(details);
                list.add(orderVO);
            }

        }

        return new PageResult(page.getTotal(),list);
    }


    @Override
    public OrderVO GetOrderVObyOrderId(long id) {

        List<OrderDetail> details = orderDetailMapper.queryByOrderId(id);

        Orders Fullorder =orderMapper.queryByid(id);

        OrderVO re = new OrderVO();
        BeanUtils.copyProperties(Fullorder,re);
        re.setOrderDetailList(details);

        return re;

    }

    @Override
    public void cancel(long id) {
        Orders my_order = orderMapper.queryByid(id);

        if (my_order==null)
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        Integer status = my_order.getStatus();
        if (status>2)
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        if(status==Orders.TO_BE_CONFIRMED)
        {
            //退款操作
        }
        my_order.setStatus(Orders.CANCELLED);
        my_order.setCancelReason("用户取消");
        my_order.setCancelTime(LocalDateTime.now());
        orderMapper.update(my_order);
    }

    @Override
    public void repetition(long id) {
        Orders orders = orderMapper.queryByid(id);
        List<OrderDetail> details = orderDetailMapper.queryByOrderId(orders.getId());
        List<ShoppingCart> shoppingCarts =new ArrayList<>();
        for (OrderDetail detail : details) {
            ShoppingCart temp = new ShoppingCart();
            BeanUtils.copyProperties(detail,temp);
            temp.setUserId(BaseContext.getCurrentId());
            temp.setCreateTime(LocalDateTime.now());
            shoppingCarts.add(temp);

        }
        shoppingCartMapper.insertBatch(shoppingCarts);

    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        Page<Orders> ordersPage = orderMapper.pageQuery(ordersPageQueryDTO);
        List<OrderVO> list = GetOrderVOList(ordersPage);
        return new PageResult(ordersPage.getTotal(),list);
    }

    private List<OrderVO> GetOrderVOList(Page<Orders> ordersPage)
    {
        List<OrderVO> list= new ArrayList<>();
        for (Orders orders : ordersPage) {
            OrderVO orderVO = new OrderVO();
            List<OrderDetail> details = orderDetailMapper.queryByOrderId(orders.getId());
            BeanUtils.copyProperties(orders,orderVO);
            orderVO.setOrderDetailList(details);
            orderVO.setOrderDishes(GetOrderDishesStr(orders));
            list.add(orderVO);
        }
        return list;
    }
    private String GetOrderDishesStr(Orders orders)
    {
        List<OrderDetail> details = orderDetailMapper.queryByOrderId(orders.getId());
        List<String> OrderDishesList =  details.stream().map(x->{
            String orderDish = x.getName() + "*" + x.getNumber()+";";
            return orderDish;
        }).collect(Collectors.toList());

        return String.join("",OrderDishesList);
    }


    @Override
    public OrderStatisticsVO statistics() {
        Integer TobeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer Confirmed = orderMapper.countStatus(Orders.CONFIRMED);
        Integer DeiveryinProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);

        OrderStatisticsVO re = OrderStatisticsVO.builder().toBeConfirmed(TobeConfirmed)
                .confirmed(Confirmed).deliveryInProgress(DeiveryinProgress)
                .build();
        return re;


    }

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();

        orderMapper.update(orders);
    }

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        Orders order = orderMapper.queryByid(ordersRejectionDTO.getId());
        if(order==null || !order.getStatus().equals(Orders.TO_BE_CONFIRMED))
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);

        if(order.getPayStatus()==Orders.PAID)
        {
            //退款
        }

        order.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        order.setCancelTime(LocalDateTime.now());
        order.setStatus(Orders.CANCELLED);
        orderMapper.update(order);
    }

    @Override
    public void adminCancel(OrdersCancelDTO ordersCancelDTO) {
        Orders order = orderMapper.queryByid(ordersCancelDTO.getId());
        if(order.getPayStatus()==Orders.PAID)
        {
            //退款
        }

        order.setCancelReason(ordersCancelDTO.getCancelReason());
        order.setCancelTime(LocalDateTime.now());
        order.setStatus(Orders.CANCELLED);
        orderMapper.update(order);
    }


    @Override
    public void delivery(Long id) {
        Orders order = orderMapper.queryByid(id);
        if(order==null || !order.getStatus().equals(Orders.CONFIRMED))
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);

        order.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(order);
    }

    @Override
    public void complete(Long id) {
        Orders order = orderMapper.queryByid(id);
        if(order==null || !order.getStatus().equals(Orders.DELIVERY_IN_PROGRESS))
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);

        order.setStatus(Orders.COMPLETED);
        order.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(order);
    }


    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        Orders orders = orderMapper.queryByNumberAndUserId(ordersPaymentDTO.getOrderNumber(),BaseContext.getCurrentId());
        orders.setStatus(Orders.TO_BE_CONFIRMED);
        orderMapper.update(orders);

        //通过websocket向客户端发送推送消息
        Map map =new HashMap<>();
        map.put("type",1);
        map.put("orderId",orders.getId());
        map.put("content","订单号："+ordersPaymentDTO.getOrderNumber());

        String jsonString = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);

        return new OrderPaymentVO();
    }

    @Override
    public void reminder(long id) {
        Orders order = orderMapper.queryByid(id);
        if(order==null )
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);



        //通过websocket向客户端发送推送消息
        Map map =new HashMap<>();
        map.put("type","1");
        map.put("orderId",order.getId());
        map.put("content","订单号："+order.getNumber());


        String jsonString = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);
    }
}
