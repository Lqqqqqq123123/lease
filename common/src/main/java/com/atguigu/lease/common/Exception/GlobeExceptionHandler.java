package com.atguigu.lease.common.Exception;

import com.atguigu.lease.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobeExceptionHandler {

    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public Result error(Exception e){
        return Result.fail(10010, "文件大小超过限制，上传失败");
    }


    @ExceptionHandler(Throwable.class)
    public Result error(Throwable e){
        log.error("全局异常捕获", e);
        return Result.fail();
    }
}
