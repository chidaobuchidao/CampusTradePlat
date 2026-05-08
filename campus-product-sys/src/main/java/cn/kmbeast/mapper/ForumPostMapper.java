package cn.kmbeast.mapper;

import cn.kmbeast.pojo.dto.query.extend.ForumPostQueryDto;
import cn.kmbeast.pojo.entity.ForumPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ForumPostMapper {
    int save(ForumPost post);
    int update(ForumPost post);
    int delete(@Param("id") Integer id);
    ForumPost getById(Integer id);
    List<ForumPost> query(ForumPostQueryDto dto);
    int queryCount(ForumPostQueryDto dto);
}
