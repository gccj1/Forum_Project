package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("db_topic_type")
public class TopicType {
    Integer id;
    String name;
    String content;
}
