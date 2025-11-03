package cn.kmbeast.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Orders {
    /**
     * 订单编号
     */
    private Integer id;
    /**
     * 订单号
     */
    private String code;
    /**
     * 订单备注
     */
    private String detail;
    /**
     * 商品ID
     */
    private Integer productId;
    /**
     * 订单价格
     */
    private BigDecimal price;
    /**
     * 订单状态
     */
    private Boolean tradeStatus;
    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /**
     * 支付时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;
}
