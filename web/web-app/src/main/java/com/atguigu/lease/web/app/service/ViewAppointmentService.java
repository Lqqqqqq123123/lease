package com.atguigu.lease.web.app.service;

import com.atguigu.lease.model.entity.ViewAppointment;
import com.atguigu.lease.web.app.vo.appointment.AppointmentDetailVo;
import com.atguigu.lease.web.app.vo.appointment.AppointmentItemVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author liubo
* @description 针对表【view_appointment(预约看房信息表)】的数据库操作Service
* @createDate 2023-07-26 11:12:39
*/
public interface ViewAppointmentService extends IService<ViewAppointment> {

    /**
     * 获取预约看房列表
     * @return
     */
    // todo 这里实际上是哪个用户登录，就去用token获取用户id，然后获取当前用户的预约看房列表
    List<AppointmentItemVo> listItem(String  token);

    /**
     * 获取预约看房详情
     * @param id
     * @return
     */
    AppointmentDetailVo customGetDetailById(Long id);
}
