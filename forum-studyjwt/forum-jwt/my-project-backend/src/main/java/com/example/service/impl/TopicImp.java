package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.dto.Interact;
import com.example.entity.dto.Topic;
import com.example.entity.dto.TopicComment;
import com.example.entity.vo.request.*;
import com.example.entity.vo.response.CommentsVO;
import com.example.entity.vo.response.NotificationVO;
import com.example.mapper.AccountMapper;
import com.example.mapper.TopicCommentMapper;
import com.example.mapper.TopicMapper;
import com.example.service.NotificationService;
import com.example.service.TopicService;
import com.example.utils.CacheUtils;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TopicImp extends ServiceImpl<TopicMapper, Topic> implements TopicService {
    @Resource
    CacheUtils cacheUtils;
    @Resource
    AccountMapper accountMapper;
    @Resource
    StringRedisTemplate template;
    @Resource
    FlowUtils flowUtils;
    @Resource
    TopicCommentMapper commentMapper;
    @Resource
    NotificationService notificationService;

    @Override
    public String deleteComment(int cid, int uid) {
        commentMapper.delete(Wrappers.lambdaQuery(TopicComment.class)
                .eq(TopicComment::getCid, cid)
                .eq(TopicComment::getUid, uid));
        return null;
    }

    @Override
    public List<CommentsVO> getComments(int tid) {
        List<TopicComment> topicComments = commentMapper.selectList(Wrappers.lambdaQuery(TopicComment.class)
                        .eq(TopicComment::getTid, tid).orderByDesc(TopicComment::getTime));
        List<CommentsVO> commentsVOList = topicComments.stream().map(topicComment -> {
            CommentsVO commentsVO = new CommentsVO();
            BeanUtils.copyProperties(topicComment, commentsVO);
            Account account = accountMapper.selectById(topicComment.getUid());
            BeanUtils.copyProperties(account, commentsVO);
            StringBuilder text=new StringBuilder();
            if(topicComment.getQuote() > 0) {
                TopicComment comment = commentMapper.selectById(topicComment.getQuote());
                if(comment==null) text.append("此评论已被删除");
                else{
                    String content =comment.getContent();
                    JSONArray ops= JSONObject.parseObject(content).getJSONArray("ops");
                    for(Object op :ops){
                        Object insert=JSONObject.parseObject(op.toString()).get("insert");
                        text.append(insert);
                    }
                }
            }
                commentsVO.setQuote( text.length() <= 30? text.toString():text.substring(0,30)+"...");
            return commentsVO;
        }).toList();
        return commentsVOList;
    }
    @Override
    public String addComment(AddCommentVO vo, int uid) {
        String keu=Const.LIMIT_COMMENT+":"+uid;
        if(!flowUtils.limitCounterCheck(keu,2,60)) return "操作过于频繁";
        TopicComment comment = new TopicComment();
        comment.setUid(uid);
        comment.setTime(new Date());
        BeanUtils.copyProperties(vo, comment);
        if(commentMapper.insert(comment)>0){
            Topic topic = baseMapper.selectById(vo.getTid());
            Account account = accountMapper.selectById(uid);
            if(vo.getQuote()>0){
                TopicComment quote = commentMapper.selectById(vo.getQuote());
                if(uid != quote.getUid()){
                    NotificationVO notificationVO = new NotificationVO();
                    notificationVO.setTittle("您有新的评论回复");
                    notificationVO.setContent(account.getUsername()+"  回复了你发表的评论，快去看看吧！");
                    notificationVO.setUrl("/index/topic-details/"+quote.getTid());
                    notificationVO.setTime(new Date());
                    notificationService.addNotification(quote.getUid(),notificationVO);
                }
            }
            else{
                if(uid != topic.getUid()){
                    NotificationVO notificationVO = new NotificationVO();
                    notificationVO.setTittle("您有新的帖子回复");
                    notificationVO.setContent(account.getUsername()+" 回复了您的帖子: "+topic.getTittle()+",快去看看吧！");
                    notificationVO.setUrl("/index/topic-details/"+topic.getTid());
                    notificationVO.setTime(new Date());
                    notificationService.addNotification(topic.getUid(),notificationVO);
                }
            }
            return null;
        }
        return "发生一些错误,评论未完成";
    }

    @Override
    public List<TopPreviewVO> getCollects(int uid) {
        List<Topic> topics = baseMapper.collectTopic(uid);
        List<TopPreviewVO> list = topics.stream().map(topic -> {
            TopPreviewVO topPreviewVO = new TopPreviewVO();
            BeanUtils.copyProperties(topic, topPreviewVO);
            return topPreviewVO;
        }).toList();
        return list;
    }

    @Override
    public void postInteract(Interact interact, boolean state) {
        String type=interact.getType();
        synchronized (type.intern()){
            template.opsForHash().put(type,interact.toKey(),Boolean.toString(state));
            saveInteractSchedule(type);
        }
    }
    private final Map<String,Boolean> state =new HashMap<>();
    ScheduledExecutorService service= Executors.newScheduledThreadPool(2);
    private void saveInteractSchedule(String type){
        if(!state.getOrDefault(type,false)){
            state.put(type,true);
            service.schedule(()->{
                this.saveInteract(type);
                state.put(type,false);
            },
                3, TimeUnit.SECONDS);
            }
        }
     private boolean checkInteract(int tid,int uid,String type) {
        String key=tid+":"+uid;
        if(template.opsForHash().hasKey(type,key)){
            return Boolean.parseBoolean(template.opsForHash().entries(type).get(key).toString());
        }
        return baseMapper.interactDetailCount(type,tid,uid) > 0;
     }
    private void saveInteract(String type){
        synchronized (type.intern()){
            List<Interact> check=new ArrayList<>();
            List<Interact> uncheck=new ArrayList<>();
            template.opsForHash().entries(type).forEach((k,v)->{
                if(Boolean.parseBoolean(v.toString())){
                    check.add(Interact.parse(k.toString(),type));
                }
                else{
                    uncheck.add(Interact.parse(k.toString(),type));
                }
            });
            if(!check.isEmpty()) baseMapper.addInteract(check,type);
            if(!uncheck.isEmpty()) baseMapper.deleteInteract(uncheck,type);
            template.delete(type);
        }
    }
    @Override
    public TopPreviewVO getDetailByTid(int tid,int uid) {
        Topic topics = baseMapper.selectById(tid);
        Integer uid1 = topics.getUid();
        TopPreviewVO vo = new TopPreviewVO();
        BeanUtils.copyProperties(topics, vo);
        vo.setLike(baseMapper.interactCount("like",tid));
        vo.setCollect(baseMapper.interactCount("collect",tid));
        vo.setDislike(baseMapper.interactCount("dislike",tid));
        TopPreviewVO.InnerInteract innerInteract=new TopPreviewVO.InnerInteract(checkInteract(tid,uid,"like"),
                checkInteract(tid,uid,"collect"),
                checkInteract(tid,uid,"dislike"));
        vo.setInteract(innerInteract);
        BeanUtils.copyProperties(accountMapper.selectById(uid1),vo);
        return vo;
    }

    @Override
    public List<TopListVO> getTopList() {
        List<Topic> topics = baseMapper.selectList(Wrappers.<Topic>query().eq("top", 1).select("tid", "tittle", "time").orderByDesc("time"));
        return topics.stream().map(topic ->{
            TopListVO vo = new TopListVO();
            BeanUtils.copyProperties(topic,vo);
            return vo;
        }).toList();
    }


    @Override
    public List<TopPreviewVO> topicPreview(int page, int type) {
        String key= Const.FORUM_PREVIEW_CACHE+page+":"+type;
        List<TopPreviewVO> list=cacheUtils.takeListFromCache(key,TopPreviewVO.class);
        if(list!=null) return list;
        Page<Topic> pageObj= Page.of(page,10);
        if(type==0) baseMapper.selectPage(pageObj, Wrappers.<Topic>query().orderByDesc("time"));
         else baseMapper.selectPage(pageObj,Wrappers.<Topic>query().eq("type",type).orderByDesc("time"));
        List<Topic> topics=pageObj.getRecords();
        if(topics.isEmpty()) return null;
        list = topics.stream().map(this::toPreviewVO).toList();
        cacheUtils.putListToCache(key,list,60);
        return list;
    }
        private  TopPreviewVO toPreviewVO(Topic topic){
        TopPreviewVO previewVO=new TopPreviewVO();
            Integer tid = topic.getTid();
            Integer uid = topic.getUid();
            previewVO.setLike(baseMapper.interactCount("like",tid));
            previewVO.setCollect(baseMapper.interactCount("collect",tid));
            previewVO.setDislike(baseMapper.interactCount("dislike",tid));
        BeanUtils.copyProperties(accountMapper.selectById(uid),previewVO);
        BeanUtils.copyProperties(topic,previewVO);
            List<String> images = new ArrayList<>();
            StringBuilder previewText=new StringBuilder();
            JSONArray ops= JSONObject.parseObject(topic.getContent()).getJSONArray("ops");
            for(Object op :ops){
                Object insert=JSONObject.from(op).get("insert");
                if(insert instanceof String text ){
                    if(previewText.length()>=300) continue;
                    previewText.append(text);
                }
                else if(insert instanceof Map<?,?> map){
                    Optional.ofNullable(map.get("image")).ifPresent(obj->images.add(obj.toString()));
                }
            }
            previewVO.setContent(previewText.length()>300? previewText.substring(0,300):previewText.toString());
            previewVO.setImages(images);
            return previewVO;
    }

    @Override
    public String updateTopic(int uid, UpdateTopicVO vo) {
        baseMapper.update(null,Wrappers.<Topic>update()
                .eq("uid",uid).eq("tid",vo.getTid())
                .set("content",vo.getContent().toJSONString())
                .set("tittle",vo.getTittle())
                .set("type",vo.getType())
    );
        return null;
    }



    @Override
    public String createTopic(int uid, TopicVO vo) {
        Topic topic = new Topic();
        BeanUtils.copyProperties(vo, topic);
        topic.setUid(uid);
        topic.setContent(vo.getContent().toJSONString());
        topic.setTime(new Date());
        if(save(topic)){
            cacheUtils.deleteCachePattern(Const.FORUM_PREVIEW_CACHE+"*");
            return null;
        }
        return "发生一些错误,创建帖子失败";
    }
}
