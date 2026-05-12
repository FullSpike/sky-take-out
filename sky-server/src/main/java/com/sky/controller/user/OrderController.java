package com.sky.controller.user;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Api(tags = "订单管理相关接口")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;


    /**
     * 提交订单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @PostMapping("/submit")
    @ApiOperation(value = "提交订单")
    public Result save(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("提交订单");
        OrderSubmitVO orderSubmitVO = orderService.save(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) {
        log.info("订单支付");
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        return Result.success(orderPaymentVO);
    }

    /**
     * 根据订单id查询订单详情
     *
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("根据订单id查询订单详情")
    public Result<OrderVO> getById(@PathVariable Long id) {
        log.info("根据订单id查询订单详情，订单id：{}", id);
        OrderVO orderVO = orderService.getById(id);
        return Result.success(orderVO);
    }

    /**
     * 查询用户订单订单列表
     *
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @GetMapping("/historyOrders")
    @ApiOperation("查询用户订单订单列表")
    public Result<PageResult> page(int page, int pageSize, Integer status) {
        log.info("查询用户订单订单列表");
        PageResult pageResult = orderService.page(page, pageSize, status);
        return Result.success(pageResult);
    }

    /**
     * 取消订单
     *
     * @param id
     * @return
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result delete(@PathVariable Long id) {
        log.info("取消订单，订单id：{}", id);
        orderService.deleteById(id);
        return Result.success();
    }

    /**
     * 再来订单
     *
     * @param id
     * @return
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来订单")
    public Result repeat(@PathVariable Long id) {
        log.info("再来订单，订单id：{}", id);
        orderService.repeat(id);
        return Result.success();
    }



}
