package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.ProductQueryDto;
import cn.kmbeast.pojo.entity.Product;

import java.util.List;
import java.util.Map;

/**
 * 商品业务逻辑接口
 */
public interface ProductService {

    Result<String> save(Product product);

    Result<String> update(Product product);

    Result<String> batchDelete(List<Integer> ids);

    Result<List<Product>> query(ProductQueryDto productQueryDto);

    Result<Product> getById(Integer id);

    Result<List<Product>> recommend(Integer limit);

    Result<Map<String, Object>> smartSearch(String keyword, Integer current, Integer size);
}
