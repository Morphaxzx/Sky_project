package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;



    @Override
    @Transactional
    public void addWithFlavor(DishDTO dishDTO) {


        //这里要干两件事情

        //1.菜品表里面添加一条数据

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);

        Long id = dish.getId();

        //2.口味表里面添加n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null && flavors.size()>0)
        {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(id);
            });
            dishFlavorMapper.BatchInsert(flavors);
        }
    }
}
