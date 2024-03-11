package com.sky.service;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DishService {
    /**
     * 新增菜品
     * @param dishDTO
     */
    void addWithFlavor(DishDTO dishDTO);

    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    void BatchDelete(List<Long> ids);

    DishVO GetDishById(long id);

    void update(DishDTO dishDTO);

    List<Dish> SelectDishbyCategory(Long id);
}
