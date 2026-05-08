package cn.kmbeast.controller;

import cn.kmbeast.aop.Pager;
import cn.kmbeast.aop.Protector;
import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.ProductQueryDto;
import cn.kmbeast.pojo.entity.Product;
import cn.kmbeast.service.ProductService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Resource
    private ProductService productService;

    /**
     * 发布商品
     */
    @Protector
    @PostMapping(value = "/save")
    public Result<String> save(@RequestBody Product product) {
        return productService.save(product);
    }

    /**
     * 修改商品
     */
    @Protector
    @PutMapping(value = "/update")
    public Result<String> update(@RequestBody Product product) {
        return productService.update(product);
    }

    /**
     * 批量删除商品
     */
    @Protector(role = "管理员")
    @PostMapping(value = "/batchDelete")
    public Result<String> batchDelete(@RequestBody List<Integer> ids) {
        return productService.batchDelete(ids);
    }

    /**
     * 分页查询商品
     */
    @Pager
    @PostMapping(value = "/query")
    public Result<List<Product>> query(@RequestBody ProductQueryDto productQueryDto) {
        return productService.query(productQueryDto);
    }

    /**
     * 查询单个商品
     */
    @GetMapping(value = "/getById/{id}")
    public Result<Product> getById(@PathVariable Integer id) {
        return productService.getById(id);
    }

    /**
     * 我的商品列表
     */
    @Protector
    @Pager
    @PostMapping(value = "/myProducts")
    public Result<List<Product>> myProducts(@RequestBody ProductQueryDto productQueryDto) {
        productQueryDto.setUserId(LocalThreadHolder.getUserId());
        return productService.query(productQueryDto);
    }

    /**
     * 个性化推荐（基于浏览历史）
     */
    @GetMapping(value = "/recommend")
    public Result<List<Product>> recommend(@RequestParam(defaultValue = "8") Integer limit) {
        return productService.recommend(limit);
    }

    /**
     * 智能搜索：精确匹配 + 标签联想
     */
    @PostMapping(value = "/search")
    public Result<Map<String, Object>> search(@RequestBody Map<String, Object> body) {
        String keyword = (String) body.get("keyword");
        Integer current = body.get("current") != null ? (Integer) body.get("current") : 1;
        Integer size = body.get("size") != null ? (Integer) body.get("size") : 12;
        return productService.smartSearch(keyword, current, size);
    }
}
