package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.properties.MinioProperties;
import com.atguigu.lease.web.admin.service.FileService;
import io.minio.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {


    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioProperties minioProperties;

    @Override
    public String upload(MultipartFile file) throws Exception{
        // 1. 获取桶，如果桶不存在，则创建
        String bucketName = minioProperties.getBucketName();
        boolean exits = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());

        if(!exits){
            // 2. 创建以及设置协议
            String policy = """
                     {
                          "Statement" : [ {
                            "Action" : "s3:GetObject",
                            "Effect" : "Allow",
                            "Principal" : "*",
                            "Resource" : "arn:aws:s3:::%s/*"
                          } ],
                          "Version" : "2012-10-17"
                    }
                    """;
            policy = policy.formatted(bucketName);

            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build());
        }
        // 上传文件
        // 解决文件名重复以及按天来整合上传的文件
        String nowDay = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String objectName = nowDay + "/" + UUID.randomUUID().toString().replace("-", "") + file.getOriginalFilename();

        // uploadObject适合已经在磁盘上的文件
        // putObject 适合在内存中的文件
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .object(objectName)
                .contentType(file.getContentType())
                .build());

        // 拼接地址并返回
        String url = minioProperties.getEndpoint() + "/" + bucketName + "/" + objectName;
        return url;
    }
}


