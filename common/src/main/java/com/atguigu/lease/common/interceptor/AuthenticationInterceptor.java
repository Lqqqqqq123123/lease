package com.atguigu.lease.common.interceptor;

import com.atguigu.lease.common.Exception.BusinessException;
import com.atguigu.lease.common.properties.AuthenticationProperties;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.common.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.checkerframework.checker.units.qual.C;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取token
        String token = request.getHeader("access_token");

        // 2. 检查有无 token
        if(ObjectUtils.isEmpty(token)){
            throw new BusinessException(ResultCodeEnum.ADMIN_LOGIN_AUTH);
        }
        // 3. 验证token
        JwtUtil.parseToken(token);
        return true;
    }
}
