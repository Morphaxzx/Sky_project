package com.sky.mapper;


import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface Setmeal_DishMapper {


    void Batchinsert(List<SetmealDish> setmealDishes);
}
