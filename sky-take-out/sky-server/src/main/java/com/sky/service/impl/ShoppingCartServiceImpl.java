package com.sky.service.impl;


import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements com.sky.service.ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        //首先查询购物车里面是否有了该菜品/套餐
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //这里一定是没有元素或者只有一个元素
        //设计成list是为了后续代码，提高代码复用性
        List<ShoppingCart> realShoppingCart  =  shoppingCartMapper.query(shoppingCart);
        //有的话 number++
        if(realShoppingCart!=null && realShoppingCart.size()>0)
        {

            realShoppingCart.get(0).setNumber(realShoppingCart.get(0).getNumber()+1);
            shoppingCartMapper.update(realShoppingCart.get(0));
        }
        //没有的话，insert,此时注意需要根据菜品/套餐操作相应table 插入冗余字段
        else
        {
            if(shoppingCartDTO.getDishId()!=null)
            {
                //添加的是菜品
                Dish dish = dishMapper.QueryDish(shoppingCartDTO.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setImage(dish.getImage());




            }
            else
            {
                //添加的是套餐
                Setmeal setmeal = setmealMapper.QuerySetmeal(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setImage(setmeal.getImage());



            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }


    }

    @Override
    public List<ShoppingCart> showShoppingCart() {
        List<ShoppingCart> carts = shoppingCartMapper.queryByUserId(BaseContext.getCurrentId());
        return carts;
    }

    @Override
    public void cleanShoppingCart() {
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }

    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        //首先得到数据库中的dish/setmeal
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> realShoppingCart  =  shoppingCartMapper.query(shoppingCart);

        //如果超过两个，改变number
        if(realShoppingCart.get(0).getNumber()>1)
        {
            realShoppingCart.get(0).setNumber(realShoppingCart.get(0).getNumber()-1);
            shoppingCartMapper.update(realShoppingCart.get(0));
        }
        else
        {
            //一个的话，直接删除
            //这里更好的方式是直接通过realShoppingCart的id进行删除
            shoppingCartMapper.delete(realShoppingCart.get(0));
        }

    }
}
