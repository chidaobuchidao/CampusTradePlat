package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.InteractionMapper;
import cn.kmbeast.mapper.KeywordTagMapper;
import cn.kmbeast.mapper.ProductMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.PageResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.ProductQueryDto;
import cn.kmbeast.pojo.em.RoleEnum;
import cn.kmbeast.pojo.entity.Product;
import cn.kmbeast.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductMapper productMapper;
    @Resource
    private InteractionMapper interactionMapper;
    @Resource
    private KeywordTagMapper keywordTagMapper;

    @Override
    public Result<String> save(Product product) {
        if (!StringUtils.hasText(product.getName())) {
            return ApiResult.error("商品名不能为空");
        }
        if (!StringUtils.hasText(product.getDetail())) {
            return ApiResult.error("商品描述不能为空");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return ApiResult.error("商品价格需大于0");
        }
        if (!StringUtils.hasText(product.getCoverList())) {
            return ApiResult.error("请上传商品封面图");
        }
        product.setUserId(LocalThreadHolder.getUserId());
        product.setCreateTime(LocalDateTime.now());
        productMapper.save(product);
        return ApiResult.success("商品发布成功");
    }

    @Override
    public Result<String> update(Product product) {
        Product dbProduct = productMapper.getById(product.getId());
        if (dbProduct == null) {
            return ApiResult.error("商品不存在");
        }
        Integer currentUserId = LocalThreadHolder.getUserId();
        Integer currentRoleId = LocalThreadHolder.getRoleId();
        if (!Objects.equals(dbProduct.getUserId(), currentUserId)
                && !Objects.equals(currentRoleId, RoleEnum.ADMIN.getRole())) {
            return ApiResult.error("无操作权限");
        }
        productMapper.update(product);
        return ApiResult.success("商品修改成功");
    }

    @Override
    public Result<String> batchDelete(List<Integer> ids) {
        productMapper.batchDelete(ids);
        return ApiResult.success("商品删除成功");
    }

    @Override
    public Result<List<Product>> query(ProductQueryDto productQueryDto) {
        int totalCount = productMapper.queryCount(productQueryDto);
        List<Product> productList = productMapper.query(productQueryDto);
        return PageResult.success(productList, totalCount);
    }

    @Override
    public Result<Product> getById(Integer id) {
        Product product = productMapper.getById(id);
        if (product == null) {
            return ApiResult.error("商品不存在");
        }
        return ApiResult.success(product);
    }

    @Override
    public Result<List<Product>> recommend(Integer limit) {
        List<Product> result;
        Integer userId = LocalThreadHolder.getUserId();
        if (userId != null) {
            // 获取用户最近浏览的商品ID
            List<Integer> browsedIds = interactionMapper.getBrowsedProductIds(userId, 20);
            if (!browsedIds.isEmpty()) {
                // 获取浏览过商品的分类
                Set<Integer> categoryIds = new HashSet<>();
                for (Integer pid : browsedIds) {
                    Product p = productMapper.getById(pid);
                    if (p != null && p.getCategoryId() != null) categoryIds.add(p.getCategoryId());
                }
                // 推荐同分类商品（排除已浏览的）
                if (!categoryIds.isEmpty()) {
                    result = productMapper.recommendByCategories(
                        new ArrayList<>(categoryIds), browsedIds, limit);
                    if (!result.isEmpty()) return ApiResult.success(result);
                }
            }
        }
        // 兜底：返回最新商品
        result = productMapper.recommendByCategories(new ArrayList<>(), new ArrayList<>(), limit);
        return ApiResult.success(result);
    }

    @Override
    public Result<Map<String, Object>> smartSearch(String keyword, Integer current, Integer size) {
        Map<String, Object> response = new HashMap<>();

        // 1. 精确匹配：搜索商品名+描述
        ProductQueryDto exactDto = new ProductQueryDto();
        exactDto.setKey(keyword);
        exactDto.setCurrent((current - 1) * size);
        exactDto.setSize(size);
        List<Product> exactList = productMapper.query(exactDto);
        int exactTotal = productMapper.queryCount(exactDto);
        response.put("exact", exactList);
        response.put("exactTotal", exactTotal);

        // 2. 关键词→分类映射
        Map<String, Object> tagMatch = keywordTagMapper.matchKeyword(keyword);
        Integer relatedCategoryId = null;
        String tagName = null;
        if (tagMatch != null) {
            relatedCategoryId = (Integer) tagMatch.get("categoryId");
            tagName = (String) tagMatch.get("tagName");
        }
        // 3. 兜底：若无映射，用精确匹配中出现最多的分类
        if (relatedCategoryId == null && !exactList.isEmpty()) {
            Map<Integer, Long> freq = exactList.stream()
                .filter(p -> p.getCategoryId() != null)
                .collect(Collectors.groupingBy(Product::getCategoryId, Collectors.counting()));
            relatedCategoryId = freq.entrySet().stream()
                .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        }

        if (relatedCategoryId != null) {
            if (tagName == null) tagName = "相关";
            response.put("tag", tagName);
            List<Integer> excludeIds = exactList.stream().map(Product::getId).collect(Collectors.toList());
            List<Product> related = productMapper.recommendByCategories(
                Collections.singletonList(relatedCategoryId), excludeIds, 4);
            response.put("related", related);
        }
        return ApiResult.success(response);
    }
}
