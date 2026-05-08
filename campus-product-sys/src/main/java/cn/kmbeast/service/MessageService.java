package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.entity.Message;

import java.util.List;
import java.util.Map;

public interface MessageService {
    Result<String> send(Integer receiverId, Integer productId, String content);
    Result<List<Message>> getChat(Integer targetId, Integer productId);
    Result<List<Map<String, Object>>> getConversations();
    Result<Integer> getUnreadCount();
    Result<String> markRead(Integer senderId, Integer productId);

    Result<String> markAllRead();
}
