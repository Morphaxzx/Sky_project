package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.*;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.Setmeal_DishMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service

public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private Setmeal_DishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;
    @Override
    @Transactional
    public void insert(SetmealDTO setmealDTO) {
        //此时需要往setmeal 和setmeal_dish两张table中插入数据
        //同时注意要把setmealId赋值给setmealDish

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.insert(setmeal);


        List<SetmealDish>  setmealDishes = setmealDTO.getSetmealDishes();
        Long setmealId = setmeal.getId();

        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        setmealDishMapper.Batchinsert(setmealDishes);
    }


    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());

    }

    @Override
    @Transactional
    public void BatchDelete(List<Long> ids) {
        //1.判断是否可以删除，存在起售菜品
        List<Setmeal> setmeals = setmealMapper.QueryByids(ids);
        for (Setmeal setmeal : setmeals) {
            if(setmeal.getStatus() == StatusConstant.ENABLE)
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }

        //3.删除菜品
        setmealMapper.DeleteByIds(ids);
        //4.删除对应套餐关系表中的数据
        setmealDishMapper.DeleteBySetmealIds(ids);
    }

    @Override
    public SetmealVO GetSetmealById(long id) {
        SetmealVO result = new SetmealVO();

        Setmeal setmeal = setmealMapper.QuerySetmeal(id);
        BeanUtils.copyProperties(setmeal,result);
        List<SetmealDish> setmealDishes = setmealDishMapper.QueryBySetmealId(id);
        result.setSetmealDishes(setmealDishes);

        return result;
    }

    @Override
    public void update(SetmealDTO setmealDTO) {
        //1.更新dish
        Setmeal setmeal =new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);

        //2.更新setmeal-dish表，即删掉再新增
        setmealDishMapper.DeleteBySetmealId(setmealDTO.getId());
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes!=null && setmealDishes.size()>0)
        {
            setmealDishes.forEach(dishFlavor -> {
                dishFlavor.setSetmealId(setmealDTO.getId());
            });
            setmealDishMapper.Batchinsert(setmealDishes);
        }
    }


    @Override
    public void startOrStop(Integer status, Long id) {
        //如果要启用一个套餐，先判断包含菜品是否存在停售中的菜品，存在的话则无法起售
        if(status==StatusConstant.ENABLE)
        {
            List<Dish> dishes = dishMapper.getDishesBySetmealId(id);
            if(dishes!=null && dishes.size()>0)
            {
                dishes.forEach(dish -> {
                    if(dish.getStatus()==StatusConstant.DISABLE)
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                });
            }
        }



        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();

        setmealMapper.update(setmeal);
    }


    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }



    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
