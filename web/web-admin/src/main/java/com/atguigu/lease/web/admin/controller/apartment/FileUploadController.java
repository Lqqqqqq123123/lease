package com.atguigu.lease.web.admin.controller.apartment;


import com.atguigu.lease.common.Exception.BusinessException;
import com.atguigu.lease.common.Exception.BussinessExceptionEnum;
import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.web.admin.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;


@Tag(name = "文件管理")
@RequestMapping("/admin/file")
@RestController
@RequiredArgsConstructor
public class FileUploadController {

    private final FileService fileService;
    @Operation(summary = "上传文件")
    @PostMapping("upload")
    public Result<String> upload(@RequestPart MultipartFile file) throws Exception {

        String url = fileService.upload(file);
        return Result.ok(url);
    }

}
