package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.ForumPostQueryDto;
import cn.kmbeast.pojo.entity.ForumPost;

import java.util.List;

public interface ForumPostService {
    Result<String> save(ForumPost post);
    Result<String> delete(Integer id);
    Result<ForumPost> getById(Integer id);
    Result<List<ForumPost>> query(ForumPostQueryDto dto);
}
