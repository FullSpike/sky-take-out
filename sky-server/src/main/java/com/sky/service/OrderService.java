package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * 提交订单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO save(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);

    /**
     * 根据订单id查询订单详情
     * @param id
     * @return
     */
    OrderVO getById(Long id);

    /**
     * 分页查询订单
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult page(int page, int pageSize, Integer status);

    /**
     * 取消订单
     * @param id
     */
    void deleteById(Long id);

    /**
     * 再来订单
     * @param id
     */
    void repeat(Long id);

    /**
     * 根据条件查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 统计订单状态
     * @return
     */
    OrderStatisticsVO countStatistics();

    /**
     * 确认订单
     * @param ordersConfirmDTO
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒绝订单
     * @param ordersRejectionDTO
     */
    void reject(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    void cancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 配送订单
     * @param id
     */
    void delivery(Long id);

    /**
     * 完成订单
     * @param id
     */
    void complete(Long id);
}
