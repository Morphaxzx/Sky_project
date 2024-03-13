package com.sky.service;


import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserLoginService {

    /**
     * 微信登录接口
     * @return
     */

    User login(UserLoginDTO userLoginDTO);
}
