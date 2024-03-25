package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_topic_comment")
public class TopicComment {
    @TableId(type = IdType.AUTO)
    Integer cid;
    Integer tid;
    Integer uid;
    String content;
    Date time;
    Integer quote;
}
