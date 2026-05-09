package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Api(tags = "购物车接口")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加购物车")
       public Result add(@RequestBody ShoppingCartDTO shoppingDTO){
        log.info("添加购物车,{}", shoppingDTO);
        shoppingCartService.add(shoppingDTO);
        return Result.success();
    }

    /**
     * 查询购物车
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(value = "查询购物车")
    public Result<List<ShoppingCart>> list(){
        log.info("查询购物车");
        List<ShoppingCart> list = shoppingCartService.list();
        return Result.success(list);
    }

    /**
     * 删除购物车
     * @return
     */
    @DeleteMapping("/clean")
    @ApiOperation(value = "清空购物车")
    public Result clean(){
        log.info("清空购物车");
        shoppingCartService.clean();
        return Result.success();
    }

    /**
     * 减少购物车数量
     * @param shoppingDTO
     * @return
     */
    @PostMapping("/sub")
    @ApiOperation(value = "删除购物车中一个商品")
    public Result sub(@RequestBody ShoppingCartDTO shoppingDTO){
        log.info("删除购物车中一个商品,{}", shoppingDTO);
        shoppingCartService.sub(shoppingDTO);
        return Result.success();
    }

}
