package com.sky.service.impl;


import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.Setmeal_DishMapper;
import com.sky.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service

public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private Setmeal_DishMapper setmealDishMapper;
    @Override
    @Transactional
    public void insert(SetmealDTO setmealDTO) {
        //此时需要往setmeal 和setmeal_dish两张table中插入数据
        //同时注意要把setmealId赋值给setmealDish

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.insert(setmeal);


        List<SetmealDish>  setmealDishes = setmealDTO.getSetmealDishes();
        Long setmealId = setmeal.getId();

        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        setmealDishMapper.Batchinsert(setmealDishes);
    }
}
