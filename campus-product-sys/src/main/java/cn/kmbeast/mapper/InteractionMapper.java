package cn.kmbeast.mapper;

import cn.kmbeast.pojo.entity.Interaction;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户行为持久化接口
 */
public interface InteractionMapper {

    int save(Interaction interaction);

    int delete(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("actionType") Integer actionType);

    List<Interaction> query(@Param("userId") Integer userId, @Param("actionType") Integer actionType, @Param("current") Integer current, @Param("size") Integer size);

    int queryCount(@Param("userId") Integer userId, @Param("actionType") Integer actionType);

    Interaction getByUserAndProduct(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("actionType") Integer actionType);

    List<Integer> getBrowsedProductIds(@Param("userId") Integer userId, @Param("limit") int limit);
}
