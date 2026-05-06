package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void insertWithDish(SetmealDTO setmealDTO) {

        //新增套餐
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        setmealMapper.insert(setmeal);

        long setmealId = setmeal.getId();

        //新增套餐菜品关系
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {

        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        Page<Setmeal> page = setmealMapper.selectPage(setmealPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {

        //判断套餐是否起售
        int count = setmealMapper.countStartByids(ids);
        if(count > 0){
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }

        //删除套餐
        setmealMapper.deleteBatch(ids);
        //删除套餐菜品关联关系
        setmealDishMapper.deleteBatch(ids);
    }

    /**
     * 套餐停发售
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, long id) {

        //判断是否是发出起售申请
        if(status == StatusConstant.ENABLE){
            //套餐是否有未起售菜品
            int count = setmealDishMapper.countStopBySetmealId(id);
            if(count > 0){
                throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }

        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();

        setmealMapper.update(setmeal);
    }

    /**
     * 根据套餐id查询套餐详情
     * @param id
     * @return
     */
    @Override
    @Transactional
    public SetmealVO getById(long id) {

        Setmeal setmeal = setmealMapper.selectById(id);

        //进行对象的转换
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);

        //查询套餐菜品关联关系
        List<SetmealDish> setmealDishes = setmealDishMapper.selectBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }

    /**
     * 更新套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void updateWithDish(SetmealDTO setmealDTO) {

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        //更新套餐
        setmealMapper.update(setmeal);

        //获取套餐id
        long setmealId = setmeal.getId();

        //删除套餐菜品关联关系
        setmealDishMapper.deleteBySetmealId(setmealId);

        //更新套餐菜品关联关系
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
        setmealDishMapper.insertBatch(setmealDishes);
    }


}
