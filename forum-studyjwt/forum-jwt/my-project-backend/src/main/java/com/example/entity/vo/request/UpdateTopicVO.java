package com.example.entity.vo.request;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

@Data
public class UpdateTopicVO {
    int tid;
    int type;
    String tittle;
    JSONObject content;
}
