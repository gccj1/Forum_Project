package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.TopicType;
import com.example.mapper.TopicTypeMapper;
import com.example.service.TopicTypeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TopicTypeImp extends ServiceImpl<TopicTypeMapper, TopicType> implements TopicTypeService  {
        @Resource
        TopicTypeMapper mapper;
    @Override
    public List<TopicType> getTopicTypesByTopicId() {
        return mapper.selectList(null);
    }
}
