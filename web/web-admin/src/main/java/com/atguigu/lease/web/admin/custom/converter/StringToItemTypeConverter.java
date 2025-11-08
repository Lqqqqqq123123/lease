package com.atguigu.lease.web.admin.custom.converter;

import com.atguigu.lease.model.enums.ItemType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/***
 * @Description: 将字符串转换成ItemType枚举
 * @Author: ltb
 */
@Component
public class StringToItemTypeConverter implements Converter<String, ItemType> {
    @Override
    public ItemType convert(String code) {
        // 根据 code 返回 ItemType
        ItemType[] values = ItemType.values();
        for (ItemType itemType : values) {
            if (code.equals(itemType.getCode() + "")){
                return itemType;
            }
        }

        throw new RuntimeException("枚举类的值不合法，只能传入1|2");
    }
}
