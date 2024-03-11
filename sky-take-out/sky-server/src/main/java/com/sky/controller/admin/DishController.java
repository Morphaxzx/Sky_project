package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@Api(tags = "菜品相关接口")
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @ApiOperation("新增菜品方法")
    @PostMapping
    public Result add( @RequestBody  DishDTO dishDTO)   {

        dishService.addWithFlavor(dishDTO);
        return Result.success();
    }


    @ApiOperation("菜品分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO)
    {
        log.info("菜品分页查询");
        PageResult  re = dishService.page(dishPageQueryDTO);
        return Result.success(re);
    }



    @DeleteMapping
    @ApiOperation("删除菜品")
    public Result<String> deleteDish(@RequestParam List<Long> ids){
        log.info("删除菜品", ids);
        dishService.BatchDelete(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> GetByIdWithFlavor(@PathVariable long id){
        log.info("根据id查询菜品,id: ", id);
        DishVO dishVO = dishService.GetDishById(id);
        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation("修改菜品")
    public Result Update(@RequestBody  DishDTO dishDTO){
        log.info("修改菜品");
        dishService.update(dishDTO);
        return Result.success();
    }



    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> QueryDishbyCategory(Long categoryId){
        log.info("根据分类查询菜品");
        List<Dish> dishes = dishService.SelectDishbyCategory(categoryId);
        return Result.success(dishes);
    }
}
