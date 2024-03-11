package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.SetmealDTO;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Api(tags = "套餐相关接口")
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @ApiOperation("新增套餐方法")
    @PostMapping
    public Result add(@RequestBody SetmealDTO setmealDTO)   {

        setmealService.insert(setmealDTO);
        return Result.success();
    }
}
