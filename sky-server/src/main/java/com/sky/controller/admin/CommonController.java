package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
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

@RestController
@RequestMapping("/admin/common")
@Api(tags = "公共相关接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){
        log.info("开始上传文件：{}", file.getOriginalFilename());

        //先获取文件的后缀名
        String fix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        //生成随机文件名
        String fileName = UUID.randomUUID().toString() + fix;
        //上传文件到OSS
        try {
            String url = aliOssUtil.upload(file.getBytes(), fileName);
            return Result.success(url);
        } catch (IOException e) {
            log.info("文件上传失败", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
