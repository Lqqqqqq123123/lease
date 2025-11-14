package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.Exception.BusinessException;
import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.common.utils.JwtUtil;
import com.atguigu.lease.model.entity.SystemUser;
import com.atguigu.lease.model.entity.UserInfo;
import com.atguigu.lease.web.admin.service.LoginService;
import com.atguigu.lease.web.admin.service.SystemUserService;
import com.atguigu.lease.web.admin.service.UserInfoService;
import com.atguigu.lease.web.admin.vo.login.CaptchaVo;
import com.atguigu.lease.web.admin.vo.login.LoginVo;
import com.atguigu.lease.web.admin.vo.system.user.SystemUserInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import io.jsonwebtoken.Claims;
import kotlin.jvm.internal.Lambda;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final SystemUserService systemUserService;

    @Override
    public CaptchaVo getCaptcha() {
        // 1. 生成验证码
        // 三个参数分别为宽、高、位数
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 4);
        // 设置字体
        specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        // 设置类型，纯数字、纯字母、字母数字混合
        specCaptcha.setCharType(Captcha.TYPE_DEFAULT);

        String text = specCaptcha.text().toLowerCase();
        String key = RedisConstant.ADMIN_LOGIN_PREFIX + UUID.randomUUID();
        String image = specCaptcha.toBase64();
        redisTemplate.opsForValue().set(key, text, RedisConstant.ADMIN_LOGIN_CAPTCHA_TTL_SEC, TimeUnit.SECONDS);

        CaptchaVo vo = new CaptchaVo(image,  key);
        return vo;
    }

    @Override
    public String login(LoginVo loginVo) {
        // 1. 先去判断验证码是否为空
        if(loginVo.getCaptchaKey() == null){
            throw new BusinessException(ResultCodeEnum.APP_LOGIN_CODE_EMPTY);
        }

        // 2. 判断验证码是否正确以及验证码是否过期
        String key = loginVo.getCaptchaKey();  // 验证码id
        String userText = loginVo.getCaptchaCode().toLowerCase(); // 用户输入的验证码

        // 2.1 判断验证码是否过期
        if(redisTemplate.opsForValue().get(key) == null){
            throw new BusinessException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_EXPIRED);
        }
        String text = redisTemplate.opsForValue().get(key).toString();

        // 2.2 判断验证码是否正确
        if(!userText.equals(text)){
            throw new BusinessException(ResultCodeEnum.APP_LOGIN_CODE_ERROR);
        }
        // 3. 验证码正确， 去判断账号密码是否正确
        String username = loginVo.getUsername();
        String password = loginVo.getPassword();
        String passwordMd2 = DigestUtils.md5Hex(password);


        // 3.1 先去查有没有当前用户以及密码是否正确
        LambdaQueryWrapper<SystemUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemUser::getUsername, username);
        SystemUser user = systemUserService.getOne(wrapper);
        //
        if(user == null || !passwordMd2.equals(user.getPassword()) ){
            throw new BusinessException(ResultCodeEnum.ADMIN_ACCOUNT_ERROR);
        }
        // 3.2 判断账户是否被禁用
        if(user.getStatus().getCode() == 0){
            throw new BusinessException(ResultCodeEnum.ADMIN_ACCOUNT_DISABLED_ERROR);
        }

        //3.3 登录成功，生成token
        String token = JwtUtil.createToken(user.getId(), user.getUsername());
        return token;
    }

    @Override

    public SystemUserInfoVo info(String token) {
        // 1. 先去解析token，获取用户名以及id
        Claims claims = JwtUtil.parseToken(token);
        Long id = claims.get("userId", Long.class);
        String username = claims.get("username", String.class);
        // 2. 根据用户名查询用户信息
        LambdaQueryWrapper<SystemUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemUser::getUsername, username)
                .eq(SystemUser::getId, id);
        SystemUser po = systemUserService.getOne(wrapper);

        SystemUserInfoVo vo = new SystemUserInfoVo();
        vo.setName(po.getUsername());
        vo.setAvatarUrl(po.getAvatarUrl());

        return vo;

    }
}
