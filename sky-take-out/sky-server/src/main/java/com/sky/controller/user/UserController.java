package com.sky.controller.user;


import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserLoginService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Api(tags = "C端用户相关接口")
@RequestMapping("/user/user")
@RestController
public class UserController {


    @Autowired
    private UserLoginService userLoginService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    @ApiOperation("微信登录方法")
    public Result<UserLoginVO> GetStatus(@RequestBody UserLoginDTO userLoginDTO){
        log.info("微信用户登录");

        User user =  userLoginService.login(userLoginDTO);

        //生成jwt
        Map<String,Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),jwtProperties.getUserTtl(),claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                                    .id(user.getId())
                                    .openid(user.getOpenid())
                                    .token(token).build();


        return Result.success(userLoginVO);


    }
}