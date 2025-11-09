package com.atguigu.lease.web.admin.controller.apartment;


import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.AttrKey;
import com.atguigu.lease.model.entity.AttrValue;
import com.atguigu.lease.web.admin.service.AttrKeyService;
import com.atguigu.lease.web.admin.service.AttrValueService;
import com.atguigu.lease.web.admin.vo.attr.AttrKeyVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "房间属性管理")
@RestController
@RequestMapping("/admin/attr")
@RequiredArgsConstructor
public class AttrController {

    private final AttrKeyService attrKeyService;
    private final AttrValueService attrValueService;

    @Operation(summary = "新增或更新属性名称")
    @PostMapping("key/saveOrUpdate")
    public Result saveOrUpdateAttrKey(@RequestBody AttrKey attrKey) {
        boolean res = attrKeyService.saveOrUpdate(attrKey);
        return res ? Result.ok() : Result.fail();
    }

    @Operation(summary = "新增或更新属性值")
    @PostMapping("value/saveOrUpdate")
    public Result saveOrUpdateAttrValue(@RequestBody AttrValue attrValue) {
        boolean res = attrValueService.saveOrUpdate(attrValue);
        return res ? Result.ok() : Result.fail();
    }


    @Operation(summary = "查询全部属性名称和属性值列表")
    @GetMapping("list")
    /**
     * 多表查询，普通 po 以及无法满足，所以有 vo
     */
    public Result<List<AttrKeyVo>> listAttrInfo()
    {
        List<AttrKeyVo> list = attrKeyService.listWithValues();
        return Result.ok(list);
    }

    @Operation(summary = "根据id删除属性名称")
    @DeleteMapping("key/deleteById")
    /**
     * 主表删除，子表级联删除
     */
    public Result removeAttrKeyById(@RequestParam Long attrKeyId) {
        boolean rm = attrKeyService.removeById(attrKeyId);
        if (rm){
            // 删除子表
            LambdaQueryWrapper<AttrValue> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AttrValue::getAttrKeyId, attrKeyId);
            attrValueService.remove(queryWrapper);
        }else{
            return Result.fail();
        }

        return Result.ok();
    }

    @Operation(summary = "根据id删除属性值")
    @DeleteMapping("value/deleteById")
    public Result removeAttrValueById(@RequestParam Long id) {
        boolean res = attrValueService.removeById(id);
        return res ? Result.ok() : Result.fail();
    }

}
