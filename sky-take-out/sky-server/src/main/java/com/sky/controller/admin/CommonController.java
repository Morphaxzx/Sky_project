package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@Api(tags = "通用接口")
@RequestMapping("/admin/common")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;

    @ApiOperation("文件上传方法")
    @PostMapping("/upload")
    public Result<String> logout(MultipartFile file)   {
        try {
            String extension = file.getOriginalFilename().split("\\.")[1];
            String name  = UUID.randomUUID().toString() + "." +extension;
            return Result.success(aliOssUtil.upload(file.getBytes(), name));
        } catch (IOException e) {
            log.error("文件上传失败");
        }
        return Result.error("文件上传失败");
    }
}
