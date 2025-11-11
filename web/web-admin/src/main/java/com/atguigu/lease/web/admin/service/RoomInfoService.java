package com.atguigu.lease.web.admin.service;

import com.atguigu.lease.model.entity.RoomInfo;
import com.atguigu.lease.web.admin.vo.room.RoomDetailVo;
import com.atguigu.lease.web.admin.vo.room.RoomItemVo;
import com.atguigu.lease.web.admin.vo.room.RoomQueryVo;
import com.atguigu.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author liubo
* @description 针对表【room_info(房间信息表)】的数据库操作Service
* @createDate 2023-07-24 15:48:00
*/
public interface RoomInfoService extends IService<RoomInfo> {

    /**
     * 自定义保存或更新房间信息方法
     * @param roomSubmitVo
     * @return
     */
    boolean customSaveOrUpdate(RoomSubmitVo roomSubmitVo);

    /**
     * 自定义分页查询房间列表方法
     * @param page
     * @param queryVo
     * @warning 想用 mybatis plus 的分页，一堆要保证mapper层的方法的第一个参数是 page
     */

    void customPage(IPage<RoomItemVo> page, RoomQueryVo queryVo);

    /**
     * 自定义删除房间信息方法
     * @warning 删除房间信息，除了删除房间信息本身，还要删除房间的配套信息、标签信息、属性值信息、房间图片信息
     * @param id
     */
    void customRemoveById(Long id);

    /**
     * 自定义获取房间详细信息方法
     * 该查询设计若干个表，但逐步查询，最后整和到指定的vo即可！
     * @param id
     * @return
     */
    RoomDetailVo customGetDetailById(Long id);
}
