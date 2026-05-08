package cn.kmbeast.controller;

import cn.kmbeast.aop.Pager;
import cn.kmbeast.aop.Protector;
import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.ForumPostQueryDto;
import cn.kmbeast.pojo.entity.ForumPost;
import cn.kmbeast.service.ForumPostService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/forum")
public class ForumPostController {

    @Resource
    private ForumPostService forumPostService;

    @Pager
    @PostMapping("/query")
    public Result<List<ForumPost>> query(@RequestBody ForumPostQueryDto dto) {
        return forumPostService.query(dto);
    }

    @GetMapping("/detail/{id}")
    public Result<ForumPost> detail(@PathVariable Integer id) {
        return forumPostService.getById(id);
    }

    @Protector
    @PostMapping("/save")
    public Result<String> save(@RequestBody ForumPost post) {
        return forumPostService.save(post);
    }

    @Protector
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        return forumPostService.delete(id);
    }

    @Protector
    @Pager
    @PostMapping("/myPosts")
    public Result<List<ForumPost>> myPosts(@RequestBody ForumPostQueryDto dto) {
        dto.setUserId(LocalThreadHolder.getUserId());
        return forumPostService.query(dto);
    }
}
