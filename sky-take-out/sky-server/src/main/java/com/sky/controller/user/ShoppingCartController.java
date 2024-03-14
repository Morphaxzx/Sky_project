package com.sky.controller.user;

import com.sky.dto.SetmealPageQueryDTO;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Api(tags = "购物车相关接口")
@RequestMapping("/user/shoppingCart")



public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @ApiOperation("购物车添加菜品/套餐功能")
    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO)   {
        log.info("添加购物车，商品信息为{}",shoppingCartDTO);
        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }


    @ApiOperation("查看购物车方法")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list()   {
        log.info("查看购物车");
        List<ShoppingCart> carts= shoppingCartService.showShoppingCart();
        return Result.success(carts);
    }


    @ApiOperation("清空购物车方法")
    @DeleteMapping("/clean")
    public Result cleanShoppingCart()   {
        log.info("清空购物车");
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }


    @ApiOperation("删除购物车中菜品/套餐")
    @PostMapping("/sub")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO)   {
        log.info("删除购物车中菜品/套餐，商品信息为{}",shoppingCartDTO);
        shoppingCartService.sub(shoppingCartDTO);
        return Result.success();
    }
}
