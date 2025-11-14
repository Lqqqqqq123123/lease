package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.web.app.mapper.*;
import com.atguigu.lease.web.app.service.*;
import com.atguigu.lease.web.app.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.app.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.app.vo.attr.AttrValueVo;
import com.atguigu.lease.web.app.vo.fee.FeeValueVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.atguigu.lease.web.app.vo.room.RoomDetailVo;
import com.atguigu.lease.web.app.vo.room.RoomItemVo;
import com.atguigu.lease.web.app.vo.room.RoomQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import kotlin.jvm.internal.Lambda;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {

    // 注入其他Service
    private final ApartmentInfoService apartmentInfoService;
    private final GraphInfoService graphInfoService;
    private final AttrValueService attrValueService;
    private final FacilityInfoService facilityInfoService;
    private final LabelInfoService labelInfoService;
    private final PaymentTypeService paymentTypeService;
    private final FeeValueService feeValueService;
    private final LeaseTermService leaseTermService;

    private final RoomInfoMapper roomInfoMapper;


    @Override
    @Transactional
    // todo 实际上为了能够查询逻辑删除后的房间，在查询房间信息时候应该用自定义方法去查，而不是mybatisplus的getById方法
    public RoomDetailVo customGetDetailById(Long id) {
        // 1. 创建 vo 对象
        RoomDetailVo vo = new RoomDetailVo();
        // 2. 查询 room_info 表
        RoomInfo roomInfo = this.getById(id);
        BeanUtils.copyProperties(roomInfo, vo);

        // 2. 查询所属公寓信息 复用之前查询公寓详细信息的接口

        ApartmentDetailVo apartmentDetailVo = apartmentInfoService.customGetDetailById(vo.getApartmentId());
        ApartmentItemVo apartmentItemVo = new ApartmentItemVo();
        // 2.1 注意，detail 里面的图片列表是 graph_vo, 而item 里面是 graphinfo，所以需要单独处理
        BeanUtils.copyProperties(apartmentDetailVo, apartmentItemVo);
        LambdaQueryWrapper<GraphInfo> graphInfoQueryWrapper = new LambdaQueryWrapper<>();
        graphInfoQueryWrapper.eq(GraphInfo::getItemId, apartmentDetailVo.getId()).eq(GraphInfo::getItemType, ItemType.APARTMENT);
        // 2.2 查询该公寓的所有图片信息
        List<GraphInfo> graphInfoList = graphInfoService.list(graphInfoQueryWrapper);

        // 2.3 赋值给 apartmentItemVo
        apartmentItemVo.setGraphVoList(graphInfoList);
        vo.setApartmentItemVo(apartmentItemVo);
        // 2.4 查询该房间对应的图片列表
        // 策略：先查出 po -- >vo
        LambdaQueryWrapper<GraphInfo> wr1 = new LambdaQueryWrapper<>();
        wr1.eq(GraphInfo::getItemId, id).eq(GraphInfo::getItemType, ItemType.ROOM);
        List<GraphVo> graphVoList = graphInfoService.list(wr1)
                .stream()
                .map(po ->{
                    GraphVo g = new GraphVo();
                    BeanUtils.copyProperties(po, g);
                    return g;
                }).collect(Collectors.toList());
        vo.setGraphVoList(graphVoList);

        // 2.5 查询该房间对应的属性信息列表 （连表查询，故自定义方法）
        List<AttrValueVo> attrValueVoList = attrValueService.customAttrValueVoList(id);
        vo.setAttrValueVoList(attrValueVoList);

        // 2.6 查询该房间对应的配套信息列表
        List<FacilityInfo> facilityInfoList = facilityInfoService.customListInfoByRoomId(id);
        vo.setFacilityInfoList(facilityInfoList);
        // 2.7 查询该房间对应的标签信息列表
        List<LabelInfo> labelInfoList = labelInfoService.customListByRoomId(id);
        vo.setLabelInfoList(labelInfoList);
        // 2.8 查询该房间对应的杂费信息列表
        // todo 数据库没这张表，去你的吧，不写了

        // 2.9 获取该房间对应的租期信息列表
        List<LeaseTerm> leaseTermList = leaseTermService.customListByRoomId(id);
        vo.setLeaseTermList(leaseTermList);

        // 2.10 获取该房间对应的支付方式列表
        List<PaymentType> paymentTypeList = paymentTypeService.customListByRoomId(id);
        vo.setPaymentTypeList(paymentTypeList);
        // 2.11 检查房间是否被删除
        if(vo.getIsDeleted() == 1) vo.setIsDelete(true);
        else vo.setIsDelete(false);

        // 2.12 获取该房间是否入住
        // todo 先随便写了，这里的是否入住特别奇怪，应该是看有没有该房间相关的租约还没到期
        vo.setIsCheckIn(false);
        return vo;

    }
}




