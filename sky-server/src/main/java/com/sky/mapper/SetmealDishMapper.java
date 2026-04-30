package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品ids列表查询套餐菜品关联关系
     * @param dishIds
     * @return
     */
    List<SetmealDish> selectByDishIds(List<Long> dishIds);
}
