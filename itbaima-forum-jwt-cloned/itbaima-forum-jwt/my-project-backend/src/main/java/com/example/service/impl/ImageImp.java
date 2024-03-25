package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.dto.TopicImage;
import com.example.mapper.AccountMapper;
import com.example.mapper.TopicImageMapper;
import com.example.service.ImageService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import io.minio.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
@Service
@Slf4j
public class ImageImp extends ServiceImpl<TopicImageMapper, TopicImage> implements ImageService {
    @Resource
    MinioClient minioClient;
    @Resource
    AccountMapper mapper;
    @Resource
    FlowUtils flowUtils;

       SimpleDateFormat format= new SimpleDateFormat("yyyyMMdd");
    @Override
    public String uploadImage(MultipartFile file, int id) throws IOException {
            String key= Const.LIMIT_IMAGE+id;
            if(!flowUtils.limitCounterCheck(key,20,3600)) return null;
            String imageName=UUID.randomUUID().toString().replace("-","");
        Date date = new Date();
        imageName="/image/"+format.format(date) +"/"+imageName;
        PutObjectArgs put = PutObjectArgs
                .builder()
                .bucket("study")
                .stream(file.getInputStream(), file.getSize(), -1)
                .object(imageName)
                .build();
        try {
            minioClient.putObject(put);
            if (save(new TopicImage(id,imageName,date))) {
                return imageName;
            }
            return null;
        } catch (Exception e) {
            log.error("上传头像失败", e);
            return null;
        }

    }

    @Override
    public String uploadAvatar(MultipartFile file, int id) throws IOException {
        String imageName = UUID.randomUUID().toString().replace("-", "");
        imageName = "/avatar/" + imageName;
        PutObjectArgs put = PutObjectArgs
                .builder()
                .bucket("study")
                .stream(file.getInputStream(), file.getSize(), -1)
                .object(imageName)
                .build();
        try {
            minioClient.putObject(put);
            String avatar = mapper.selectById(id).getAvatar();
            deleteAvatar(avatar);
            if (mapper.update(null, Wrappers.<Account>update().eq("uid", id).set("avatar", imageName)) > 0) {
                return imageName;
            }
            return null;
        } catch (Exception e) {
            log.error("上传头像失败", e);
            return null;
        }
    }
    private void deleteAvatar(String imageName) throws Exception{
        if(imageName==null||imageName.isEmpty())
            return;
        RemoveObjectArgs build = RemoveObjectArgs.builder().bucket("study").object(imageName).build();
        minioClient.removeObject(build);
    }

    @Override
    public void fetchAvatar(OutputStream out,String image) throws Exception{
        GetObjectArgs build = GetObjectArgs
                            .builder()
                            .bucket("study")
                            .object(image)
                            .build();
        try (GetObjectResponse response = minioClient.getObject(build)) {
            IOUtils.copy(response, out);
        }

    }
}