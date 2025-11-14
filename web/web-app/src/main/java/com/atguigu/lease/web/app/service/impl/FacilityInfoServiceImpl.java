package com.atguigu.lease.web.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.lease.model.entity.FacilityInfo;
import com.atguigu.lease.web.app.service.FacilityInfoService;
import com.atguigu.lease.web.app.mapper.FacilityInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author liubo
* @description 针对表【facility_info(配套信息表)】的数据库操作Service实现
* @createDate 2023-07-26 11:12:39
*/
@Service
@RequiredArgsConstructor
public class FacilityInfoServiceImpl extends ServiceImpl<FacilityInfoMapper, FacilityInfo>
    implements FacilityInfoService{

    private final FacilityInfoMapper facilityInfoMapper;
    @Override
    public List<FacilityInfo> customListByApartmentId(Long id) {
        return facilityInfoMapper.customListByApartmentId(id);
    }
}




