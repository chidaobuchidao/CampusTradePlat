package cn.kmbeast.mapper;

import cn.kmbeast.pojo.entity.Message;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MessageMapper {
    int save(Message msg);
    List<Message> getChat(@Param("userId") Integer userId, @Param("targetId") Integer targetId, @Param("productId") Integer productId);
    List<Map<String, Object>> getConversations(@Param("userId") Integer userId);
    int getUnreadCount(@Param("userId") Integer userId);
    int markRead(@Param("userId") Integer userId, @Param("senderId") Integer senderId, @Param("productId") Integer productId);

    int markAllRead(@Param("userId") Integer userId);
}
