package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    /**
     * 新增套餐
     * @param setmealDTO
     */
    void insertWithDish(SetmealDTO setmealDTO);

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 批量删除套餐
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 套餐停发售
     * @param status
     * @param id
     */
    void startOrStop(Integer status, long id);

    /**
     * 根据套餐id查询套餐详情
     * @param id
     * @return
     */
    SetmealVO getById(long id);

    /**
     * 更新套餐
     * @param setmealDTO
     */
    void updateWithDish(SetmealDTO setmealDTO);
}
