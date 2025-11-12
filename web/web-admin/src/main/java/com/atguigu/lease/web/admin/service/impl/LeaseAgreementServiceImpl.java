package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.Exception.BusinessException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.ApartmentInfo;
import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.entity.LeaseTerm;
import com.atguigu.lease.model.entity.RoomInfo;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.LeaseAgreementService;
import com.atguigu.lease.web.admin.vo.apartment.agreement.AgreementQueryVo;
import com.atguigu.lease.web.admin.vo.apartment.agreement.AgreementVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author liubo
 * @description 针对表【lease_agreement(租约信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
@RequiredArgsConstructor
public class LeaseAgreementServiceImpl extends ServiceImpl<LeaseAgreementMapper, LeaseAgreement>
        implements LeaseAgreementService {

    private final LeaseAgreementMapper leaseAgreementMapper;
    private final ApartmentInfoMapper apartmentInfoMapper;
    private final RoomInfoMapper roomInfoMapper;
    private final LeaseTermMapper leaseTermMapper;
    private final PaymentTypeMapper paymentTypeMapper;
    @Override
    public void customPage(IPage<AgreementVo> page, AgreementQueryVo queryVo) {
        // 调用mapper
        leaseAgreementMapper.customPage(page, queryVo);
    }

    @Override
    public AgreementVo customGetDetailById(Long id) {
        // 0. 创建一个vo对象
        AgreementVo vo = new AgreementVo();
        // 1. 先查出租约信息
        LeaseAgreement leaseAgreement = leaseAgreementMapper.selectById(id);
        BeanUtils.copyProperties(leaseAgreement, vo);
        //2. 在根据 公寓id 查出公寓信息
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(vo.getApartmentId());
        vo.setApartmentInfo(apartmentInfo);

        // 3. 根据 房间id 查出房间信息
        RoomInfo roomInfo = roomInfoMapper.selectById(vo.getRoomId());
        vo.setRoomInfo(roomInfo);

        // 4. 根据 租期id 查出租期信息
        LeaseTerm leaseTerm = leaseTermMapper.selectById(vo.getLeaseTermId());
        vo.setLeaseTerm(leaseTerm);

        // 5. 根据 支付方式id 查出支付方式信息
        vo.setPaymentType(paymentTypeMapper.selectById(vo.getPaymentTypeId()));

        return vo;
    }

    @Override
    public void customRemoveById(Long id) {
        // 单表删除，但是要看当前id是否存在，也就是能够删除成功
        int row =  leaseAgreementMapper.deleteById(id);
        if(row == 0){
            throw new BusinessException(ResultCodeEnum.ID_NOT_EXIST_NOT_DELETE);
        }

    }
}




