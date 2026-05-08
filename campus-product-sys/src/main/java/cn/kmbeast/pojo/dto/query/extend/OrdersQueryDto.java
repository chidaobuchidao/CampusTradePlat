package cn.kmbeast.pojo.dto.query.extend;

import cn.kmbeast.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单查询条件类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrdersQueryDto extends QueryDto {
    /**
     * 买家ID
     */
    private Integer buyerId;
    /**
     * 卖家ID（商品所属用户）
     */
    private Integer sellerId;
    /**
     * 交易状态
     */
    private Boolean tradeStatus;
    private String role;
}
