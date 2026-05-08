package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.ForumPostMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.PageResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.ForumPostQueryDto;
import cn.kmbeast.pojo.em.RoleEnum;
import cn.kmbeast.pojo.entity.ForumPost;
import cn.kmbeast.service.ForumPostService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ForumPostServiceImpl implements ForumPostService {

    @Resource
    private ForumPostMapper forumPostMapper;

    @Override
    public Result<String> save(ForumPost post) {
        if (!StringUtils.hasText(post.getTitle())) return ApiResult.error("标题不能为空");
        if (!StringUtils.hasText(post.getContent())) return ApiResult.error("内容不能为空");
        post.setUserId(LocalThreadHolder.getUserId());
        post.setCreateTime(LocalDateTime.now());
        if (post.getCategory() == null) post.setCategory("交流");
        forumPostMapper.save(post);
        return ApiResult.success("发帖成功");
    }

    @Override
    public Result<String> delete(Integer id) {
        ForumPost post = forumPostMapper.getById(id);
        if (post == null) return ApiResult.error("帖子不存在");
        Integer uid = LocalThreadHolder.getUserId();
        if (!Objects.equals(post.getUserId(), uid) && !Objects.equals(LocalThreadHolder.getRoleId(), RoleEnum.ADMIN.getRole())) {
            return ApiResult.error("无权删除");
        }
        forumPostMapper.delete(id);
        return ApiResult.success("删除成功");
    }

    @Override
    public Result<ForumPost> getById(Integer id) {
        ForumPost post = forumPostMapper.getById(id);
        if (post == null) return ApiResult.error("帖子不存在");
        post.setViewCount((post.getViewCount() == null ? 0 : post.getViewCount()) + 1);
        forumPostMapper.update(post);
        return ApiResult.success(post);
    }

    @Override
    public Result<List<ForumPost>> query(ForumPostQueryDto dto) {
        int total = forumPostMapper.queryCount(dto);
        List<ForumPost> list = forumPostMapper.query(dto);
        return PageResult.success(list, total);
    }
}
