package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.Exception.BusinessException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.ApartmentInfoMapper;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.atguigu.lease.web.admin.vo.fee.FeeValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.util.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
@RequiredArgsConstructor
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {

    private final ApartmentInfoMapper apartmentInfoMapper;

    private final ApartmentFacilityService apartmentFacilityService;
    private final ApartmentFeeValueService apartmentFeeValueService;
    private final ApartmentLabelService apartmentLabelService;
    private final GraphInfoService graphInfoService;

    // 地区信息 service
    private final ProvinceInfoService provinceInfoService;
    private final CityInfoService cityInfoService;
    private final DistrictInfoService districtInfoService;

    // 房间信息service
    private final RoomInfoService roomInfoService;

    private final FacilityInfoService facilityInfoService;
    @Override
    public boolean customSaveOrUpdate(ApartmentSubmitVo vo) {

        boolean isUpdate = vo.getId() != null;

        // 先更新公寓主表,在更新之前，先看看前端有没有传地区信息，还是只传了id
        if(vo.getProvinceId() != null){
            // 看看地区 name 是否为空
            if (!StringUtils.hasText(vo.getProvinceName())){
                // 根据省id获取省份名称
                ProvinceInfo provinceInfo = provinceInfoService.getById(vo.getProvinceId());
                vo.setProvinceName(provinceInfo.getName());
            }

            if (!StringUtils.hasText(vo.getCityName())){
                // 根据城市id获取城市名称
                CityInfo cityInfo = cityInfoService.getById(vo.getCityId());
                vo.setCityName(cityInfo.getName());
            }
            if (!StringUtils.hasText(vo.getDistrictName())){
                // 根据区域id获取区域名称
                DistrictInfo districtInfo = districtInfoService.getById(vo.getDistrictId());
                vo.setDistrictName(districtInfo.getName());
            }
        }

        // 然后再去更新或者插入
        saveOrUpdate(vo);

        Long id = vo.getId();
        if(isUpdate){
            // 先去删除旧数据
            // 删除公寓配套表
            LambdaQueryWrapper<ApartmentFacility> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(ApartmentFacility::getApartmentId, vo.getId());
            apartmentFacilityService.remove(queryWrapper1);
            // 删除公寓杂费值表
            LambdaQueryWrapper<ApartmentFeeValue> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(ApartmentFeeValue::getApartmentId, vo.getId());
            apartmentFeeValueService.remove(queryWrapper2);

            // 删除公寓标签表
            LambdaQueryWrapper<ApartmentLabel> queryWrapper3 = new LambdaQueryWrapper<>();
            queryWrapper3.eq(ApartmentLabel::getApartmentId, vo.getId());
            apartmentLabelService.remove(queryWrapper3);

            // 公寓图片表删除
            LambdaQueryWrapper<GraphInfo> queryWrapper4 = new LambdaQueryWrapper<>();
            queryWrapper4.eq(GraphInfo::getItemId, vo.getId()).eq(GraphInfo::getItemType, 1);
            graphInfoService.remove(queryWrapper4);
        }

        // 更新或者添加中间表以及图片表
        // 先遍历配套
        List<Long> facilityInfoIds = vo.getFacilityInfoIds();
        if(facilityInfoIds != null  && facilityInfoIds.size() > 0){
            List<ApartmentFacility> apartmentFacilities = new ArrayList<>();
            for(Long facilityId : facilityInfoIds){
                ApartmentFacility build = ApartmentFacility.builder().apartmentId(id).facilityId(facilityId).build();
                apartmentFacilities.add(build);
            }
            // 批量添加
            apartmentFacilityService.saveBatch(apartmentFacilities);
        }

        // 遍历杂费值
        List<Long> feeValueIds = vo.getFeeValueIds();
        if(!CollectionUtils.isEmpty(feeValueIds)){
            List<ApartmentFeeValue> apartmentFeeValues = new ArrayList<>();
            for(Long feeValueId : feeValueIds){
                apartmentFeeValues.add(ApartmentFeeValue.builder().apartmentId(id).feeValueId(feeValueId).build());
            }
            // 批量添加
            apartmentFeeValueService.saveBatch(apartmentFeeValues);
        }


        // 遍历标签
        List<Long> labelIds = vo.getLabelIds();
        if(!CollectionUtils.isEmpty(labelIds)){
            List<ApartmentLabel> apartmentLabels = new ArrayList<>();
            for(Long labelId : labelIds){
                apartmentLabels.add(ApartmentLabel.builder().apartmentId(id).labelId(labelId).build());
            }
            // 批量添加
            apartmentLabelService.saveBatch(apartmentLabels);
        }

        // 遍历图片
        List<GraphVo> graphVoList = vo.getGraphVoList();
        if(!CollectionUtils.isEmpty(graphVoList)){
            List<GraphInfo> graphInfos = new ArrayList<>();
            for(GraphVo graphVo : vo.getGraphVoList()){
                GraphInfo g = new GraphInfo();
                g.setName(graphVo.getName());
                g.setUrl(graphVo.getUrl());
                g.setItemType(ItemType.APARTMENT);
                g.setItemId(id);
                graphInfos.add(g);
            }
            graphInfoService.saveBatch(graphInfos);
        }

        return true;
    }

    @Override

    public ApartmentDetailVo getDetailById(Long id) {
        ApartmentDetailVo vo = new ApartmentDetailVo();
        // 1. 先查询公寓信息
        ApartmentInfo apartmentInfo = getById(id);
        BeanUtils.copyProperties(apartmentInfo, vo);

        // 2. 查询公寓配套信息
        List<FacilityInfo> facilityInfoList = apartmentFacilityService.listFacilityInfoByApartmentId(id);
        vo.setFacilityInfoList(facilityInfoList);
        // 3. 查询公寓杂费信息
        List<FeeValueVo> feeValueVos = apartmentFeeValueService.listOfFeeValueVoByApartmentId(id);
        vo.setFeeValueVoList(feeValueVos);

        // 4. 查询公寓标签信息
        List<LabelInfo> labelInfoList = apartmentLabelService.listLabelInfoByApartmentId(id);
        vo.setLabelInfoList(labelInfoList);

        // 5. 获取公寓图片信息
        List<GraphVo> graphVos = new ArrayList<>();
        LambdaQueryWrapper<GraphInfo> graphWrapper = new LambdaQueryWrapper<>();
        graphWrapper.eq(GraphInfo::getItemId, id).eq(GraphInfo::getItemType, ItemType.APARTMENT);
        List<GraphInfo> graphList = graphInfoService.list(graphWrapper);

        // 5.1 遍历查出来的po，然后转vo
        for (var g : graphList){
            GraphVo graphVo = new GraphVo();
            BeanUtils.copyProperties(g, graphVo);
            graphVos.add(graphVo);
        }
        vo.setGraphVoList(graphVos);
        // 6. 封装vo
        return vo;
    }

    @Override
    public boolean customRemoveById(Long id) {

        // 在删除之前，需要检查该公寓是否还有房间
        LambdaQueryWrapper<RoomInfo> roomWrapper = new LambdaQueryWrapper<>();
        roomWrapper.eq(RoomInfo::getApartmentId, id);
        long count = roomInfoService.count(roomWrapper);
        if(count > 0){
            // 如果为空，抛出业务异常 DELETE_ERROR(207, "请先删除子集"),
            throw new BusinessException(ResultCodeEnum.DELETE_ERROR);
        }

        // 删除公寓配套表
        LambdaQueryWrapper<ApartmentFacility> facilityWrapper = new LambdaQueryWrapper<>();
        facilityWrapper.eq(ApartmentFacility::getApartmentId, id);
        apartmentFacilityService.remove(facilityWrapper);
        // 删除公寓费用表
        LambdaQueryWrapper<ApartmentFeeValue> feeWrapper = new LambdaQueryWrapper<>();
        feeWrapper.eq(ApartmentFeeValue::getApartmentId, id);
        apartmentFeeValueService.remove(feeWrapper);
        // 删除公寓标签表
        LambdaQueryWrapper<ApartmentLabel> labelWrapper = new LambdaQueryWrapper<>();
        labelWrapper.eq(ApartmentLabel::getApartmentId, id);
        apartmentLabelService.remove(labelWrapper);
        // 删除公寓图片表
        LambdaQueryWrapper<GraphInfo> graphWrapper = new LambdaQueryWrapper<>();
        graphWrapper.eq(GraphInfo::getItemId, id).eq(GraphInfo::getItemType, ItemType.APARTMENT);
        graphInfoService.remove(graphWrapper);
        // 删除公寓表
        return removeById(id);
    }

    /**
     * 自定义公寓信息的分页查询，由于结果没有 po 直接与 数据表映射，故自己写xml文件
     * @param page:分页参数
     * @param queryVo
     * @return
     */
    @Override
    public IPage<ApartmentItemVo> customPage(IPage page, ApartmentQueryVo queryVo) {
        return apartmentInfoMapper.customPage(page, queryVo);
    }
}




