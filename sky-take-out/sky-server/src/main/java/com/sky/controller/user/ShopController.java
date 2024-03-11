package com.sky.controller.user;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("userShopController")
@Api(tags = "用户端店铺相关接口")
@RequestMapping("/user/shop")
public class ShopController {

//    @Autowired
//    private RedisTemplate redisTemplate;
//    public static final String KEY = "SHOP_STATUS";
//    @GetMapping("/status")
//    @ApiOperation("获取店铺状态")
//    public Result<Integer> GetStatus(){
//        log.info("获取店铺状态,1为营业中，0为打烊中");
//        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
//        return Result.success(status);
//    }

}
