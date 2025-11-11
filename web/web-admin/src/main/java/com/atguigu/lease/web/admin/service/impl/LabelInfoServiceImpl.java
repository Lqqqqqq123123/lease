package com.atguigu.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.lease.model.entity.LabelInfo;
import com.atguigu.lease.web.admin.service.LabelInfoService;
import com.atguigu.lease.web.admin.mapper.LabelInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author liubo
* @description 针对表【label_info(标签信息表)】的数据库操作Service实现
* @createDate 2023-07-24 15:48:00
*/
@Service
@RequiredArgsConstructor
public class LabelInfoServiceImpl extends ServiceImpl<LabelInfoMapper, LabelInfo>
    implements LabelInfoService{

    private final LabelInfoMapper labelInfoMapper;

    @Override
    public List<LabelInfo> listLabelInfoByRoomId(Long id) {
        return labelInfoMapper.listLabelInfoByRoomId(id);
    }
}




