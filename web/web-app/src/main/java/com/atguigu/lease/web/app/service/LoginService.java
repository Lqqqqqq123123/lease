package com.atguigu.lease.web.app.service;

import com.atguigu.lease.web.app.vo.user.LoginVo;
import com.atguigu.lease.web.app.vo.user.UserInfoVo;

public interface LoginService {


    Integer sendSms(String phone) throws Exception;

    String login(LoginVo loginVo);

    UserInfoVo info(String token);
}
