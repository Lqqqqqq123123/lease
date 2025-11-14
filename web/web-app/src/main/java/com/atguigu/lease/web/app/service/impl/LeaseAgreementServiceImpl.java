package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.utils.JwtUtil;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.app.mapper.*;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.service.LeaseAgreementService;
import com.atguigu.lease.web.app.service.RoomInfoService;
import com.atguigu.lease.web.app.service.UserInfoService;
import com.atguigu.lease.web.app.vo.agreement.AgreementDetailVo;
import com.atguigu.lease.web.app.vo.agreement.AgreementItemVo;
import com.atguigu.lease.web.app.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.atguigu.lease.web.app.vo.ltb.OtherVo;
import com.atguigu.lease.web.app.vo.room.RoomDetailVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.jsonwebtoken.Claims;
import io.minio.credentials.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【lease_agreement(租约信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
@RequiredArgsConstructor
public class LeaseAgreementServiceImpl extends ServiceImpl<LeaseAgreementMapper, LeaseAgreement>
        implements LeaseAgreementService {

    private final UserInfoService userInfoService;
    private final ApartmentInfoService apartmentInfoService;
    private final RoomInfoService roomInfoService;

    private final LeaseAgreementMapper leaseAgreementMapper;

    @Override
    public List<AgreementItemVo> customListItem(String token) {
        // 1. 先获取用户信息
        Claims claims = JwtUtil.parseToken(token);
        Long userId = claims.get("userId", Long.class);
        UserInfo userInfo = userInfoService.getById(userId);
        String username = userInfo.getNickname();

        //2. 根据用户名称查询租约信息
        return leaseAgreementMapper.customListItem(username);

    }

    @Override
    @Transactional
    public AgreementDetailVo customGetDetailById(Long id) {
        // 1. 创建 vo
        AgreementDetailVo vo = new AgreementDetailVo();

        // 2. 查询租约信息
        LeaseAgreement leaseAgreement = leaseAgreementMapper.selectById(id);
        BeanUtils.copyProperties(leaseAgreement, vo);

        // 3. 查询公寓信息 复用公寓模块的接口
        ApartmentDetailVo apartmentDetailVo = apartmentInfoService.customGetDetailById(leaseAgreement.getApartmentId());
        vo.setApartmentName(apartmentDetailVo.getName());
        vo.setApartmentGraphVoList(apartmentDetailVo.getGraphVoList());

        // 4. 查询房间信息，直接去房间模块的接口先写好
        RoomDetailVo roomDetailVo = roomInfoService.customGetDetailById(vo.getRoomId());
        vo.setRoomGraphVoList(roomDetailVo.getGraphVoList());
        vo.setRoomNumber(roomDetailVo.getRoomNumber());

        // 5. 查询支付方式、租期月数、租期单位
        OtherVo otherVo = leaseAgreementMapper.getOtherVoById(id);

        BeanUtils.copyProperties(otherVo, vo);

        return vo;
    }
}




