package cn.kmbeast.controller;

import cn.kmbeast.aop.Protector;
import cn.kmbeast.mapper.*;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.CategoryQueryDto;
import cn.kmbeast.pojo.dto.query.extend.ProductQueryDto;
import cn.kmbeast.pojo.dto.query.extend.UserQueryDto;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * 管理后台统计控制器
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private UserMapper userMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private CategoryMapper categoryMapper;

    @Protector(role = "管理员")
    @GetMapping(value = "/statistics")
    public Result<Map<String, Object>> statistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("userCount", userMapper.queryCount(new UserQueryDto()));
        stats.put("productCount", productMapper.queryCount(new ProductQueryDto()));
        stats.put("categoryCount", categoryMapper.queryCount(new CategoryQueryDto()));

        // 饼图 — 从 DB 查询真实角色分布
        List<Map<String, Object>> roleCounts = userMapper.countByRole();
        List<Map<String, Object>> pieData = new ArrayList<>();
        for (Map<String, Object> row : roleCounts) {
            Object role = row.get("role");
            Object count = row.get("count");
            String name = "1".equals(String.valueOf(role)) ? "管理员" : "普通用户";
            pieData.add(createItem(name, toInt(count)));
        }
        // 兜底：避免空饼图
        if (pieData.isEmpty()) {
            pieData.add(createItem("管理员", 1));
            pieData.add(createItem("普通用户", 0));
        }

        // 折线图 — 近7天注册趋势
        List<Map<String, Object>> dbLineData = userMapper.countByDay();
        List<Map<String, Object>> lineData = buildLast7Days(dbLineData);

        Map<String, Object> chart = new HashMap<>();
        chart.put("pieData", pieData);
        chart.put("lineData", lineData);
        stats.put("chart", chart);

        return ApiResult.success(stats);
    }

    private List<Map<String, Object>> buildLast7Days(List<Map<String, Object>> dbData) {
        List<Map<String, Object>> result = new ArrayList<>();
        java.time.LocalDate today = java.time.LocalDate.now();
        String[] weekNames = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate date = today.minusDays(i);
            String dayName = weekNames[date.getDayOfWeek().getValue() % 7];
            int count = 0;
            for (Map<String, Object> row : dbData) {
                if (String.valueOf(row.get("day")).startsWith(date.toString())) {
                    count = ((Number) row.get("count")).intValue();
                    break;
                }
            }
            result.add(createItem(dayName, count));
        }
        return result;
    }

    private Map<String, Object> createItem(String name, Integer value) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("value", value);
        return item;
    }

    private int toInt(Object obj) {
        if (obj instanceof Number) return ((Number) obj).intValue();
        if (obj instanceof String) return Integer.parseInt((String) obj);
        return 0;
    }
}
