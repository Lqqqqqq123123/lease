package com.atguigu.lease.web.admin.controller.apartment;


import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.FeeKey;
import com.atguigu.lease.model.entity.FeeValue;
import com.atguigu.lease.web.admin.service.FeeKeyService;
import com.atguigu.lease.web.admin.service.FeeValueService;
import com.atguigu.lease.web.admin.vo.fee.FeeKeyVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "房间杂费管理")
@RestController
@RequestMapping("/admin/fee")
@RequiredArgsConstructor
public class FeeController {

    private final FeeKeyService feeKeyService;;
    private final FeeValueService feeValueService;


    @Operation(summary = "保存或更新杂费名称")
    @PostMapping("key/saveOrUpdate")
    public Result saveOrUpdateFeeKey(@RequestBody FeeKey feeKey) {
        boolean res = feeKeyService.saveOrUpdate(feeKey);
        return res ? Result.ok() : Result.fail();
    }

    @Operation(summary = "保存或更新杂费值")
    @PostMapping("value/saveOrUpdate")
    public Result saveOrUpdateFeeValue(@RequestBody FeeValue feeValue) {
        boolean res = feeValueService.saveOrUpdate(feeValue);
        return res ? Result.ok() : Result.fail();
    }


    @Operation(summary = "查询全部杂费名称和杂费值列表")
    @GetMapping("list")
    /**
     * 多表查询，普通 po 以及无法满足，所以有 vo
     */
    public Result<List<FeeKeyVo>> feeInfoList() {
        List<FeeKeyVo> list = feeKeyService.listWithValues();
        return Result.ok(list);
    }

    @Operation(summary = "根据id删除杂费名称")
    @DeleteMapping("key/deleteById")
    public Result deleteFeeKeyById(@RequestParam Long feeKeyId) {
        boolean res = feeKeyService.removeById(feeKeyId);
        if(res){
            // 删除子表
            LambdaQueryWrapper<FeeValue> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FeeValue::getFeeKeyId, feeKeyId);
            feeValueService.remove(queryWrapper);
            return Result.ok();
        }
        return Result.fail();
    }

    @Operation(summary = "根据id删除杂费值")
    @DeleteMapping("value/deleteById")
    public Result deleteFeeValueById(@RequestParam Long id) {
        boolean res = feeValueService.removeById(id);
        return res ? Result.ok() : Result.fail();
    }
}
