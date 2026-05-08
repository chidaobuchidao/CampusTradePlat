package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.OrdersQueryDto;
import cn.kmbeast.pojo.entity.Orders;

import java.util.List;
import java.util.Map;

/**
 * 订单业务逻辑接口
 */
public interface OrdersService {

    Result<String> create(Orders orders);

    Result<String> updateStatus(Orders orders);

    Result<List<Orders>> query(OrdersQueryDto ordersQueryDto);

    Result<Orders> getById(Integer id);

    Result<String> updatePrice(Orders orders);

    Result<Orders> findByProductAndBuyer(Integer productId, Integer userId);

    Result<String> pay(Integer orderId);

    Result<String> ship(Integer orderId);

    Result<String> receive(Integer orderId);

    Result<String> complete(Integer orderId);

    Result<String> cancel(Integer orderId);

    Result<Map<String, Object>> stats();
}
