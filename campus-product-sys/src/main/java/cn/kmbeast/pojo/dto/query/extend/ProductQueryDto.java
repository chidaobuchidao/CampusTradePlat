package cn.kmbeast.pojo.dto.query.extend;

import cn.kmbeast.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品查询条件类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductQueryDto extends QueryDto {
    /**
     * 商品名
     */
    private String name;
    /**
     * 商品类别ID
     */
    private Integer categoryId;
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 新旧程度
     */
    private Integer oldLevel;
    /**
     * 最低价格
     */
    private BigDecimal minPrice;
    /**
     * 最高价格
     */
    private BigDecimal maxPrice;
    /**
     * 通用关键词（同时搜索商品名和描述）
     */
    private String key;
    private Boolean includeSoldOut;
}
