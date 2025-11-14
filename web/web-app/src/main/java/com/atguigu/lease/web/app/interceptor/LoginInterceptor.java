package com.atguigu.lease.web.app.interceptor;

import com.atguigu.lease.common.Exception.BusinessException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.common.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("access_token");

        // 1. 验证token
        if(token == null){
            throw new BusinessException(ResultCodeEnum.APP_LOGIN_AUTH);
        }

        // 2.解析 token
        JwtUtil.parseToken(token);

        return true;

    }
}
