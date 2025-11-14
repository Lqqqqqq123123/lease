package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.ApartmentInfo;
import com.atguigu.lease.model.entity.FacilityInfo;
import com.atguigu.lease.model.entity.GraphInfo;
import com.atguigu.lease.model.entity.LabelInfo;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.app.mapper.*;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.service.FacilityInfoService;
import com.atguigu.lease.web.app.service.GraphInfoService;
import com.atguigu.lease.web.app.service.LabelInfoService;
import com.atguigu.lease.web.app.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import kotlin.jvm.internal.Lambda;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
@RequiredArgsConstructor
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {

    private final GraphInfoService graphInfoService;
    private final LabelInfoService labelInfoService;
    private final FacilityInfoService facilityInfoService;


    private final ApartmentInfoMapper apartmentInfoMapper;


    @Override
    @Transactional
    public ApartmentDetailVo customGetDetailById(Long id) {
        ApartmentDetailVo vo = new ApartmentDetailVo();
        // 1.查询公寓信息
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(id);

        BeanUtils.copyProperties(apartmentInfo, vo);


         // 2. 查询公寓图片
        LambdaQueryWrapper<GraphInfo> wr1 = new LambdaQueryWrapper<>();
        wr1.eq(GraphInfo::getItemId, id).eq(GraphInfo::getItemType, ItemType.APARTMENT);
        List<GraphVo> graphInfoList = graphInfoService.list(wr1)
                .stream()
                .map(po ->{
                    GraphVo g = new GraphVo();
                    g.setName(po.getName());
                    g.setUrl(po.getUrl());
                    return g;
                }).collect(Collectors.toList());

        vo.setGraphVoList(graphInfoList);

        // 3. 查询公寓标签
        List<LabelInfo> labelInfoList = labelInfoService.customListByApartmentId(id);
        vo.setLabelInfoList(labelInfoList);

        // 4. 获取公寓配套信息
        List<FacilityInfo> facilityInfoList = facilityInfoService.customListByApartmentId(id);
        vo.setFacilityInfoList(facilityInfoList);

        vo.setMinRent(new BigDecimal(999.99));
        vo.setIsDelete(false);
        return vo;

    }
}




