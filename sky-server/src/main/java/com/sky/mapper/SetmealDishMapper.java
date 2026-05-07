package com.sky.mapper;

import com.sky.entity.SetmealDish;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品ids列表查询套餐菜品关联关系
     * @param dishIds
     * @return
     */
    List<SetmealDish> selectByDishIds(List<Long> dishIds);

    /**
     * 批量新增套餐菜品关联关系
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐菜品关联关系ids列表删除套餐菜品关联关系
     * @param setmealIds
     */
    void deleteBatch(List<Long> setmealIds);

    /**
     * 根据套餐id查询起售菜品数量
     * @param setmealId
     * @return
     */
    int countStopBySetmealId(long setmealId);

    /**
     * 根据套餐id查询套餐菜品关联关系
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> selectBySetmealId(long setmealId);

    /**
     * 根据套餐id删除套餐菜品关联关系
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(long setmealId);

    /**
     * 根据套餐id查询包含的菜品列表
     * @param id
     * @return
     */
    List<DishItemVO> selectDishItemBySetmealId(Long id);
}
