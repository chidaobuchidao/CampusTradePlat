package cn.kmbeast.Interceptor;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Token 拦截器 — 从请求头提取 token 并解析用户信息放入 ThreadLocal。
 * 不阻断请求 —— 鉴权由 @Protector AOP 切面负责。
 */
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("token");
        if (token != null) {
            Claims claims = JwtUtil.fromToken(token);
            if (claims != null) {
                Integer userId = claims.get("id", Integer.class);
                Integer roleId = claims.get("role", Integer.class);
                LocalThreadHolder.setUserId(userId, roleId);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        LocalThreadHolder.clear();
    }
}
