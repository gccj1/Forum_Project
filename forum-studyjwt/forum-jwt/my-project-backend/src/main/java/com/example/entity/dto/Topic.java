package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_topic")
public class Topic {
    @TableId(type = IdType.AUTO)
    Integer tid;
    Integer type;
    String tittle;
    String content;
    Integer uid;
    Date time;
   Integer top;
}
