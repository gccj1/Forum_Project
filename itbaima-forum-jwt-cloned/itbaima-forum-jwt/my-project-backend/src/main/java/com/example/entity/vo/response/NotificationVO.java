package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class NotificationVO {
   Integer id;
    String tittle;
    String content;
    String url;
    Date time;
}
