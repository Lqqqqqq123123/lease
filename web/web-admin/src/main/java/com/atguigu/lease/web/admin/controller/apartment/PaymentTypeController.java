package com.atguigu.lease.web.admin.controller.apartment;


import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.PaymentType;
import com.atguigu.lease.web.admin.service.PaymentTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "支付方式管理")
@RequestMapping("/admin/payment")
@RestController
@RequiredArgsConstructor
public class PaymentTypeController {


    private final PaymentTypeService paymentTypeService;

    @Operation(summary = "查询全部支付方式列表")
    @GetMapping("list")
    /**
     * 分析：单表查询，直接用 mybatis plus 的查询方法即可
     * 逻辑删除：只需要在逻辑删除字段上添加 @TableLogic， mybatis plus 会自动处理
     */
    public Result<List<PaymentType>> list() {
        List<PaymentType> list = paymentTypeService.list();
        return Result.ok(list);
    }

    @Operation(summary = "保存或更新支付方式")
    @PostMapping("saveOrUpdate")
    /**
     * 分析：单表保存或更新，直接用 mybatis plus 的保存或更新方法即可
     * 问题：
     * 1）逻辑删除字段没有默认值，导致修改时会把之前添加的记录忽略（因为mybatis-plus在修改前会先查一下，当他一查，发现当前要求改的对象的id不存在
     * ，就变成新增了，实际上是存在的，是不过它的逻辑字段是空，导致了mybatis-plus没查出来），从而导致重复添加
     * 解决方法：
     * 2）由于我们用了 @JsonIgnore忽略了一些字段，所以这些字段也不会从前端接受，所以后端还得处理这些字段（创建时间、修改时间、逻辑删除字段）
     */
    public Result saveOrUpdatePaymentType(@RequestBody PaymentType paymentType) {
        boolean res = paymentTypeService.saveOrUpdate(paymentType);
        return res ? Result.ok() : Result.fail();
    }

    @Operation(summary = "根据ID删除支付方式")
    @DeleteMapping("deleteById")
    public Result deletePaymentById(@RequestParam Long id) {
        boolean res = paymentTypeService.removeById(id);
        return res ? Result.ok() : Result.fail();
    }

}















