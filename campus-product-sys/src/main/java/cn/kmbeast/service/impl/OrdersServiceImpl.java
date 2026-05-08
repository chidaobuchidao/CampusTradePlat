package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.MessageMapper;
import cn.kmbeast.mapper.OrdersMapper;
import cn.kmbeast.mapper.ProductMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.PageResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.OrdersQueryDto;
import cn.kmbeast.pojo.entity.Message;
import cn.kmbeast.pojo.entity.Orders;
import cn.kmbeast.pojo.entity.Product;
import cn.kmbeast.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class OrdersServiceImpl implements OrdersService {

    private static final int STATUS_PENDING = 0;
    private static final int STATUS_PAID = 1;
    private static final int STATUS_SHIPPED = 2;
    private static final int STATUS_RECEIVED = 3;
    private static final int STATUS_COMPLETED = 4;
    private static final int STATUS_CANCELLED = 5;

    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private MessageMapper messageMapper;

    @Override
    public Result<String> create(Orders orders) {
        Product product = productMapper.getById(orders.getProductId());
        if (product == null) return ApiResult.error("商品不存在");
        if (product.getInventory() != null && product.getInventory() <= 0) return ApiResult.error("商品库存不足");

        Integer buyerId = LocalThreadHolder.getUserId();
        if (buyerId.equals(product.getUserId())) return ApiResult.error("不能购买自己的商品");

        Orders existing = ordersMapper.findByProductAndBuyer(orders.getProductId(), buyerId);
        if (existing != null) return ApiResult.error("您已有该商品的待支付订单，请勿重复下单");

        orders.setCode(UUID.randomUUID().toString());
        orders.setUserId(buyerId);
        orders.setPrice(product.getPrice());
        orders.setTradeStatus(false);
        orders.setStatus(STATUS_PENDING);
        orders.setCreateTime(LocalDateTime.now());
        ordersMapper.save(orders);

        if (product.getInventory() != null && product.getInventory() > 0) {
            product.setInventory(product.getInventory() - 1);
            productMapper.update(product);
        }

        notifyUser(product.getUserId(), buyerId, product.getId(),
                "您收到了新订单：「" + product.getName() + "」");
        return ApiResult.success("下单成功");
    }

    @Override
    public Result<String> updateStatus(Orders orders) {
        // 兼容旧逻辑
        Orders dbOrders = ordersMapper.getById(orders.getId());
        if (dbOrders == null) return ApiResult.error("订单不存在");
        if (dbOrders.getStatus() != STATUS_PENDING) return ApiResult.error("订单状态不允许此操作");

        orders.setPayTime(LocalDateTime.now());
        orders.setStatus(STATUS_PAID);
        orders.setTradeStatus(true);
        ordersMapper.update(orders);

        Product p = productMapper.getById(dbOrders.getProductId());
        if (p != null) {
            notifyUser(p.getUserId(), dbOrders.getUserId(), p.getId(),
                    "订单「" + p.getName() + "」已付款，等待发货");
        }
        return ApiResult.success("支付成功");
    }

    @Override
    public Result<String> pay(Integer orderId) {
        Orders db = ordersMapper.getById(orderId);
        if (db == null) return ApiResult.error("订单不存在");
        if (db.getStatus() != STATUS_PENDING) return ApiResult.error("订单状态不允许付款");

        Integer userId = LocalThreadHolder.getUserId();
        if (!userId.equals(db.getUserId())) return ApiResult.error("只有买家可以付款");

        updateOrderStatus(orderId, STATUS_PAID);

        Product p = productMapper.getById(db.getProductId());
        if (p != null) {
            notifyUser(p.getUserId(), userId, db.getProductId(),
                    "订单「" + p.getName() + "」已付款，请尽快发货");
        }
        return ApiResult.success("付款成功");
    }

    @Override
    public Result<String> ship(Integer orderId) {
        Orders db = ordersMapper.getById(orderId);
        if (db == null) return ApiResult.error("订单不存在");
        if (db.getStatus() != STATUS_PAID) return ApiResult.error("订单状态不允许发货");

        Integer userId = LocalThreadHolder.getUserId();
        Product product = productMapper.getById(db.getProductId());
        if (product == null || !userId.equals(product.getUserId())) return ApiResult.error("只有卖家可以发货");

        updateOrderStatus(orderId, STATUS_SHIPPED);

        notifyUser(db.getUserId(), userId, db.getProductId(),
                "订单「" + product.getName() + "」已发货，请注意查收");
        return ApiResult.success("已确认发货");
    }

    @Override
    public Result<String> receive(Integer orderId) {
        Orders db = ordersMapper.getById(orderId);
        if (db == null) return ApiResult.error("订单不存在");
        if (db.getStatus() != STATUS_SHIPPED) return ApiResult.error("订单状态不允许确认收货");

        Integer userId = LocalThreadHolder.getUserId();
        if (!userId.equals(db.getUserId())) return ApiResult.error("只有买家可以确认收货");

        updateOrderStatus(orderId, STATUS_RECEIVED);

        Product p = productMapper.getById(db.getProductId());
        if (p != null) {
            notifyUser(p.getUserId(), userId, db.getProductId(),
                    "买家已确认收货，订单「" + p.getName() + "」待评价");
        }
        return ApiResult.success("已确认收货");
    }

    @Override
    public Result<String> complete(Integer orderId) {
        Orders db = ordersMapper.getById(orderId);
        if (db == null) return ApiResult.error("订单不存在");
        if (db.getStatus() != STATUS_RECEIVED) return ApiResult.error("订单状态不允许此操作");

        updateOrderStatus(orderId, STATUS_COMPLETED);
        return ApiResult.success("交易完成");
    }

    @Override
    public Result<List<Orders>> query(OrdersQueryDto ordersQueryDto) {
        int totalCount = ordersMapper.queryCount(ordersQueryDto);
        List<Orders> ordersList = ordersMapper.query(ordersQueryDto);
        return PageResult.success(ordersList, totalCount);
    }

    @Override
    public Result<Orders> getById(Integer id) {
        Orders orders = ordersMapper.getById(id);
        if (orders == null) return ApiResult.error("订单不存在");
        return ApiResult.success(orders);
    }

    @Override
    public Result<String> updatePrice(Orders orders) {
        if (orders.getId() == null || orders.getPrice() == null) return ApiResult.error("参数不完整");
        Orders db = ordersMapper.getById(orders.getId());
        if (db == null) return ApiResult.error("订单不存在");
        if (db.getStatus() != STATUS_PENDING) return ApiResult.error("只有待支付订单可以修改价格");

        Integer userId = LocalThreadHolder.getUserId();
        Product product = productMapper.getById(db.getProductId());
        if (product == null || !userId.equals(product.getUserId())) return ApiResult.error("只有卖家可以修改订单");

        Orders up = new Orders();
        up.setId(orders.getId());
        up.setPrice(orders.getPrice());
        ordersMapper.update(up);

        notifyUser(db.getUserId(), userId, db.getProductId(),
                "订单「" + product.getName() + "」价格已修改为 ¥" + orders.getPrice());
        return ApiResult.success("价格已更新");
    }

    @Override
    public Result<Orders> findByProductAndBuyer(Integer productId, Integer userId) {
        Orders order = ordersMapper.findByProductAndBuyer(productId, userId);
        if (order == null) return ApiResult.error("未找到订单");
        return ApiResult.success(order);
    }

    @Override
    public Result<String> cancel(Integer orderId) {
        Orders db = ordersMapper.getById(orderId);
        if (db == null) return ApiResult.error("订单不存在");
        if (db.getStatus() != STATUS_PENDING) return ApiResult.error("只有待支付订单可以取消");

        Integer userId = LocalThreadHolder.getUserId();
        Product product = productMapper.getById(db.getProductId());

        boolean isBuyer = userId.equals(db.getUserId());
        boolean isSeller = product != null && userId.equals(product.getUserId());
        if (!isBuyer && !isSeller) return ApiResult.error("无权取消此订单");

        // 恢复库存
        if (product != null && product.getInventory() != null) {
            product.setInventory(product.getInventory() + 1);
            productMapper.update(product);
        }

        // 软删除
        ordersMapper.cancelById(orderId);

        if (product != null) {
            if (isBuyer) {
                notifyUser(product.getUserId(), userId, db.getProductId(),
                        "买家取消了订单：「" + product.getName() + "」");
            } else {
                notifyUser(db.getUserId(), userId, db.getProductId(),
                        "卖家取消了订单：「" + product.getName() + "」");
            }
        }
        return ApiResult.success("订单已取消");
    }

    @Override
    public Result<Map<String, Object>> stats() {
        Integer userId = LocalThreadHolder.getUserId();
        Map<String, Object> result = new HashMap<>();

        Map<String, Object> buySum = ordersMapper.sumByBuyer(userId);
        Map<String, Object> sellSum = ordersMapper.sumBySeller(userId);
        Map<String, Object> statusCounts = ordersMapper.countByStatus(userId);
        List<Map<String, Object>> monthly = ordersMapper.monthlyStats(userId);

        result.put("buyTotal", buySum.get("total"));
        result.put("buyAmount", buySum.get("amount"));
        result.put("sellTotal", sellSum.get("total"));
        result.put("sellAmount", sellSum.get("amount"));
        result.put("statusCounts", statusCounts);
        result.put("monthly", monthly);

        return ApiResult.success(result);
    }

    private void updateOrderStatus(Integer orderId, int newStatus) {
        Orders up = new Orders();
        up.setId(orderId);
        up.setStatus(newStatus);
        ordersMapper.update(up);
    }

    private void notifyUser(Integer receiverId, Integer senderId, Integer productId, String content) {
        try {
            Message msg = Message.builder()
                    .receiverId(receiverId).senderId(senderId)
                    .productId(productId)
                    .content(content).isRead(false)
                    .createTime(LocalDateTime.now()).build();
            messageMapper.save(msg);
        } catch (Exception e) {
            log.warn("消息通知失败: receiverId={}, productId={}", receiverId, productId, e);
        }
    }
}
