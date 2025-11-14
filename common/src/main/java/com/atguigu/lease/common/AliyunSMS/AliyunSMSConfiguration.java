package com.atguigu.lease.common.AliyunSMS;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import com.atguigu.lease.common.properties.AliyunSMSProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(AliyunSMSProperties.class)
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "aliyun.sms", name = "endpoint")
@Configuration
public class AliyunSMSConfiguration {
    private final AliyunSMSProperties aliyunSMSProperties;

    @Bean("smsClient")
    public Client smsClient() throws Exception {
        Config config = new Config();
        try {
            config.setAccessKeyId(aliyunSMSProperties.getAccessKeyId());
            config.setAccessKeySecret(aliyunSMSProperties.getAccessKeySecret());
            config.setEndpoint(aliyunSMSProperties.getEndpoint());
            return new Client(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
