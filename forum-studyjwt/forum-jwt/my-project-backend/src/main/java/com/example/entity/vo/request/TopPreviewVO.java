package com.example.entity.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TopPreviewVO {
   Integer tid;
    Integer type;
    String tittle;
    String content;
    Date time;
    List<String> images;
    Integer uid;
     String username;
    String avatar;
    int like;
    int collect;
    int dislike;
    InnerInteract interact;
    @Data
    @AllArgsConstructor
    public static class InnerInteract{
        Boolean like;
        Boolean collect;
        Boolean dislike;
    }
}
