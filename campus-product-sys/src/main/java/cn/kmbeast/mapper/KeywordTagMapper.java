package cn.kmbeast.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface KeywordTagMapper {
    Map<String, Object> matchKeyword(@Param("keyword") String keyword);
}
