package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 新增订单
     * @param orders
     */
    @Insert("insert into orders (number, user_id, address_book_id, order_time, checkout_time, " +
            "amount, remark, phone, address, user_name, consignee, cancel_reason, rejection_reason, " +
            "cancel_time, estimated_delivery_time, delivery_time, pack_amount, tableware_number) " +
            "values " +
            "(#{number}, #{userId}, #{addressBookId}, #{orderTime}, #{checkoutTime}, " +
            "#{amount}, #{remark}, #{phone}, #{address}, #{userName}, #{consignee}, #{cancelReason}, #{rejectionReason}, " +
            "#{cancelTime}, #{estimatedDeliveryTime}, #{deliveryTime}, #{packAmount}, #{tablewareNumber})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Orders orders);

    /**
     * 更新订单状态
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据订单id查询订单详情
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 分页查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    List<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 删除订单
     * @param id
     */
    @Delete("delete from orders where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据订单状态查询订单菜品信息
     * @param orders
     * @return
     */
    List<Orders> list(Orders orders);

    /**
     * 根据订单状态查询订单菜品信息
     * @param status
     * @param time
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime time);

    /**
     * 批量更新订单状态
     * @param timeoutOrders
     */
    void updateBatch(List<Orders> timeoutOrders);
}
