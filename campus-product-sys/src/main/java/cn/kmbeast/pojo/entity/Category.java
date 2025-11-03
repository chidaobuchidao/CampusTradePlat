package cn.kmbeast.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品类别
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Category {
    /**
     * 商品类别编号
     */
    private Integer id;
    /**
     * 商品类别名称
     */
    private String Name;
    /**
     * 是否启用
     */
    private Boolean isUse;

}
