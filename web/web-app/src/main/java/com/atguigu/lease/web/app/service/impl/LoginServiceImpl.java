package com.atguigu.lease.web.app.service.impl;


import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.atguigu.lease.common.Exception.BusinessException;
import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.common.utils.JwtUtil;
import com.atguigu.lease.model.entity.UserInfo;
import com.atguigu.lease.model.enums.BaseStatus;
import com.atguigu.lease.web.app.service.LoginService;
import com.atguigu.lease.web.app.service.UserInfoService;
import com.atguigu.lease.web.app.vo.user.LoginVo;
import com.atguigu.lease.web.app.vo.user.UserInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final Client smsClient;
    private final UserInfoService userInfoService;

    @Override
    public Integer sendSms(String phone) throws Exception {
        // 1.先校验验证码是否在有效期内，再提示不能频繁发送
        String key = RedisConstant.APP_LOGIN_PREFIX + phone;
        Object code = redisTemplate.opsForValue().get(key);

        if(code != null && !code.toString().equals("null")){
            throw new BusinessException(ResultCodeEnum.APP_SEND_SMS_TOO_OFTEN);
        }

        // 2. 发送验证码（六位的随机数字）
        Random random = new Random();
        Integer i = random.nextInt(100000, 999999);
        System.out.println("验证码：" + i);

        // 3. 存放到Redis中
        redisTemplate.opsForValue().set(key, i, RedisConstant.APP_LOGIN_CODE_TTL_SEC, TimeUnit.SECONDS);

        // 4. 新建一个请求
        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(phone);
        request.setSignName("阿里云短信测试");
        request.setTemplateCode("SMS_154950909");
        request.setTemplateParam("{\"code\":\"" + i + "\"}");

        // 5. 发送验证码
        // smsClient.sendSms(request);

        return i;
    }

    @Override
    public String login(LoginVo loginVo) {

        String code = loginVo.getCode();
        String phone = loginVo.getPhone();
        Object code_redis = redisTemplate.opsForValue().get(RedisConstant.APP_LOGIN_PREFIX + phone);
        // 1. 校验验证码是否为空
        if(ObjectUtils.isEmpty(code_redis)){
            throw new BusinessException(ResultCodeEnum.APP_LOGIN_CODE_EXPIRED);
        }

        String code_redis_str = code_redis.toString();
        // 2. 校验验证码是否一致
        if(! code_redis_str.equals(code)){
            throw new BusinessException(ResultCodeEnum.APP_LOGIN_CODE_ERROR);
        }

        // 3. 去根据手机号查询用户，去看看当前用户存在不存在，不存在就注册
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getPhone, phone);

        UserInfo user = userInfoService.getOne(wrapper);

        // 3.1 如果不存在，就注册
        if(user == null){
           user = new UserInfo();
           user.setPhone(phone);
           user.setStatus(BaseStatus.ENABLE);
           user.setNickname("用户" + phone.substring(phone.length() - 4));
           userInfoService.save(user);
           // todo 为用户设置默认头像地址
            //  user.setAvatarUrl();
        }

        // 4. 校验用户状态
        if(user.getStatus() == BaseStatus.DISABLE){
            throw new BusinessException(ResultCodeEnum.APP_ACCOUNT_DISABLED_ERROR);
        }

        // 5. 生成 token
        String token = JwtUtil.createToken(user.getId(), user.getNickname());

        return token;

    }

    @Override
    public UserInfoVo info(String  token) {
        // 1. 解析token
        Claims claims = JwtUtil.parseToken(token);

        // 2. 获取用户id
        Long id = claims.get("userId", Long.class);

        // 3. 根据用户id查询用户信息
        UserInfo userInfo = userInfoService.getById(id);

        UserInfoVo vo = new UserInfoVo(userInfo.getNickname(), userInfo.getAvatarUrl());

        return vo;
    }

}
