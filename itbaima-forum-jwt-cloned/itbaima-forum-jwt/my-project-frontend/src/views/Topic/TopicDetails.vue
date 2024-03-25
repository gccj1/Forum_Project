<script setup>
import {useRoute} from "vue-router";
import {get, post} from "@/net";
import {computed, reactive, ref} from "vue";
import LightCard from "@/components/LightCard.vue";
import {ArrowLeft, ChatDotSquare, CircleCheck, Clock, DeleteLocation, Remove, Star} from "@element-plus/icons-vue";
import {useStore} from "@/store";
import {QuillDeltaToHtmlConverter} from 'quill-delta-to-html'
import router from "@/router";
import TopicInteracter from "@/components/TopicInteracter.vue";
import axios from "axios";
import Topic from "@/components/Topic.vue";
import {ElMessage} from "element-plus";
import CommentEditor from "@/components/CommentEditor.vue";

const route=useRoute()
const tid=route.params.tid
const topicDetail=ref({})
const comment=reactive({
  show:false,
  quote :'',
  commentsList:null,
})
const interactState=reactive({
  like:false,
  collect:false,
  dislike:false,
})
const store=useStore()
const success=ref(false)
const updateEdit=ref(false)

function getCommentsList() {
  get(`/api/forum/comments?tid=${tid}`, data=>{
    comment.commentsList=data
  })
}
function deleteComment(cid) {
  get(`/api/forum/delete-comment?cid=${cid}`,()=>{
    ElMessage.success('评论删除成功')
    getCommentsList()
  })
}
let detailContent=null
const init=()=>{
  get(`/api/forum/topic-detail?tid=${tid}`, (data) =>{
    success.value=true
    topicDetail.value=data
    interactState.like=data.interact.like
    interactState.collect=data.interact.collect
    interactState.dislike=data.interact.dislike
    getCommentsList()
    detailContent=convertTohtml(data.content)
  })

}
init()
function convertTohtml(content){
  const ops= JSON.parse(content).ops
  const converter =new QuillDeltaToHtmlConverter(ops,{inlineStyles:true })
  return converter.convert();
}
function updateTopic(editor) {
  post('/api/forum/update-topic', {
    tid: tid,
    type: editor.type,
    tittle: editor.tittle,
    content: editor.content
  }, () => {
    ElMessage.success('帖子内容更新成功！')
    updateEdit.value = false
    init()
  })
}

function interact(type){
  get(`/api/forum/interact?tid=${tid}&type=${type}&state=${!interactState[type]}`,()=>{
    interactState[type]=!interactState[type]
  })
}
</script>

<template>
  <div style="display: flex">
    <div style="flex: 1">
      <light-card  style="max-width: 950px;margin: 10px auto ;" v-loading="!success" element-loading-text="帖子加载中...">
        <div >
          <el-button  type="primary" plain :icon="ArrowLeft" @click="router.push('/index/')">回到列表</el-button>
        </div>
        <div style="display: flex;margin: 20px 0;">
          <div style="margin-right: 10px;">
            <el-avatar :size="60" :src="topicDetail.avatar ?`${axios.defaults.baseURL}/images${topicDetail.avatar}`:'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'"></el-avatar>
          </div>
          <div style="margin-top: 20px;">
            <div style=" font-size: 20px;">{{topicDetail.username}}</div>
            <div style="font-size: 13px;color: grey;">
              {{new Date(topicDetail.time).toLocaleString()}}
            </div>
          </div>
        </div>
        <div style="display: flex; font-weight: bolder;font-size: 20px;">
          <div style="margin: 0 10px;">
            <el-tag style="font-size: 15px; padding: 5px;">{{store.getTypes(topicDetail.type)?.name}}</el-tag>
          </div>
          <div style="text-align: center; flex: 1">
            {{topicDetail.tittle}}
          </div>

        </div>
        <el-divider style="margin: 7px 0;"></el-divider>
        <div v-html="detailContent" style="opacity: 0.8;margin: 20px;min-height: 250px;"></div>
        <el-divider style="margin: 7px 0; opacity: 0.6;font-size: 12px;">到底啦~</el-divider>
        <div style="text-align: right;">
          <topic-interacter   v-if="store.user.uid===topicDetail.uid" @click="updateEdit=true">
            <font-awesome-icon  icon="fa-edit"></font-awesome-icon>
          </topic-interacter>
          <topic-interacter  :number="topicDetail.like" checkname="已点赞" color="green" @check="interact('like')" :check="interactState.like">
            <font-awesome-icon icon="fa-thumbs-up"></font-awesome-icon>
          </topic-interacter>
          <topic-interacter  :number="topicDetail.collect" checkname="已收藏" color="yellow" @check="interact('collect')" :check="interactState.collect">
            <font-awesome-icon icon="fas fa-calendar-check" />
          </topic-interacter>
          <topic-interacter  :number="topicDetail.dislike" checkname="已踩" color="red" @check="interact('dislike')" :check="interactState.dislike">
            <font-awesome-icon icon="fa-thumbs-down"></font-awesome-icon>
          </topic-interacter>
        </div>



        <topic v-if="topicDetail" :show="updateEdit" @close="updateEdit=false" :default-tittle="topicDetail.tittle" :default-content="topicDetail.content"
               :default-type="topicDetail.type" :submit-button="'修改帖子'" :submit="updateTopic">
        </topic>
        <comment-editor :show="comment.show" @comments="init" @close="comment.show=false"
                        :tid="tid" :quote="comment.quote"></comment-editor>

      </light-card>
      <light-card v-for="item in comment.commentsList" style="margin: 10px auto;max-width: 950px;">
        <div style="display: flex;margin: 20px 0;">
          <div style="margin-right: 10px;">
            <el-avatar :size="60" :src="item.avatar ?`${axios.defaults.baseURL}/images${item.avatar}`:'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'"></el-avatar>
          </div>
          <div style="margin-top: 20px;">
            <div style=" font-size: 20px;">{{item.username}}</div>
            <div style="font-size: 13px;color: grey;">
              {{new Date(item.time).toLocaleString()}}
            </div>
          </div>
        </div>

        <div>
          <div v-if="item.quote" class="reply-comment">
            回复 : [ {{item.quote}} ]
          </div>
        </div>

        <div v-html="convertTohtml(item.content)" style="opacity: 0.8;margin: 20px;"></div>
        <div style="text-align: right;padding: 0 20px;">
          <el-link class="commentButton" :underline="false" @click="comment.show=true;comment.quote=item"
                   style="font-size: 15px;" :icon="ChatDotSquare">&nbsp;回复评论</el-link>
          <el-popconfirm title="确定要删除评论吗？" @confirm="deleteComment(item.cid)" placement="top">
            <template #reference>
              <el-link class="commentButton" v-if="item.uid===store.user.uid" :underline="false"
                       style="font-size: 15px;color: #a23a14" :icon="Remove">&nbsp; 删除评论</el-link>
            </template>
          </el-popconfirm>

        </div>
      </light-card>
    </div>
    <div style="min-width: 150px;">
      <div style="position: sticky;top: 700px;margin-left: 10px;" >
        <font-awesome-icon class="comment-icon" size="4x" icon="fa-comments" title="发表评论" @click="comment.show=true;comment.quote=null" />
      </div>
    </div>
  </div>

</template>

<style scoped>
.reply-comment{
  opacity: 0.7;
  width: 750px;
  background-color: gray;
  padding: 10px ;
  border-radius: 5px;
  margin: 10px ;
}
.commentButton{
  margin: 5px 20px;
  transition:  0.3s;
  &:hover{
    scale: 1.1;
    cursor: pointer;
  }
}
.comment-icon{
  transition:  0.3s;
  &:hover{
    color: yellowgreen;
   scale: 1.1;
    cursor: pointer;
  }
}
</style>