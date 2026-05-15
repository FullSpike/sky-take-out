package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import com.sky.vo.SalesTop10ReportVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
     * 根据订单号和用户id查询订单
     * @param orderNumber
     * @param userId
     * @return
     */
    @Select("select * from orders where number = #{orderNumber} and user_id = #{userId}")
    Orders getByNumberAndUserId(String orderNumber, Long userId);

    /**
     * 根据订单状态查询订单金额
     * @param map
     * @return
     */
    Double sumAmountByMap(Map map);

    /**
     * 查询Top10Top10销售商品
     * @param beginTime
     * @param endTime
     * @return
     */
    List<GoodsSalesDTO> getTop10ByBeginAndEnd(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 根据订单状态查询订单数
     * @param orderMap
     * @return
     */
    Integer countByMap(Map<String, Object> orderMap);
}
