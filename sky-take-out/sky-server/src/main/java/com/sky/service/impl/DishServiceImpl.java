package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;



    @Override
    @Transactional
    public void addWithFlavor(DishDTO dishDTO) {


        //这里要干两件事情

        //1.菜品表里面添加一条数据

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);

        Long id = dish.getId();

        //2.口味表里面添加n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null && flavors.size()>0)
        {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(id);
            });
            dishFlavorMapper.BatchInsert(flavors);
        }
    }

    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());

    }

    @Override
    @Transactional
    public void BatchDelete(List<Long> ids) {
        //1.判断是否可以删除，存在起售菜品
        List<Dish> dishes = dishMapper.QueryByids(ids);
        for (Dish dish : dishes) {
            if(dish.getStatus() == StatusConstant.ENABLE)
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }
        //2.判断是否可以删除，被套餐关联
        //因为setmeal-dish里面没数据，所以跳过

        //3.删除菜品
        dishMapper.DeleteByIds(ids);
        //4.删除对应的口味表
        dishFlavorMapper.DeleteByDishIds(ids);
    }

    @Override
    public DishVO GetDishById(long id) {
        DishVO result = new DishVO();
        Dish d1 = dishMapper.QueryDish(id);
        BeanUtils.copyProperties(d1,result);
        List<DishFlavor> flavor = dishFlavorMapper.QueryFlavor(id);
        result.setFlavors(flavor);

        return result;

    }

    @Override
    public void update(DishDTO dishDTO) {
        //1.更新dish
        Dish dish =new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);

        //2.更新flavor表，即删掉再新增
        dishFlavorMapper.DeleteByDishId(dishDTO.getId());
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null && flavors.size()>0)
        {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.BatchInsert(flavors);
        }
    }

    @Override
    public List<Dish> SelectDishbyCategory(Long CategoryId) {
        Dish dish=Dish.builder()
                .categoryId(CategoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.QueryDishByCategory(dish);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();

        dishMapper.update(dish);
    }


    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.QueryDishByCategory(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.QueryFlavor(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
