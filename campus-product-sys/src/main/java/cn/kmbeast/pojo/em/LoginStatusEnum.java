package cn.kmbeast.pojo.em;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录状态枚举
 */
@Getter
@AllArgsConstructor
public enum LoginStatusEnum {

    USE(false, "可登录"),
    BAN_USE(true, "登录状态异常");

    /**
     * 编码
     */
    private final Boolean flag;
    /**
     * 名称
     */
    private final String name;

}
