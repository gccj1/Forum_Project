package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.ImageService;
import io.minio.errors.ErrorResponseException;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ObjectController {
    @Resource
    ImageService imageService;

    @GetMapping("/images/avatar/**")
    public void getAvatar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        fetchImage(request,response);
    }
    @GetMapping("/images/image/**")
    public void getTopicImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        fetchImage(request,response);
    }
    private void fetchImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String imagePath=request.getServletPath().substring(7);
        ServletOutputStream outputStream = response.getOutputStream();
        if(imagePath.length()<=13){
            outputStream.println(RestBean.failure(404,"图片不存在").toString());
        }else{
            try {
                imageService.fetchAvatar(outputStream,imagePath);
                response.setHeader("Cache-Control", "max-age=2592000");
            } catch (ErrorResponseException e) {
                if( e.response().code()==404){
                    outputStream.println(RestBean.failure(404,"图片不存在").toString());
                }
                else log.error("图片获取失败",e);
            }
        }
    }
}
