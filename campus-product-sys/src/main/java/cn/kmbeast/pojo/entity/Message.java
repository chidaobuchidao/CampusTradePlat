package cn.kmbeast.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {
    private Integer id;
    // 保留旧字段兼容
    private Integer userId;
    // 私聊新增
    private Integer senderId;
    private Integer receiverId;
    private Integer productId;
    private String content;
    private Boolean isRead;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}