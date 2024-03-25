package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Interact;
import com.example.entity.dto.Topic;
import com.example.entity.vo.request.*;
import com.example.entity.vo.response.CommentsVO;

import java.util.List;

public interface TopicService extends IService<Topic>{
    List<CommentsVO> getComments(int tid);
    public String deleteComment(int cid,int uid);
    public String createTopic(int id, TopicVO vo);
    public String updateTopic(int uid, UpdateTopicVO vo);
    public String addComment(AddCommentVO vo,int uid);
    public List<TopPreviewVO> topicPreview(int page,int type);
    public TopPreviewVO getDetailByTid(int tid,int uid);
    public List<TopListVO> getTopList();
    public List<TopPreviewVO> getCollects(int uid);
    public void postInteract(Interact interact,boolean state);
}
