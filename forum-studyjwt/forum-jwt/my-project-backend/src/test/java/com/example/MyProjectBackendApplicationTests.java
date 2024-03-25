package com.example;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.dto.Topic;
import com.example.mapper.TopicMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MyProjectBackendApplicationTests {
    @Resource
    TopicMapper mapper;
    @Test
    void contextLoads() {
        Page<Topic> pageObj=new  Page<>(1,10);
        Page<Topic> topicPage = mapper.selectPage(pageObj, Wrappers.<Topic>query().orderByDesc("time"));
        List<Topic> topics=pageObj.getRecords();
        System.out.println(topics.size());
    }
}
