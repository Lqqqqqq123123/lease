package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.Exception.BusinessException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.ApartmentInfoMapper;
import com.atguigu.lease.web.admin.mapper.AttrValueMapper;
import com.atguigu.lease.web.admin.mapper.RoomInfoMapper;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.attr.AttrValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.atguigu.lease.web.admin.vo.room.RoomDetailVo;
import com.atguigu.lease.web.admin.vo.room.RoomItemVo;
import com.atguigu.lease.web.admin.vo.room.RoomQueryVo;
import com.atguigu.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liutianba
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createData 2025-11-11 11:23
 */
@Service
@RequiredArgsConstructor
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {

    private final RoomFacilityService roomFacilityService;
    private final RoomLabelService roomLabelService;
    private final RoomAttrValueService roomAttrValueService;
    private final RoomLeaseTermService roomLeaseTermService;
    private final RoomPaymentTypeService roomPaymentTypeService;
    private final GraphInfoService graphInfoService;
    private final LeaseAgreementService leaseAgreementService;
    private final FacilityInfoService facilityInfoService;
    private final LabelInfoService labelInfoService;
    private final PaymentTypeService paymentTypeService;
    private final LeaseTermService leaseTermService;

    private final RoomInfoMapper roomInfoMapper;
    private final AttrValueMapper attrValueMapper;
    private final ApartmentInfoMapper apartmentInfoMapper;
    @Override
    /**
     * 思路：
     * 第一步：先去判断本次操作是新增还是更新
     * 第二步：然后先保存房间信息
     * 如果是更新：先去删除中间表的所有信息
     * 之后，保存所有中间表信息以及图像信息
     */
    public boolean customSaveOrUpdate(RoomSubmitVo roomSubmitVo) {
        // 1.先去判断本次操作是新增还是更新
        boolean isUpdate = roomSubmitVo.getId() != null;
        // 2.然后先保存房间信息
        saveOrUpdate(roomSubmitVo);
        Long id = roomSubmitVo.getId();
        // 3. 如果是更新：先去删除中间表的所有信息
        if(isUpdate){
            // 3.1 删除房间配套信息
            LambdaQueryWrapper<RoomFacility> wr1 = new LambdaQueryWrapper<>();
            wr1.eq(RoomFacility::getRoomId, id);
            roomFacilityService.remove(wr1);

            // 3.2 删除房间标签信息
            LambdaQueryWrapper<RoomLabel> wr2 = new LambdaQueryWrapper<>();
            wr2.eq(RoomLabel::getRoomId, id);
            roomLabelService.remove(wr2);

            // 3.3 删除房间属性值信息
            LambdaQueryWrapper<RoomAttrValue> wr3 = new LambdaQueryWrapper<>();
            wr3.eq(RoomAttrValue::getRoomId, id);
            roomAttrValueService.remove(wr3);

            // 3.4 删除房间租期信息
            LambdaQueryWrapper<RoomLeaseTerm> wr4 = new LambdaQueryWrapper<>();
            wr4.eq(RoomLeaseTerm::getRoomId, id);
            roomLeaseTermService.remove(wr4);

            // 3.5 删除房间付款方式信息
            LambdaQueryWrapper<RoomPaymentType> wr5 = new LambdaQueryWrapper<>();
            wr5.eq(RoomPaymentType::getRoomId, id);
            roomPaymentTypeService.remove(wr5);

            // 3.6 删除房间图信息
            LambdaQueryWrapper<GraphInfo> wr6 = new LambdaQueryWrapper<>();
            wr6.eq(GraphInfo::getItemId, id).eq(GraphInfo::getItemType, ItemType.ROOM);
            graphInfoService.remove(wr6);
        }

         // 4. 保存所有中间表信息
         // 4.1 保存房间配套信息
        List<Long> facilityInfoIds = roomSubmitVo.getFacilityInfoIds();
        if(!CollectionUtils.isEmpty(facilityInfoIds)){
            List<RoomFacility> list1 = new ArrayList<>();
            for(Long facilityInfoId : facilityInfoIds){
                RoomFacility roomFacility = RoomFacility.builder().facilityId(facilityInfoId).roomId(id).build();
                list1.add(roomFacility);
            }
            roomFacilityService.saveBatch(list1);
        }

        // 4.2 保存房间标签信息
        List<Long>labelInfoIds = roomSubmitVo.getLabelInfoIds();
        if(!CollectionUtils.isEmpty(labelInfoIds)){
            roomLabelService.saveBatch(labelInfoIds.stream()
                    .map((label_id)->{
                        return RoomLabel.builder().labelId(label_id).roomId(id).build();})
                    .collect(Collectors.toList()));
        }
        // 4.3 保存房间属性值信息
        List<Long> attrValueIds = roomSubmitVo.getAttrValueIds();
        if(!CollectionUtils.isEmpty(attrValueIds)){
            roomAttrValueService.saveBatch(attrValueIds.stream()
                            .map((attr_value_id)->{
                                return RoomAttrValue.builder().attrValueId(attr_value_id).roomId(id).build();})
                            .collect(Collectors.toList()));
        }

        // 4.4 保存房间租期信息
        List<Long> leaseTermIds = roomSubmitVo.getLeaseTermIds();
        if(!CollectionUtils.isEmpty(leaseTermIds)){
            roomLeaseTermService.saveBatch(leaseTermIds.stream()
                    .map((lease_term_id)->{
                        return RoomLeaseTerm.builder().leaseTermId(lease_term_id).roomId(id).build();})
                    .collect(Collectors.toList()));
        }

        // 4.5 保存房间付款方式信息
        List<Long> paymentTypeIds = roomSubmitVo.getPaymentTypeIds();
        if(!CollectionUtils.isEmpty(paymentTypeIds)){
            roomPaymentTypeService.saveBatch(paymentTypeIds.stream()
                    .map((payment_type_id)->{
                        return RoomPaymentType.builder().roomId(id).paymentTypeId(payment_type_id).build();})
                    .collect(Collectors.toList())
            );
        }

        // 4.6 批量保存房间图信息
        List<GraphVo> graphVoList = roomSubmitVo.getGraphVoList();
        if(!CollectionUtils.isEmpty(graphVoList)){
            graphInfoService.saveBatch(graphVoList.stream()
                            .map((graphVo)->{
                                GraphInfo g = new GraphInfo();
                                g.setUrl(graphVo.getUrl());
                                g.setName(graphVo.getName());
                                g.setItemId(id);
                                g.setItemType(ItemType.ROOM);
                                return g;
                            })
                            .collect(Collectors.toList())
                    );
        }

        return true;

    }

    @Override
    /**
     * 多表查询，查出来的结果直接封装到 RoomItemVo 即可
     */
    public void customPage(IPage<RoomItemVo> page, RoomQueryVo queryVo) {
        roomInfoMapper.customPage(page, queryVo);
    }

    @Override
    public void customRemoveById(Long id) {
        // 1. 先去判断该房间是不是还在出租，如果在出租，就不能删除
        // 1. 先获得当前房间信息
        RoomInfo roomInfo = roomInfoMapper.selectById(id);
        LambdaQueryWrapper<LeaseAgreement> wr = new LambdaQueryWrapper<>();
        wr.eq(LeaseAgreement::getRoomId, id);
        wr.eq(LeaseAgreement::getApartmentId, roomInfo.getApartmentId());
        List<Integer> status = new ArrayList();
        status.add(2); status.add(5); status.add(7);
        wr.in(LeaseAgreement::getStatus, status);
        if(leaseAgreementService.count(wr) > 0){
            throw new BusinessException(ResultCodeEnum.ROOM_HAS_LEASE);
        }

        // 2. 如果可以删除，就删除
        // 2.1 删除房间信息
        roomInfoMapper.deleteById(id);

        // 2.2 删除房间配套信息
        LambdaQueryWrapper<RoomFacility> wr1 = new LambdaQueryWrapper<>();
        wr1.eq(RoomFacility::getRoomId, id);
        roomFacilityService.remove(wr1);

        // 2.3 删除房间标签信息
        LambdaQueryWrapper<RoomLabel> wr2 = new LambdaQueryWrapper<>();
        wr2.eq(RoomLabel::getRoomId, id);
        roomLabelService.remove(wr2);

        // 3.4 删除房间属性值信息
        LambdaQueryWrapper<RoomAttrValue> wr3 = new LambdaQueryWrapper<>();
        wr3.eq(RoomAttrValue::getRoomId, id);
        roomAttrValueService.remove(wr3);

        // 3.5 删除房间租期信息
        LambdaQueryWrapper<RoomLeaseTerm> wr4 = new LambdaQueryWrapper<>();
        wr4.eq(RoomLeaseTerm::getRoomId, id);
        roomLeaseTermService.remove(wr4);

        // 3.6 删除房间付款方式信息
        LambdaQueryWrapper<RoomPaymentType> wr5 = new LambdaQueryWrapper<>();
        wr5.eq(RoomPaymentType::getRoomId, id);
        roomPaymentTypeService.remove(wr5);

        // 3.7 删除房间图信息
        LambdaQueryWrapper<GraphInfo> wr6 = new LambdaQueryWrapper<>();
        wr6.eq(GraphInfo::getItemId, id).eq(GraphInfo::getItemType, ItemType.ROOM);
        graphInfoService.remove(wr6);

    }

    @Override
    public RoomDetailVo customGetDetailById(Long id) {
        RoomDetailVo vo = new RoomDetailVo();
        // 1. 先查询房间信息，然后复制给vo
        RoomInfo roominfo = roomInfoMapper.selectById(id);
        BeanUtils.copyProperties(roominfo, vo);

        // 2. 再去查询该房间所属的公寓信息，然后赋值给vo
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(vo.getApartmentId());
        vo.setApartmentInfo(apartmentInfo);

        // 3. 再去查询该房间的图片列表，然后赋值给vo
        LambdaQueryWrapper<GraphInfo> wr_graph = new LambdaQueryWrapper<>();
        wr_graph.eq(GraphInfo::getItemId, id).eq(GraphInfo::getItemType, ItemType.ROOM);
        List<GraphVo> graphVolist = graphInfoService.list(wr_graph)
                .stream()
                .map((graphInfo)->{
                    return GraphVo.builder().name(graphInfo.getName()).url(graphInfo.getUrl()).build();
                })
                .collect(Collectors.toList());
        vo.setGraphVoList(graphVolist);

        // 4. 再去查询该房间的属性信息，然后赋值给vo
        List<AttrValueVo> attrValueVoList = attrValueMapper.listOfAttrValueVoListByRoomId(id);
        vo.setAttrValueVoList(attrValueVoList);
        // 5. 再去查询该房间的配套信息，然后赋值给vo
        List<FacilityInfo> facilityInfoList = facilityInfoService.listFacilityInfoByRoomId(id);
        vo.setFacilityInfoList(facilityInfoList);
        // 6. 再去查询该房间的标签信息，然后赋值给vo
        List<LabelInfo> labelInfoList = labelInfoService.listLabelInfoByRoomId(id);
        vo.setLabelInfoList(labelInfoList);
        // 7. 再去查询该房间的付款方式信息，然后赋值给vo
        List<PaymentType> paymentTypeList = paymentTypeService.listPaymentTypeByRoomId(id);
        vo.setPaymentTypeList(paymentTypeList);
        // 8. 再去查询该房间的可选租期信息，然后赋值给vo
        List<LeaseTerm> leaseTermList = leaseTermService.listLeaseTermByRoomId(id);
        vo.setLeaseTermList(leaseTermList);
        return vo;
    }
}




