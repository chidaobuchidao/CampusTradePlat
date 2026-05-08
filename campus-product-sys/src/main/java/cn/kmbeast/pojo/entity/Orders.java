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
    private String address;
    private String phone;
    private String remark;
    /**
     * 买家用户ID
     */
    private Integer userId;
    /**
     * 商品ID
     */
    private Integer productId;
    /**
     * 订单价格
     */
    private BigDecimal price;
    /**
     * 订单状态(旧)
     */
    private Boolean tradeStatus;
    /**
     * 订单状态: 0待支付 1已支付 2已发货 3已收货 4已完成 5已取消
     */
    private Integer status;
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

    // ---- 联表字段（非数据库列） ----
    private String productName;
    private String coverList;
    private String sellerName;
    private String buyerName;
}
