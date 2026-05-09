package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * 添加购物车
     * @param shoppingDTO
     */
    void add(ShoppingCartDTO shoppingDTO);

    /**
     *
     * @return
     */
    List<ShoppingCart> list();

    /**
     * 删除购物车
     */
    void clean();

    /**
     * 减少购物车数量
     * @param shoppingDTO
     */
    void sub(ShoppingCartDTO shoppingDTO);
}
