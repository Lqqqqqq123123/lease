package com.atguigu.lease.web.app.service;

import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.web.app.vo.agreement.AgreementDetailVo;
import com.atguigu.lease.web.app.vo.agreement.AgreementItemVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author liubo
* @description 针对表【lease_agreement(租约信息表)】的数据库操作Service
* @createDate 2023-07-26 11:12:39
*/
public interface LeaseAgreementService extends IService<LeaseAgreement> {

    /**
     * 返回当前用户的租约列表
     * @param token
     * @return
     */
    List<AgreementItemVo> customListItem(String token);

    /**
     * 根据租约id返回租约详情信息
     * @param id
     * @return
     */
    AgreementDetailVo customGetDetailById(Long id);
}
