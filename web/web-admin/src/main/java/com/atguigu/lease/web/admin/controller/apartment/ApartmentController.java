package com.atguigu.lease.web.admin.controller.apartment;


import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.ApartmentFacility;
import com.atguigu.lease.model.entity.ApartmentFeeValue;
import com.atguigu.lease.model.entity.ApartmentInfo;
import com.atguigu.lease.model.enums.ReleaseStatus;
import com.atguigu.lease.web.admin.service.ApartmentFacilityService;
import com.atguigu.lease.web.admin.service.ApartmentInfoService;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@Tag(name = "公寓信息管理")
@RestController
@RequestMapping("/admin/apartment")
@RequiredArgsConstructor
public class ApartmentController {

    private final ApartmentInfoService apartmentInfoService;
    private final ApartmentFacilityService apartmentFacilityService;

    @Operation(summary = "保存或更新公寓信息")
    @PostMapping("saveOrUpdate")
    /**
     * 在多表业务场景下，涉及到更新的话，往往是中间表直接删除然后再去新增，单表还是正常更新
     */
    public Result saveOrUpdate(@RequestBody ApartmentSubmitVo vo) {
       boolean res = apartmentInfoService.customSaveOrUpdate(vo);
       return res ? Result.ok() : Result.fail();
    }

    @Operation(summary = "根据条件分页查询公寓列表")
    @GetMapping("pageItem")
    /**
     * 1. 分页查询：记得配置 mybatis-plus 的分页插件
     * 2. 条件可以为空，所以是动态拼接sql，在mybatis-plus中，多表的动态sql还是得自己去写（不是，这里看你查的是什么，如果是单表数据，可以用mybatis-plus的查询方法）
     */
    public Result<IPage<ApartmentItemVo>> pageItem(@RequestParam long current, @RequestParam long size, ApartmentQueryVo queryVo) {
        IPage<ApartmentItemVo> page = new Page<>(current, size);
        apartmentInfoService.customPage(page, queryVo);
        return Result.ok(page);
    }

    @Operation(summary = "根据ID获取公寓详细信息")
    @GetMapping("getDetailById")
    /**
     * 当前查询是一个多表查询，最后将结果分装到写好的 Vo 即可
     */
    public Result<ApartmentDetailVo> getDetailById(@RequestParam Long id) {
        ApartmentDetailVo vo = apartmentInfoService.getDetailById(id);
        return Result.ok();
    }

    @Operation(summary = "根据id删除公寓信息")
    @DeleteMapping("removeById")
    /**
     * 删除：多表删除，需要先删除中间表数据，再删除单表数据
     */
    public Result removeById(@RequestParam Long id) {
       boolean res = apartmentInfoService.customRemoveById(id);
       return res ? Result.ok() : Result.fail();
    }

    @Operation(summary = "根据id修改公寓发布状态")
    @PostMapping("updateReleaseStatusById")
    /**
     * 分析：单表修改，直接用 mybatis plus 的修改方法即可
     */
    public Result updateReleaseStatusById(@RequestParam Long id, @RequestParam ReleaseStatus status) {

        // 直接用 updateWrapper
        LambdaUpdateWrapper<ApartmentInfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ApartmentInfo::getId, id);
        wrapper.set(ApartmentInfo::getIsRelease, status);
        boolean res = apartmentInfoService.update(wrapper);
        return res ? Result.ok() : Result.fail();
    }

    @Operation(summary = "根据区县id查询公寓信息列表")
    @GetMapping("listInfoByDistrictId")
    /**
     * 分析：单表查询，直接用 mybatis plus 的查询方法即可
     */
    public Result<List<ApartmentInfo>> listInfoByDistrictId(@RequestParam Long id) {
        List<ApartmentInfo> list = new ArrayList<>();
        LambdaQueryWrapper<ApartmentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApartmentInfo::getDistrictId, id);
        list = apartmentInfoService.list(wrapper);
        return Result.ok(list);
    }
}














