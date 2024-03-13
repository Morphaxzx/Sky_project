package com.sky.mapper;


import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserLoginMapper {


    @Select("select * from user where openid=#{openid}")
    User queryUserByOpenid(String openid);


    void insert(User user);
}
