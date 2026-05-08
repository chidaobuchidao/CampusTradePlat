package cn.kmbeast.mapper;

import cn.kmbeast.pojo.dto.query.extend.ProductQueryDto;
import cn.kmbeast.pojo.entity.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品持久化接口
 */
public interface ProductMapper {

    int save(Product product);

    int update(Product product);

    void batchDelete(@Param("ids") List<Integer> ids);

    List<Product> query(ProductQueryDto productQueryDto);

    int queryCount(ProductQueryDto productQueryDto);

    Product getById(Integer id);

    List<Product> recommendByCategories(@Param("categoryIds") List<Integer> categoryIds, @Param("excludeIds") List<Integer> excludeIds, @Param("limit") int limit);
}
