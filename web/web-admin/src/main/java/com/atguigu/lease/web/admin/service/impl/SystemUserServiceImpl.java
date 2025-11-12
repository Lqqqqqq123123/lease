package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.SystemUser;
import com.atguigu.lease.web.admin.mapper.SystemUserMapper;
import com.atguigu.lease.web.admin.service.SystemUserService;
import com.atguigu.lease.web.admin.vo.system.user.SystemUserItemVo;
import com.atguigu.lease.web.admin.vo.system.user.SystemUserQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author liubo
 * @description 针对表【system_user(员工信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
@RequiredArgsConstructor
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser>
        implements SystemUserService {

    private final SystemUserMapper systemUserMapper;
    @Override
    public void customPage(IPage<SystemUserItemVo> page, SystemUserQueryVo queryVo) {
        systemUserMapper.customPage(page, queryVo);
    }

    @Override
    public SystemUserItemVo customGetDetailById(Long id) {
        return systemUserMapper.customGetDetailById(id);
    }
}




