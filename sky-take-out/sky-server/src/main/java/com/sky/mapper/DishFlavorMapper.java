package com.sky.mapper;

import com.sky.entity.DishFlavor;
import lombok.Data;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    void BatchInsert(List<DishFlavor> flavors);

    void DeleteByDishIds(List<Long> DishIds);

    @Delete("delete from dish_flavor where dish_id=#{DishId}")
    void DeleteByDishId(long DishId);


    @Select("select * from dish_flavor where dish_id=#{id}")
    List<DishFlavor> QueryFlavor(long id);
}
