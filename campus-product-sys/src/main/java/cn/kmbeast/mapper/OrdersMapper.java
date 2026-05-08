package cn.kmbeast.mapper;

import cn.kmbeast.pojo.dto.query.extend.OrdersQueryDto;
import cn.kmbeast.pojo.entity.Orders;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 订单持久化接口
 */
public interface OrdersMapper {

    int save(Orders orders);

    int update(Orders orders);

    List<Orders> query(OrdersQueryDto ordersQueryDto);

    int queryCount(OrdersQueryDto ordersQueryDto);

    Orders getById(Integer id);

    Orders findByProductAndBuyer(@Param("productId") Integer productId, @Param("userId") Integer userId);

    int cancelById(Integer id);

    Map<String, Object> sumByBuyer(@Param("userId") Integer userId);

    Map<String, Object> sumBySeller(@Param("userId") Integer userId);

    Map<String, Object> countByStatus(@Param("userId") Integer userId);

    List<Map<String, Object>> monthlyStats(@Param("userId") Integer userId);
}
