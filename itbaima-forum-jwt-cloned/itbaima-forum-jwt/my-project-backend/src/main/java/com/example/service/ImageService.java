package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.TopicImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;

public interface ImageService extends IService<TopicImage> {
    String uploadAvatar(MultipartFile file,int id) throws IOException;
    String uploadImage(MultipartFile file,int id) throws IOException;
    void fetchAvatar(OutputStream out, String image) throws Exception;
}
