package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class CommentsVO {
    Integer cid;
    Integer uid;
    Integer tid;
    String username;
    String avatar;
    String quote;
    Date time;
    String content;
}
