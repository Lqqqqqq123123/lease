package com.atguigu.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.lease.model.entity.FacilityInfo;
import com.atguigu.lease.web.admin.service.FacilityInfoService;
import com.atguigu.lease.web.admin.mapper.FacilityInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author liubo
* @description 针对表【facility_info(配套信息表)】的数据库操作Service实现
* @createDate 2023-07-24 15:48:00
*/
@Service
@RequiredArgsConstructor
public class FacilityInfoServiceImpl extends ServiceImpl<FacilityInfoMapper, FacilityInfo>
    implements FacilityInfoService{

    private final FacilityInfoMapper facilityInfoMapper;
    @Override
    public List<FacilityInfo> listFacilityInfoByRoomId(Long id) {
        return facilityInfoMapper.listFacilityInfoByRoomId(id);
    }
}




