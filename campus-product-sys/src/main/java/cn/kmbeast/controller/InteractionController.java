package cn.kmbeast.controller;

import cn.kmbeast.aop.Protector;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.entity.Interaction;
import cn.kmbeast.service.InteractionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户行为控制器
 */
@RestController
@RequestMapping("/interaction")
public class InteractionController {

    @Resource
    private InteractionService interactionService;

    /**
     * 切换收藏状态
     */
    @Protector
    @PostMapping(value = "/toggleSave/{productId}")
    public Result<String> toggleSave(@PathVariable Integer productId) {
        return interactionService.toggleSave(productId);
    }

    /**
     * 记录浏览
     */
    @Protector
    @PostMapping(value = "/recordView/{productId}")
    public Result<String> recordView(@PathVariable Integer productId) {
        return interactionService.recordView(productId);
    }

    /**
     * 我的收藏列表
     */
    @Protector
    @GetMapping(value = "/mySave")
    public Result<List<Interaction>> mySave(@RequestParam(defaultValue = "1") Integer current,
                                             @RequestParam(defaultValue = "10") Integer size) {
        return interactionService.getMySave(current, size);
    }

    /**
     * 我的浏览历史
     */
    @Protector
    @GetMapping(value = "/myView")
    public Result<List<Interaction>> myView(@RequestParam(defaultValue = "1") Integer current,
                                             @RequestParam(defaultValue = "10") Integer size) {
        return interactionService.getMyView(current, size);
    }
}
