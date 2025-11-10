package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.FacilityInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.lease.model.entity.ApartmentFacility;
import com.atguigu.lease.web.admin.service.ApartmentFacilityService;
import com.atguigu.lease.web.admin.mapper.ApartmentFacilityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
* @author liubo
* @description 针对表【apartment_facility(公寓&配套关联表)】的数据库操作Service实现
* @createDate 2023-07-24 15:48:00
*/
@Service
@RequiredArgsConstructor
public class ApartmentFacilityServiceImpl extends ServiceImpl<ApartmentFacilityMapper, ApartmentFacility>
    implements ApartmentFacilityService{

    private final ApartmentFacilityMapper apartmentFacilityMapper;
    @Override
    public List<FacilityInfo> listFacilityInfoByApartmentId(Long id) {
        return apartmentFacilityMapper.listFacilityInfoByApartmentId(id);
    }
}




