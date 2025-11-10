package com.atguigu.lease.web.admin.custom.config;

import com.atguigu.lease.web.admin.custom.converter.StringToItemTypeConverter;
import com.atguigu.lease.web.admin.custom.converter.StringToReleaseStatusConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Autowired
    StringToItemTypeConverter stringToItemTypeConverter;
    @Autowired
    StringToReleaseStatusConvert stringToReleaseStatusConvert;


    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToItemTypeConverter);
        registry.addConverter(stringToReleaseStatusConvert);
    }
}
