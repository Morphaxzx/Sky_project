package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {


    List<ShoppingCart> query(ShoppingCart shoppingCart);

    void update(ShoppingCart realShoppingCart);

    void insert(ShoppingCart shoppingCart);

    @Select("select * from shopping_cart where user_id = #{currentId}")
    List<ShoppingCart> queryByUserId(Long currentId);

    @Delete("delete from shopping_cart where user_id = #{currentId}")
    void deleteByUserId(Long currentId);

    void delete(ShoppingCart shoppingCart);
}
