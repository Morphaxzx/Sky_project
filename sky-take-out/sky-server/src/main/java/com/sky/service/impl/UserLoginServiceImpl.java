package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserLoginMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserLoginService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    public static final String WX_LOGIN= "https://api.weixin.qq.com/sns/jscode2session";


    @Autowired
    private UserLoginMapper userLoginMapper;
    @Autowired
    private WeChatProperties weChatProperties;
    @Override
    public User login(UserLoginDTO userLoginDTO) {

        //调用微信服务接口，获取openid
        String openid = GetOpenid(userLoginDTO);
        //判断openid是否为空，为空表示登录失败

        if (openid==null)
        {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //判断是否为新用户

        User user = userLoginMapper.queryUserByOpenid(openid);

        //新用户完成注册
        if (user==null)
        {
            user = User.builder().openid(openid).createTime(LocalDateTime.now()).build();
            userLoginMapper.insert(user);
        }
        //返回用户对象

        return  user;
    }

    /**
     * 调用微信接口获取openid
     * @param userLoginDTO
     * @return
     */
    private String GetOpenid(UserLoginDTO userLoginDTO)
    {
        HashMap<String,String> map =new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",userLoginDTO.getCode());
        map.put("grant_type","authorization_code");
        String s = HttpClientUtil.doGet(WX_LOGIN, map);
        JSONObject jsonObject = JSON.parseObject(s);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
