package com.atguigu.lease.web.admin.controller.lease;


import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.ViewAppointment;
import com.atguigu.lease.model.enums.AppointmentStatus;
import com.atguigu.lease.web.admin.service.ViewAppointmentService;
import com.atguigu.lease.web.admin.vo.appointment.AppointmentQueryVo;
import com.atguigu.lease.web.admin.vo.appointment.AppointmentVo;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kotlin.jvm.internal.Lambda;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Tag(name = "预约看房管理")
@RequestMapping("/admin/appointment")
@RestController
@RequiredArgsConstructor
public class ViewAppointmentController {

    private final ViewAppointmentService viewAppointmentService;
    @Operation(summary = "分页查询预约信息")
    @GetMapping("page")
    public Result<IPage<AppointmentVo>> page(@RequestParam long current, @RequestParam long size, AppointmentQueryVo queryVo) {
        IPage<AppointmentVo> page = new Page<>();
        page.setSize(current); page.setSize(size);
        viewAppointmentService.customPage(page, queryVo);
        return Result.ok(page);
    }

    @Operation(summary = "根据id更新预约状态")
    @PostMapping("updateStatusById")
    public Result updateStatusById(@RequestParam Long id, @RequestParam AppointmentStatus status) {
        LambdaUpdateWrapper<ViewAppointment> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ViewAppointment::getId, id);
        wrapper.set(ViewAppointment::getAppointmentStatus, status);
        viewAppointmentService.update(wrapper);
        return Result.ok();
    }

}
