package cn.kmbeast.controller;

import cn.kmbeast.aop.Pager;
import cn.kmbeast.aop.Protector;
import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.OrdersQueryDto;
import cn.kmbeast.pojo.entity.Orders;
import cn.kmbeast.service.OrdersService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Resource
    private OrdersService ordersService;

    /**
     * 创建订单
     */
    @Protector
    @PostMapping(value = "/create")
    public Result<String> create(@RequestBody Orders orders) {
        return ordersService.create(orders);
    }

    /**
     * 更新订单状态
     */
    @Protector
    @PutMapping(value = "/updateStatus")
    public Result<String> updateStatus(@RequestBody Orders orders) {
        return ordersService.updateStatus(orders);
    }

    /**
     * 我的订单（购买 + 售出）
     */
    @Protector
    @Pager
    @PostMapping(value = "/myOrders")
    public Result<List<Orders>> myOrders(@RequestBody OrdersQueryDto ordersQueryDto) {
        Integer userId = LocalThreadHolder.getUserId();
        // role: 'buy'=仅购买, 'sell'=仅售出, 默认=全部
        if ("buy".equals(ordersQueryDto.getRole())) {
            ordersQueryDto.setBuyerId(userId);
        } else if ("sell".equals(ordersQueryDto.getRole())) {
            ordersQueryDto.setSellerId(userId);
        } else {
            ordersQueryDto.setBuyerId(userId);
            ordersQueryDto.setSellerId(userId);
        }
        return ordersService.query(ordersQueryDto);
    }

    @Protector
    @PutMapping(value = "/updatePrice")
    public Result<String> updatePrice(@RequestBody Orders orders) {
        return ordersService.updatePrice(orders);
    }

    /**
     * 根据商品ID查找当前用户的待支付订单
     */
    @Protector
    @GetMapping(value = "/findByProduct/{productId}")
    public Result<Orders> findByProduct(@PathVariable Integer productId) {
        Integer userId = LocalThreadHolder.getUserId();
        return ordersService.findByProductAndBuyer(productId, userId);
    }

    /**
     * 买家付款
     */
    @Protector
    @PutMapping(value = "/pay")
    public Result<String> pay(@RequestBody Orders orders) {
        return ordersService.pay(orders.getId());
    }

    /**
     * 卖家发货
     */
    @Protector
    @PutMapping(value = "/ship")
    public Result<String> ship(@RequestBody Orders orders) {
        return ordersService.ship(orders.getId());
    }

    /**
     * 买家确认收货
     */
    @Protector
    @PutMapping(value = "/receive")
    public Result<String> receive(@RequestBody Orders orders) {
        return ordersService.receive(orders.getId());
    }

    /**
     * 交易完成
     */
    @Protector
    @PutMapping(value = "/complete")
    public Result<String> complete(@RequestBody Orders orders) {
        return ordersService.complete(orders.getId());
    }

    /**
     * 取消订单（买家或卖家）
     */
    @Protector
    @PutMapping(value = "/cancel")
    public Result<String> cancel(@RequestBody Orders orders) {
        return ordersService.cancel(orders.getId());
    }

    /**
     * 订单统计数据
     */
    @Protector
    @GetMapping(value = "/stats")
    public Result<Map<String, Object>> stats() {
        return ordersService.stats();
    }
}
