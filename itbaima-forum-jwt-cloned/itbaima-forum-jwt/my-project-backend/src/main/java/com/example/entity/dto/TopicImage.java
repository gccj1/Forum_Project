package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_topic_image")
@AllArgsConstructor
public class TopicImage {
    Integer uid;
    String name;
    Date time;
}
