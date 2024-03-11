package com.sky.mapper;


import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface Setmeal_DishMapper {


    void Batchinsert(List<SetmealDish> setmealDishes);

    /**
     * 根据setmeal ids删除套餐菜品关系表中数据
     * @param setmealIds
     */
    void DeleteBySetmealIds(List<Long> setmealIds);

    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> QueryBySetmealId(long setmealId);


    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void DeleteBySetmealId(Long setmealId);
}
