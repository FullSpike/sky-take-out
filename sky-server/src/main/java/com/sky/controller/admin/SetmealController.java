package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;


    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result insert(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐：{}", setmealDTO);
        setmealService.insertWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 查询套餐分页列表
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("查询套餐分页列表")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("查询套餐分页列表：{}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除套餐：{}", ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 套餐停发售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐停发售")
       public Result startOrStop(@PathVariable Integer status,
                              long id){
        log.info("套餐停发售：{}", id);
        setmealService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据套餐id查询套餐详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据套餐id查询套餐详情")
       public Result<SetmealVO> getById(@PathVariable long id){
        log.info("根据套餐id查询套餐详情：{}", id);
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    /**
     * 更新套餐
     * @param setmealDTO
     * @return
     */
    @PutMapping
    @ApiOperation("更新套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("更新套餐：{}", setmealDTO);
        setmealService.updateWithDish(setmealDTO);
        return Result.success();
    }
}
