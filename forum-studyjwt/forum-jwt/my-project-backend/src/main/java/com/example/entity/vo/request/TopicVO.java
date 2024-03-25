package com.example.entity.vo.request;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

@Data
public class TopicVO {
    String tittle;
    Integer type;
    JSONObject content;
}
