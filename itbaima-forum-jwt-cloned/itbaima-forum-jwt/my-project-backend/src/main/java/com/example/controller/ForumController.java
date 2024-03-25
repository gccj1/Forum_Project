package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.Interact;
import com.example.entity.dto.TopicType;
import com.example.entity.vo.request.*;
import com.example.entity.vo.response.CommentsVO;
import com.example.service.TopicService;
import com.example.service.TopicTypeService;
import com.example.service.WeatherService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {
    @Resource
    WeatherService weatherService;
    @Resource
    TopicService topicService;
    @Resource
    TopicTypeService typeService;
    @GetMapping("/weather")
    public RestBean<WeatherVO> getWeather( double longitude, double latitude) {
        WeatherVO weatherVO = weatherService.fetchWeatherData(longitude, latitude);
        return weatherVO==null? RestBean.failure(400,"天气数据获取失败"): RestBean.success(weatherVO);
    }
    @PostMapping("add-comment")
    public RestBean<String> addComment(@RequestBody AddCommentVO vo,
                                       @RequestAttribute(Const.ATTR_USER_ID) int uid) {
        String comment = topicService.addComment(vo,uid);
        return comment!=null? RestBean.failure(400,comment): RestBean.success();
    }
    @GetMapping("delete-comment")
    public RestBean<String> deleteComment(int cid, @RequestAttribute(Const.ATTR_USER_ID) int uid) {
        return RestBean.success(topicService.deleteComment(cid,uid));
    }
    @GetMapping("/comments")
    public RestBean<List<CommentsVO>> getComments(int tid){
        return RestBean.success(topicService.getComments(tid));
    }
    @GetMapping("/type")
    public RestBean<List<TopicType>> getType(){
       return RestBean.success(typeService.getTopicTypesByTopicId()) ;
    }

    @GetMapping("/topic-preview")
    public RestBean<List<TopPreviewVO>>  getTopicPreview(@RequestParam @Min(0) int page,
                                                    @RequestParam @Min(0) int type){
        return RestBean.success(topicService.topicPreview(page, type));
    }
    @PostMapping("/update-topic")
    public RestBean<String> updateTopic(@RequestBody UpdateTopicVO topic,
                                      @RequestAttribute(Const.ATTR_USER_ID) int uid){
        return RestBean.success(topicService.updateTopic(uid, topic));
    }

    @PostMapping("/top-create")
    public RestBean<String> createTopicType(@RequestBody TopicVO topic,
                                          @RequestAttribute(Const.ATTR_USER_ID) int id){
        String topic1 = topicService.createTopic(id, topic);
        return topic1!=null? RestBean.failure(400,topic1): RestBean.success();
    }
    @GetMapping("/top-topics")
    public RestBean<List<TopListVO>> getTopList(){
        return RestBean.success(topicService.getTopList());
    }

    @GetMapping("/topic-detail")
    public RestBean<TopPreviewVO> getDetails(int tid,
                                             @RequestAttribute(Const.ATTR_USER_ID) int uid){
        return RestBean.success(topicService.getDetailByTid(tid,uid));
    }
    @GetMapping("/interact")
    public RestBean<Void> getInteract(int tid,@RequestAttribute(Const.ATTR_USER_ID) int uid,
                                      @Pattern(regexp = "(like|collect|dislike)") String type,
                                      boolean state){
        topicService.postInteract(new Interact(tid,uid,type,new Date()),state);
        return RestBean.success();
    }
    @GetMapping("/collects")
    public RestBean<List<TopPreviewVO>> getCollects(@RequestAttribute(Const.ATTR_USER_ID) int uid) {
        return RestBean.success(topicService.getCollects(uid));
    }
}
