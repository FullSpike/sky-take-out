package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 新增菜品
     * @param dishDTO
     */
    void saveWithDishFlavor(DishDTO dishDTO);

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQueryWithFlavor(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    DishVO getByIdWithFlavor(long id);

    /**
     * 更新菜品
     * @param dishDTO
     */
    void updateDishWithFlavor(DishDTO dishDTO);

    /**
     * 起售/停售菜品
     * @param id
     * @param status
     */
    void startOrClose(long id, Integer status);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    List<Dish> getByCategoryId(long categoryId);
}
