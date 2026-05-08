package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.MessageMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.entity.Message;
import cn.kmbeast.service.MessageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageMapper messageMapper;

    @Override
    public Result<String> send(Integer receiverId, Integer productId, String content) {
        Integer senderId = LocalThreadHolder.getUserId();
        if (senderId.equals(receiverId)) return ApiResult.error("不能给自己发消息");
        Message msg = new Message();
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setProductId(productId != null ? productId : 0);
        msg.setContent(content);
        messageMapper.save(msg);
        return ApiResult.success("发送成功");
    }

    @Override
    public Result<List<Message>> getChat(Integer targetId, Integer productId) {
        Integer userId = LocalThreadHolder.getUserId();
        List<Message> list = messageMapper.getChat(userId, targetId, productId);
        return ApiResult.success(list);
    }

    @Override
    public Result<List<Map<String, Object>>> getConversations() {
        Integer userId = LocalThreadHolder.getUserId();
        List<Map<String, Object>> list = messageMapper.getConversations(userId);
        return ApiResult.success(list);
    }

    @Override
    public Result<Integer> getUnreadCount() {
        Integer userId = LocalThreadHolder.getUserId();
        return ApiResult.success(messageMapper.getUnreadCount(userId));
    }

    @Override
    public Result<String> markRead(Integer senderId, Integer productId) {
        messageMapper.markRead(LocalThreadHolder.getUserId(), senderId, productId);
        return ApiResult.success();
    }

    @Override
    public Result<String> markAllRead() {
        messageMapper.markAllRead(LocalThreadHolder.getUserId());
        return ApiResult.success();
    }
}
