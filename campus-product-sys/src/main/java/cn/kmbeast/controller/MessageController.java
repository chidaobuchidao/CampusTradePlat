package cn.kmbeast.controller;

import cn.kmbeast.aop.Protector;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.entity.Message;
import cn.kmbeast.service.MessageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private MessageService messageService;

    @Protector
    @PostMapping("/send")
    public Result<String> send(@RequestBody Map<String, Object> body) {
        Integer receiverId = (Integer) body.get("receiverId");
        Integer productId = body.get("productId") != null ? (Integer) body.get("productId") : 0;
        String content = (String) body.get("content");
        return messageService.send(receiverId, productId, content);
    }

    @Protector
    @GetMapping("/chat/{targetId}")
    public Result<List<Message>> chat(@PathVariable Integer targetId,
                                       @RequestParam(required = false) Integer productId) {
        return messageService.getChat(targetId, productId != null ? productId : 0);
    }

    @Protector
    @GetMapping("/conversations")
    public Result<List<Map<String, Object>>> conversations() {
        return messageService.getConversations();
    }

    @Protector
    @GetMapping("/unread")
    public Result<Integer> unread() {
        return messageService.getUnreadCount();
    }

    @Protector
    @PutMapping("/read/{senderId}")
    public Result<String> markRead(@PathVariable Integer senderId,
                                    @RequestParam(required = false) Integer productId) {
        return messageService.markRead(senderId, productId != null ? productId : 0);
    }

    @Protector
    @PutMapping("/markAllRead")
    public Result<String> markAllRead() {
        return messageService.markAllRead();
    }
}
