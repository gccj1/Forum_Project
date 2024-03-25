package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.TopicNotification;
import com.example.entity.vo.response.NotificationVO;

import java.util.List;

public interface NotificationService extends IService<TopicNotification> {
    public List<NotificationVO> getAllNotifications(int uid);

    public void addNotification(int uid, NotificationVO vo);
    public void deleteAllNotifications(int uid);
    public void deleteOneNotification(int id,int uid);
}
