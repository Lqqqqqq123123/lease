package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.utils.JwtUtil;
import com.atguigu.lease.model.entity.GraphInfo;
import com.atguigu.lease.model.entity.ViewAppointment;
import com.atguigu.lease.web.app.mapper.ViewAppointmentMapper;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.service.GraphInfoService;
import com.atguigu.lease.web.app.service.ViewAppointmentService;
import com.atguigu.lease.web.app.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.app.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.app.vo.appointment.AppointmentDetailVo;
import com.atguigu.lease.web.app.vo.appointment.AppointmentItemVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.jsonwebtoken.Claims;
import kotlin.jvm.internal.Lambda;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【view_appointment(预约看房信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
@RequiredArgsConstructor
public class ViewAppointmentServiceImpl extends ServiceImpl<ViewAppointmentMapper, ViewAppointment>
        implements ViewAppointmentService {

    private final ViewAppointmentMapper viewAppointmentMapper;

    private final ApartmentInfoService apartmentInfoService;
    private final GraphInfoService graphInfoService;

    @Override
    public List<AppointmentItemVo> listItem(String  token) {
        // 获取用户id
        Claims claims = JwtUtil.parseToken(token);
        Long userId = claims.get("userId", Long.class);
        return viewAppointmentMapper.listItem(userId);
    }

    @Override
    public AppointmentDetailVo customGetDetailById(Long id) {
        // 0. 创建返回对象
        AppointmentDetailVo vo = new AppointmentDetailVo();

        // 1. 查询预约信息
        LambdaQueryWrapper<ViewAppointment> wr1 = new LambdaQueryWrapper<>();
        ViewAppointment appointment = viewAppointmentMapper.selectById(id);

        // 2. 将 appointment 赋值给 vo
        BeanUtils.copyProperties(appointment, vo);


        // 3. 查询公寓信息（这里复用公寓模块的接口）
        ApartmentDetailVo apartmentDetailVo = apartmentInfoService.customGetDetailById(vo.getApartmentId());
        ApartmentItemVo apartmentItemVo = new ApartmentItemVo();
        BeanUtils.copyProperties(apartmentDetailVo, apartmentItemVo);
        // 3.1 获取公寓的图片
        LambdaQueryWrapper<GraphInfo> wr2 = new LambdaQueryWrapper<>();
        wr2.eq(GraphInfo::getItemId, vo.getApartmentId());
        wr2.eq(GraphInfo::getItemType, 1);
        List<GraphInfo> graphList = graphInfoService.list(wr2);
        vo.setApartmentItemVo(apartmentItemVo);
        vo.getApartmentItemVo().setGraphVoList(graphList);
        return vo;
    }
}




