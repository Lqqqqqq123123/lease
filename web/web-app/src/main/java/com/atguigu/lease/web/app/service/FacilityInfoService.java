package com.atguigu.lease.web.app.service;

import com.atguigu.lease.model.entity.FacilityInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author liubo
* @description 针对表【facility_info(配套信息表)】的数据库操作Service
* @createDate 2023-07-26 11:12:39
*/
public interface FacilityInfoService extends IService<FacilityInfo> {

    /**
     * 根据公寓id查询配套信息列表
     * @param id
     * @return
     */
    List<FacilityInfo> customListByApartmentId(Long id);

    /**
     * 根据房间id查询配套信息列表
     * @param id
     * @return
     */
    List<FacilityInfo> customListInfoByRoomId(Long id);
}
