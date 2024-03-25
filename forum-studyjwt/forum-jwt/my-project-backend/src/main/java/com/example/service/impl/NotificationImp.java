package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.TopicNotification;
import com.example.entity.vo.response.NotificationVO;
import com.example.mapper.NotificationMapper;
import com.example.service.NotificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationImp extends ServiceImpl<NotificationMapper, TopicNotification> implements NotificationService {

    @Override
    public List<NotificationVO> getAllNotifications(int uid) {
        List<TopicNotification> topicNotifications = baseMapper.selectList(Wrappers.lambdaQuery(TopicNotification.class)
                .eq(TopicNotification::getUid, uid));
        return topicNotifications.stream().map(list -> {
            NotificationVO notificationVO = new NotificationVO();
            BeanUtils.copyProperties(list, notificationVO);
            return notificationVO;
        }).toList();
    }

    @Override
    public void addNotification(int uid, NotificationVO vo) {
        TopicNotification topicNotification = new TopicNotification();
        BeanUtils.copyProperties(vo, topicNotification);
        topicNotification.setUid(uid);
        baseMapper.insert(topicNotification);
    }

    @Override
    public void  deleteAllNotifications(int uid) {
        boolean deleted = remove(Wrappers.<TopicNotification>query().eq("uid", uid));
        return ;
    }

    @Override
    public void deleteOneNotification(int id, int uid) {
        boolean deleted = remove(Wrappers.<TopicNotification>query().eq("id", id).eq("uid", uid));
        return ;
    }
}
