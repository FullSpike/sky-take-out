package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;

    @Value("${sky.shop.address")
    private String shopAddress;
    @Value("${sky.baidu.ak}")
    private String ak;

    /**
     * 新增订单
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    @Override
    public OrderSubmitVO save(OrdersSubmitDTO ordersSubmitDTO) {

        //地址不存在异常
        Long addressBookId = ordersSubmitDTO.getAddressBookId();
        AddressBook addressBook = addressBookMapper.getById(addressBookId);
        if(addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        //配送距离超过5000米异常
        //checkOutOfRange(addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail());

        //购物车为空异常
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if(list == null || list.isEmpty()){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //获取用户姓名
        String userName = userMapper.getById(userId).getName();

        //新增订单
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setUserId(userId);
        orders.setAddress(addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail());
        orders.setPhone(addressBook.getPhone());
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setUserName(userName);
        orderMapper.insert(orders);

        //增加订单细节
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for(ShoppingCart cart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);

        //删除购物车数据
        shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        shoppingCartMapper.delete(shoppingCart);

        return new OrderSubmitVO(orders.getId(), orders.getNumber(), orders.getAmount(), orders.getOrderTime());

    }

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        Orders orders =  Orders.builder()
                .status(Orders.TO_BE_CONFIRMED)
                .number(ordersPaymentDTO.getOrderNumber())
                .checkoutTime(LocalDateTime.now())
                .payStatus(Orders.PAID)
                .build();
        //更新订单状态
        orderMapper.update(orders);

        return null;
    }

    /**
     * 根据订单id查询订单详情
     * @param id
     * @return
     */
    @Override
    @Transactional
    public OrderVO getById(Long id) {
        //根据订单id查询订单详情
        Orders orders = orderMapper.getById(id);

        //根据订单id查询订单菜品信息
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        //返回数据
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    /**
     * 分页查询订单
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult page(int page, int pageSize, Integer status) {
        //开启分页查询
        PageHelper.startPage(page, pageSize);

        //查询订单列表
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setPage(page);
        ordersPageQueryDTO.setPageSize(pageSize);
        ordersPageQueryDTO.setStatus(status);
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        List<Orders> ordersList = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> orderVOList = new ArrayList<>();
        for(Orders orders : ordersList) {
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            orderVO.setOrderDetailList(orderDetailList);
            orderVOList.add(orderVO);
        }

        //返回数据
        return new PageResult(orderVOList.size(), orderVOList);
    }

    /**
     * 取消订单
     * @param id
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        //根据订单id查询订单详情
        Orders ordersDB = orderMapper.getById(id);

        //业务异常
        if(ordersDB == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if(ordersDB.getStatus() > 2){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(id);

        //根据订单id删除订单菜品信息
        orders.setCancelTime(LocalDateTime.now());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消订单");
        orderMapper.update(orders);
    }

    /**
     * 再来订单
     * @param id
     */
    @Override
    @Transactional
    public void repeat(Long id) {
        //根据订单id查询订单详情
        Orders ordersDB = orderMapper.getById(id);

        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersDB, orders);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setOrderTime(LocalDateTime.now());
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setCancelReason(null);
        orders.setCancelTime(null);

        orderMapper.insert(orders);

    }

    /**
     * 根据条件查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        //开启分页查询
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        //查询订单列表
        List<Orders> ordersList = orderMapper.pageQuery(ordersPageQueryDTO);

        //返回数据
        return new PageResult(ordersList.size(), ordersList);
    }

    /**
     * 统计订单状态
     * @return
     */
    @Override
    @Transactional
    public OrderStatisticsVO countStatistics() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        Orders orders = new Orders();

        //查询订单状态统计
        orders.setStatus(Orders.CONFIRMED);
        List<Orders> confirmedList = orderMapper.list(orders);
        orderStatisticsVO.setConfirmed(confirmedList.size());

        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        List<Orders> deliveryInProgressList = orderMapper.list(orders);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgressList.size());

        orders.setStatus(Orders.TO_BE_CONFIRMED);
        List<Orders> toBeConfirmedList = orderMapper.list(orders);
        orderStatisticsVO.setToBeConfirmed(toBeConfirmedList.size());

        return orderStatisticsVO;
    }

    /**
     * 确认订单
     * @param ordersConfirmDTO
     */
    @Override
    @Transactional
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        //根据订单id查询订单详情
        Orders ordersDB = orderMapper.getById(ordersConfirmDTO.getId());

        //业务异常
        if(ordersDB == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //更新订单状态
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();
        orderMapper.update(orders);
    }

    /**
     * 拒绝订单
     * @param ordersRejectionDTO
     */
    @Override
    @Transactional
    public void reject(OrdersRejectionDTO ordersRejectionDTO) {
        //根据订单id查询订单详情
        Orders ordersDB = orderMapper.getById(ordersRejectionDTO.getId());

        //业务异常
        if(ordersDB == null || !Orders.TO_BE_CONFIRMED.equals(ordersDB.getStatus())){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //更新订单状态
        Orders orders = Orders.builder()
                .id(ordersRejectionDTO.getId())
                .status(Orders.CANCELLED)
                .rejectionReason(ordersRejectionDTO.getRejectionReason())
                .cancelTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);

    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    @Override
    @Transactional
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        //根据订单id查询订单详情
        Orders orderDB= orderMapper.getById(ordersCancelDTO.getId());

        //业务异常
        if(orderDB == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //更新数据库
        Orders orders = Orders.builder()
                .id(orderDB.getId())
                .status(Orders.CANCELLED)
                .cancelReason(ordersCancelDTO.getCancelReason())
                .cancelTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);
    }

    /**
     * 配送订单
     * @param id
     */
    @Override
    @Transactional
    public void delivery(Long id) {
        //根据订单id查询数据库
        Orders ordersDB = orderMapper.getById(id);

        //业务异常
        if(ordersDB == null || !Orders.CONFIRMED.equals(ordersDB.getStatus())){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //更新数据库数据
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.DELIVERY_IN_PROGRESS)
                .build();
        orderMapper.update(orders);
    }

    /**
     * 完成订单
     * @param id
     */
    @Override
    @Transactional
    public void complete(Long id) {
        //根据订单id查询数据库
        Orders ordersDB = orderMapper.getById(id);

        //业务异常
        if(ordersDB == null || !Orders.DELIVERY_IN_PROGRESS.equals(ordersDB.getStatus())){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //更新数据库数据
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);
    }


    /**
     * 检查客户的收货地址是否超出配送范围
     * @param address
     */
    private void checkOutOfRange(String address) {
        Map map = new HashMap();
        map.put("address",shopAddress);
        map.put("output","json");
        map.put("ak",ak);

        //获取店铺的经纬度坐标
        String shopCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        JSONObject jsonObject = JSON.parseObject(shopCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("店铺地址解析失败");
        }

        //数据解析
        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        String lat = location.getString("lat");
        String lng = location.getString("lng");
        //店铺经纬度坐标
        String shopLngLat = lat + "," + lng;

        map.put("address",address);
        //获取用户收货地址的经纬度坐标
        String userCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        jsonObject = JSON.parseObject(userCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("收货地址解析失败");
        }

        //数据解析
        location = jsonObject.getJSONObject("result").getJSONObject("location");
        lat = location.getString("lat");
        lng = location.getString("lng");
        //用户收货地址经纬度坐标
        String userLngLat = lat + "," + lng;

        map.put("origin",shopLngLat);
        map.put("destination",userLngLat);
        map.put("steps_info","0");

        //路线规划
        String json = HttpClientUtil.doGet("https://api.map.baidu.com/directionlite/v1/driving", map);

        jsonObject = JSON.parseObject(json);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("配送路线规划失败");
        }

        //数据解析
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray jsonArray = (JSONArray) result.get("routes");
        Integer distance = (Integer) ((JSONObject) jsonArray.get(0)).get("distance");

        if(distance > 5000){
            //配送距离超过5000米
            throw new OrderBusinessException("超出配送范围");
        }
    }
}
