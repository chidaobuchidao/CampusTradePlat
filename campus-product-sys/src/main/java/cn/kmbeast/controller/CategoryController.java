package cn.kmbeast.controller;

import cn.kmbeast.aop.Pager;
import cn.kmbeast.aop.Protector;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.CategoryQueryDto;
import cn.kmbeast.pojo.entity.Category;
import cn.kmbeast.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品类别控制器
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 新增
     *
     * @param category 参数
     * @return Result<String> 响应结果
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public Result<String> save(@RequestBody Category category) {
        return categoryService.save(category);
    }

    /**
     * 修改
     *
     * @param category 参数
     * @return Result<String> 响应结果
     */
    @PutMapping(value = "/update")
    @ResponseBody
    public Result<String> update(@RequestBody Category category) {
        return categoryService.update(category);
    }

    /**
     * 批量删除
     */
    @Protector(role = "管理员") // 只有管理员能够去进行操作
    @PostMapping(value = "/batchDelete")
    @ResponseBody
    public Result<String> batchDelete(@RequestBody List<Integer> ids) {
        return categoryService.batchDelete(ids);
    }

    /**
     * 查询
     *
     * @param categoryQueryDto 查询参数
     * @return Result<List < Category>> 响应结果
     */
    @Pager
    @PostMapping(value = "/query")
    @ResponseBody
    public Result<List<Category>> query(@RequestBody CategoryQueryDto categoryQueryDto) {
        return categoryService.query(categoryQueryDto);
    }
}