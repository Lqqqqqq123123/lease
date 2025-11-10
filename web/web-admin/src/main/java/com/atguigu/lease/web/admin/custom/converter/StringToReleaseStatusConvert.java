package com.atguigu.lease.web.admin.custom.converter;

import com.atguigu.lease.model.enums.ReleaseStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToReleaseStatusConvert implements Converter<String, ReleaseStatus> {
    @Override
    public ReleaseStatus convert(String source) {
        ReleaseStatus[] values = ReleaseStatus.values();
        for (var v: values){
            if(source.equals(v.getCode() + "")){
                return v;
            }
        }
        throw new RuntimeException("枚举类的值不合法，只能传入0|1");
    }
}
