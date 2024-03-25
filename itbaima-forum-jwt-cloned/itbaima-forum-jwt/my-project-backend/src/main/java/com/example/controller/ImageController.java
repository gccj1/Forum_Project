package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.ImageService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/images")
public class ImageController {
    @Resource
    ImageService imageService;
    @PostMapping("/avatar")
    public RestBean<String> uploadAvatar(@RequestParam("file") MultipartFile file,
                                         @RequestAttribute(Const.ATTR_USER_ID) int id) throws IOException {
        if(file.getSize()> 1024*1024){
            return RestBean.failure(400,"文件大小超过限制1M");
        }
        log.info("上传头像，用户ID: {}大小{}", id,file.getSize());
        String imageUrl=imageService.uploadAvatar(file,id);
        return imageUrl==null? RestBean.failure(400,"上传头像失败"): RestBean.success(imageUrl);
    }
    @PostMapping("/cache")
    public RestBean<String> images(@RequestParam("file") MultipartFile file,
                                         @RequestAttribute(Const.ATTR_USER_ID) int id) throws IOException {
        if(file.getSize()> 1024*1024*5){
            return RestBean.failure(400,"文件大小超过限制5M");
        }
        log.info("上传图片，用户ID: {}大小{}", id,file.getSize());
        String imageUrl=imageService.uploadImage(file,id);
        return imageUrl==null? RestBean.failure(400,"上传图片失败"): RestBean.success(imageUrl);
    }
}
