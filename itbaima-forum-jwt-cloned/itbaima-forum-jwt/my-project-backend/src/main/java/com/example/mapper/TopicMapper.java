package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.Interact;
import com.example.entity.dto.Topic;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TopicMapper extends BaseMapper<Topic> {
    @Insert("""
                 <script>
                    insert ignore into db_topic_${type} values
                    <foreach collection ="interacts" item="item" separator =",">
                    (#{item.tid},#{item.uid},#{item.time})
                    </foreach>
                   </script>
         """)
        void addInteract(List<Interact> interacts, String type);
        @Delete("""
                <script>
                delete from db_topic_${type} where
                <foreach collection ="interacts" item="item" separator =",">
                (tid=#{item.tid} and uid=#{item.uid})
                </foreach>
                </script>
                  """)
    void deleteInteract(List<Interact>interacts,String type);
    @Select("""
            select count(*) from db_topic_${type} where tid=#{tid};
            """)
    int interactCount(String type,int tid);
    @Select("""
            select count(*) from db_topic_${type} where tid=#{tid} and uid=#{uid};
            """)
    int interactDetailCount(String type,int tid,int uid);
    @Select("""
            select db_topic.tittle,db_topic.type,db_topic.tid from db_topic_collect left join db_topic on db_topic_collect.tid=db_topic.tid where db_topic.uid=#{uid};
            """)
    List<Topic> collectTopic(int uid);
}
