package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Api(tags = "套餐相关接口")
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @ApiOperation("新增套餐方法")
    @PostMapping
    @CacheEvict(cacheNames = "SetmealCache",key = "#setmealDTO.categoryId")
    public Result add(@RequestBody SetmealDTO setmealDTO)   {

        setmealService.insert(setmealDTO);
        return Result.success();
    }

    @ApiOperation("分页展示套餐方法")
    @GetMapping("/page")
    public Result<PageResult> add(SetmealPageQueryDTO setmealPageQueryDTO)   {

        PageResult re = setmealService.page(setmealPageQueryDTO);
        return Result.success(re);
    }

    @DeleteMapping
    @ApiOperation("删除套餐")
    @CacheEvict(cacheNames = "SetmealCache",allEntries = true)
    public Result<String> deleteSetmeal(@RequestParam List<Long> ids){
        log.info("删除套餐", ids);
        setmealService.BatchDelete(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> GetByIdWithDish(@PathVariable long id){
        log.info("根据id查询套餐,id: ", id);
        SetmealVO setmealVO = setmealService.GetSetmealById(id);
        return Result.success(setmealVO);
    }


    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "SetmealCache",allEntries = true)
    public Result Update(@RequestBody  SetmealDTO setmealDTO){
        log.info("修改套餐");
        setmealService.update(setmealDTO);
        return Result.success();
    }


    @PostMapping("/status/{status}")
    @ApiOperation("启售停售分类")
    @CacheEvict(cacheNames = "SetmealCache",allEntries = true)
    public Result startOrStop(@PathVariable("status") Integer status, Long id){
        setmealService.startOrStop(status,id);
        return Result.success();
    }

}
