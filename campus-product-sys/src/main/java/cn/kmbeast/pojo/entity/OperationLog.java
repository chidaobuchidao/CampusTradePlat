package cn.kmbeast.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日志
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperationLog {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 操作用户ID
     */
    private Integer userId;
    /**
     * 操作内容
     */
    private String content;
    /**
     * 操作时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
