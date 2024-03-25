package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.TopicComment;
import com.example.mapper.TopicCommentMapper;
import com.example.service.CommentService;
import org.springframework.stereotype.Service;

@Service
public class CommentImp extends ServiceImpl<TopicCommentMapper, TopicComment> implements CommentService {

}
