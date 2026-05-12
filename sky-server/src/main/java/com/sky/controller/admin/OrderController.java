package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Api(tags = "订单管理")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 根据条件查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("根据条件查询订单")
    public Result<PageResult> page(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("根据条件查询订单，条件：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 统计订单状态
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("统计订单状态")
    public Result<OrderStatisticsVO> countStatistics(){
        log.info("统计订单状态");
        OrderStatisticsVO orderStatisticsVO = orderService.countStatistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 根据订单id查询订单详情
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation("根据订单id查询订单详情")
    public Result<OrderVO> getDetails(@PathVariable Long id){
        log.info("根据订单id查询订单详情，订单id：{}", id);
        OrderVO orderVO = orderService.getById(id);
        return Result.success(orderVO);
    }

    /**
     * 确认订单
     *
     * @param ordersConfirmDTO
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("确认订单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("确认订单");
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 取消订单
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation("拒绝订单")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        log.info("拒绝订单");
        orderService.reject(ordersRejectionDTO);
        return Result.success();
    }


    /**
     * 取消订单
     *
     * @param ordersCancelDTO
     * @return
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel (@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("取消订单,{}",ordersCancelDTO);
        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 配送订单
     *
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("配送订单")
    public Result delivery(@PathVariable Long id){
        log.info("配送订单，订单id：{}", id);
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 完成订单
     * @param id
     * @return
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable Long id){
        log.info("完成订单，订单id：{}", id);
        orderService.complete(id);
        return Result.success();
    }

}
