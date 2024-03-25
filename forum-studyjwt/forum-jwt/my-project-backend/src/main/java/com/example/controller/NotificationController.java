package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.response.NotificationVO;
import com.example.service.NotificationService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    @Resource
    NotificationService service;

    @GetMapping("/list")
    public RestBean<List<NotificationVO>> listNotification(@RequestAttribute(Const.ATTR_USER_ID) int uid) {
        return RestBean.success(service.getAllNotifications(uid));
    }

    @GetMapping("/delete")
    public RestBean<List<NotificationVO>> deleteNotification(@RequestParam @Min(0) int id,
                                                             @RequestAttribute(Const.ATTR_USER_ID) int uid) {
        service.deleteOneNotification(id, uid);
        return RestBean.success();
    }

    @GetMapping("/delete-all")
    public RestBean<List<NotificationVO>> deleteAllNotification(@RequestAttribute(Const.ATTR_USER_ID) int uid) {
        service.deleteAllNotifications(uid);
        return RestBean.success();
    }
}
