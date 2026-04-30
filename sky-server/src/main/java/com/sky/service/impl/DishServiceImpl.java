package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Override
    @Transactional
    public void saveWithDishFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //新增菜品
        dishMapper.insert(dish);
        //新增菜品口味
        long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();

        if(flavors != null && !flavors.isEmpty()){
            flavors.forEach(flavor -> {
                flavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQueryWithFlavor(DishPageQueryDTO dishPageQueryDTO) {

        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        //查询菜品
        try (Page<DishVO> page = dishMapper.pageQueryWithCategoryName(dishPageQueryDTO)) {
            //返回分页结果
            return new PageResult(page.getTotal(), page.getResult());
        }
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {

        List<Dish> dishes = dishMapper.selectStartByIds(ids);
        if(dishes != null && !dishes.isEmpty()){
            //不能删除起售中的菜品
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }

        List<SetmealDish> setmealDishes = setmealDishMapper.selectByDishIds(ids);
        if(setmealDishes != null && !setmealDishes.isEmpty()){
            //不能删除套餐中的菜品
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品
        dishMapper.deleteBatch(ids);

        //删除菜品口味
        dishFlavorMapper.deleteBatchByDishIds(ids);
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Override
    @Transactional
    public DishVO getByIdWithFlavor(long id) {

        Dish dish = dishMapper.selectById(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);

        List<DishFlavor> flavors = dishFlavorMapper.selectByDishId(id);
        dishVO.setFlavors(flavors);

        return dishVO;
    }

    /**
     * 更新菜品
     * @param dishDTO
     */
    @Override
    @Transactional
    public void updateDishWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //更新菜品
        dishMapper.update(dish);

        //删除菜品口味
        dishFlavorMapper.deleteBatchByDishIds(List.of(dishDTO.getId()));
        //新增菜品口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()) {
            flavors.forEach(flavor -> {
                flavor.setDishId(dish.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 起售/停售菜品
     * @param id
     * @param status
     */
    @Override
    public void startOrClose(long id, Integer status) {

        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();

        //更新菜品状态
        dishMapper.update(dish);
    }

}
