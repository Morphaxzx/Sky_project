package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("adminShopController")
@Api(tags = "管理端店铺相关接口")
@RequestMapping("/admin/shop")
public class ShopController {
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    public static final String KEY = "SHOP_STATUS";
//    @PutMapping("/{status}")
//    @ApiOperation("设置店铺状态")
//    public Result SetStatus(@PathVariable Integer status){
//        log.info("设置店铺状态为{}",status==1?"营业中":"打烊中");
//        redisTemplate.opsForValue().set(KEY,status);
//        return Result.success();
//    }


//
//    @GetMapping("/status")
//    @ApiOperation("获取店铺状态")
//    public Result<Integer> GetStatus(){
//        log.info("获取店铺状态,1为营业中，0为打烊中");
//        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
//        return Result.success(status);
//    }


}
