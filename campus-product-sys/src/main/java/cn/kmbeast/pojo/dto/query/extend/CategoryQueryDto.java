package cn.kmbeast.pojo.dto.query.extend;

import cn.kmbeast.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品类别的查询条件类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryQueryDto extends QueryDto {
    /**
     * 分类名
     */
    private String name;
    /**
     * 是否启用
     */
    private Boolean isUse;
}