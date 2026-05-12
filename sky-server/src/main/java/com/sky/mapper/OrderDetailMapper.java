package com.sky.mapper;

import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {


    /**
     * 批量插入订单详情
     * @param orderDetailList
     */
    void insertBatch(List<OrderDetail> orderDetailList);

    /**
     * 根据订单id查询订单菜品信息
     * @param id
     * @return
     */
    @Select("select * from order_detail where order_id = #{id}")
    List<OrderDetail> getByOrderId(Long id);

    /**
     * 删除订单菜品信息
     * @param id
     */
    @Delete("delete from order_detail where order_id = #{id}")
    void deleteByOrderId(Long id);
}
