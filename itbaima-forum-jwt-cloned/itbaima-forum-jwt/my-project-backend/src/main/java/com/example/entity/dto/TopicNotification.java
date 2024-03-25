package com.example.entity.dto;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_notification")
public class TopicNotification {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer uid;
    String content;
    String tittle;
    String url;
    Date time;
}
