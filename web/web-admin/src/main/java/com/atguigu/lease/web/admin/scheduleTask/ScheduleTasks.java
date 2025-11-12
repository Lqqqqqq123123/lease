package com.atguigu.lease.web.admin.scheduleTask;

import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.web.admin.service.LeaseAgreementService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class ScheduleTasks {

    private final LeaseAgreementService leaseAgreementService;

    @Scheduled(cron = "0 0 0 * * *")
    public void updateLeaseAgreementTask() {
        // 更新租约信息
        LambdaUpdateWrapper<LeaseAgreement> wrapper = new LambdaUpdateWrapper<>();
        wrapper .lt(LeaseAgreement::getLeaseEndDate, new Date())
                .in(LeaseAgreement::getStatus, 2, 5, 7);
        wrapper.set(LeaseAgreement::getStatus, LeaseStatus.EXPIRED);
        leaseAgreementService.update(wrapper);
    }
}
