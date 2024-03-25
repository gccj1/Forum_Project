<script setup>

import LightCard from "@/components/LightCard.vue";
import {
  Calendar,
  Clock,
  CollectionTag,
  Compass,
  Document,
  Picture,
  Edit,
  EditPen,
  Link,
  Microphone, Collection
} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {get} from "@/net";
import {reactive, ref, watch} from "vue";
import {ElMessage} from "element-plus";
import Topic from "@/components/Topic.vue";
import {useStore} from "@/store";
import axios from "axios";
import router from "@/router";
import TopicInteracter from "@/components/TopicInteracter.vue";
import Collects from "@/components/collects.vue";

const today=new Date()
const year=today.getFullYear()
const month=today.getMonth()+1
const day=today.getDate()
const date=`${year} 年 ${month} 月 ${day} 日`

const store=useStore()

get("/api/forum/top-topics",data =>{
  top.value=data
})

const weather=reactive({
  location:{},
  now:{},
  hourly:[],
  success:false,
})
const editor =ref(false)

const list=ref(null)
const collectList=ref([])
const type=ref(0)
const page=ref(1)
const topicEnd=ref(false)
const top=ref(null)
function resetTopic(){
  page.value=1
  list.value=null
  topicEnd.value=false
  // updateTopic()
}
get(`api/forum/topic-preview?page=${page.value}&type=${type.value}`,data =>{
  list.value=data
})

const updatePage=()=>{
  get(`api/forum/topic-preview?page=${page.value}&type=${type.value}`,data =>{
    list.value=data
  })
}
watch(type,()=>{
  resetTopic()
  updatePage()
}, {immediate:true})
function updateTopic(){
  if(topicEnd.value) return;
  get(`api/forum/topic-preview?page=${page.value}&type=${type.value}`, data =>{
       if(data){
         list.value=data
         page.value+=1
       }
      if(!data || data.length < 5  ) {
        topicEnd.value=true
      }
    })
}
function onCreateTopic(){
  resetTopic()
  updatePage()
  editor.value=false
}
navigator.geolocation.getCurrentPosition((position) =>{
  const latitude=position.coords.latitude
  const longitude=position.coords.longitude
  get(`/api/forum/weather?longitude=${longitude}&latitude=${latitude}`,data =>{
    Object.assign(weather,data)
    weather.success=true
  },(meg)=>{
    ElMessage.warning(meg)
  })
},(error) =>{
      console.log(error)
  get("/api/forum/weather?longitude=113.083501&latitude=28.183141",data =>{
    Object.assign(weather,data)
    weather.success=true
    ElMessage.warning("无法获取您的位置，已使用默认位置")
  }, (meg)=>{
        ElMessage.warning(meg)
      }
  )
}, {
      timeout:3000,
      enableHighAccuracy:true
    }
)
const collectShow=ref(false)
const getCollects=()=>{
  get("/api/forum/collects",data =>{
    collectList.value=data
    collectShow.value=true
  })
}

</script>

<template>
  <div style="display: flex">
    <div  id="top"></div>
    <div style="min-width: 100px;">
<!--      <a href="#top" style="text-decoration: none;" ><div class="backTop"  >-->
<!--        <span ><font-awesome-icon size="4x"  icon="fa-arrow-alt-circle-up" /></span>-->
<!--      </div></a>-->
    </div>
    <div  style="display: flex; max-width: 1000px;min-width: 950px; margin: 15px auto 20px 40px; gap: 10px;">
      <div style="flex: 1;">
        <div style="margin: 10px 0;" >
          <light-card  style="margin: 10px 0;">
            <div  class="create-topic" @click="editor=true">
              <el-icon><EditPen/></el-icon>
              发表一些想法吧......
            </div>
            <div style="margin-top:10px;display:flex;gap:20px;font-size:18px;color:grey">
              <el-icon><Edit /></el-icon>
              <el-icon><Document /></el-icon>
              <el-icon><Compass /></el-icon>
              <el-icon><Picture /></el-icon>
              <el-icon><Microphone /></el-icon>
            </div>

          </light-card>
          <light-card style="margin: 10px 0;">
            <div @click="router.push(`/index/topic-details/${item.tid}`)" class="top-list"   v-for="item in top">
              <div>
                <el-tag>置顶</el-tag>
                {{item.tittle}}</div>
              <div style="font-size: 12px;color: grey;">
                <el-icon><Clock/></el-icon>
                {{new Date(item.time).toLocaleString()}}</div>
            </div>
          </light-card>
          <light-card style="display: flex; gap: 10px;">
            <div :class="`type-selector ${type===item.id ? 'active':''}`" @click="type=item.id" v-for="item in store.forum.types">
              <span>{{item.name}}</span>
            </div>
          </light-card>
        </div>
        <transition name="el-fade-in-linear" mode="out-in">
          <div>
            <div class="topic-container"  style="display: flex; gap: 10px;flex-direction: column; min-height: 600px;" >
              <light-card v-for="item in list" class="topic-div">
                <div @click="router.push(`/index/topic-details/${item.tid}`)">
                  <div style="display: flex">
                    <div style="margin-right: 10px;">
                      <el-avatar :size="40"
                                 :src="item.avatar ?`${axios.defaults.baseURL}/images${item.avatar}`
                                 :'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'">

                      </el-avatar>
                    </div>
                    <div>
                      <div style="translate: 5px; font-size: 15px;">{{item.username}}</div>
                      <div style="font-size: 12px;color: grey;">
                        <el-icon><Clock/></el-icon>
                        {{new Date(item.time).toLocaleString()}}
                      </div>
                    </div>
                  </div>
                  <el-divider style="margin: 3px 0;"></el-divider>
                  <div >
                    <el-tag style="margin-right: 10px;">
                      {{store.getTypes(item.type)?.name}}
                    </el-tag>
                    <span class="topic-tittle">{{item.tittle}} </span>
                  </div>
                  <div class="topic-content">{{item.content}}</div>
                  <div style="display: grid;  grid-template-columns: repeat(2,1fr); grid-gap: 10px;">
                    <el-image class="topic-image" fit="cover" v-for="img in item.images" :src="img"></el-image>
                  </div>
                  <div style="text-align: right;">
                    <topic-interacter   v-if="store.user.uid===item.uid" >
                      <font-awesome-icon style="color: greenyellow" icon="fa-edit"></font-awesome-icon>
                    </topic-interacter>
                    <topic-interacter  :number="item.like" >
                      <font-awesome-icon icon="fa-thumbs-up"></font-awesome-icon>
                    </topic-interacter>
                    <topic-interacter  :number="item.collect" >
                      <font-awesome-icon icon="fas fa-calendar-check" />
                    </topic-interacter>
                    <topic-interacter  :number="item.dislike" >
                      <font-awesome-icon icon="fa-thumbs-down"></font-awesome-icon>
                    </topic-interacter>
                  </div>
                </div>

              </light-card>

            </div>
            <light-card style="margin: 10px 0;">
              <el-pagination style=""  v-model:current-page="page" @update:current-page="updatePage"  layout="prev, pager, next" :total="100" />
            </light-card>
          </div>

        </transition>

      </div>
      <div style="width: 300px;">
        <div style="position: sticky; top: 20px;">
          <light-card @click="getCollects" style="margin: 10px 0;">
            <div class="collects">
              <el-icon style="translate: -2px 3px;"><Collection/></el-icon>
              点击查看收藏
            </div>
          </light-card>
          <light-card>
            <div  style=" font-weight: bold;text-align: center;">
              <el-icon><CollectionTag/></el-icon>
              论坛更新公告
            </div>
            <el-divider style="margin: 10px 0;"></el-divider>
            <div style="color: gray; font-size: 14px; padding: 0 20px;">
              If you can keep your head when all about you
              Are losing theirs and blaming it on you;
            </div>
          </light-card>
          <div style="margin-top: 10px;">
            <light-card>
              <div  style=" font-weight: bold;text-align: center;">
                <el-icon ><Calendar/></el-icon>
                天气预报
              </div>
              <el-divider style="margin: 10px 0;"></el-divider>
              <div>
                <weather :data="weather"></weather>
              </div>
            </light-card>
          </div>
          <div style="margin-top: 10px;">
            <light-card>
              <div class="info-text">
                <div>今日</div>
                <div>{{date}}</div>
              </div>
              <div class="info-text">
                <div>IP</div>
                <div>127.0.0.1</div>
              </div>
              <div class="info-text">
                <div>归属地</div>
                <div>湖南</div>
              </div>
            </light-card>
          </div >
          <div style="margin: 10px 0;">
            <div style="color: grey;font-size: 15px;">
              <el-icon><Link/></el-icon>
              友情链接
            </div>
            <el-divider style="margin: 10px 0;"></el-divider>
            <div style="display: grid;grid-template-columns: repeat(2,1fr);grid-gap:10px;margin: 10px;">
              <div class="link">
                <el-image style="height: 100%"  src="https://upload-dianshi-1255598498.file.myqcloud.com/upload/nodir/345X200-9ae456f58874df499adf7c331c02cb0fed12b81d.jpg"></el-image>
              </div>
              <div class="link">
                <el-image style="height: 100%"  src="https://upload-dianshi-1255598498.file.myqcloud.com/upload/nodir/345X200-9ae456f58874df499adf7c331c02cb0fed12b81d.jpg"></el-image>
              </div>
              <div class="link">
                <el-image style="height: 100%" src="https://upload-dianshi-1255598498.file.myqcloud.com/upload/nodir/345X200-9ae456f58874df499adf7c331c02cb0fed12b81d.jpg"></el-image>
              </div>
              <div class="link">
                <el-image style="height: 100%" src="https://upload-dianshi-1255598498.file.myqcloud.com/upload/nodir/345X200-9ae456f58874df499adf7c331c02cb0fed12b81d.jpg"></el-image>
              </div>
            </div>
          </div>
        </div>

      </div>
      <Topic :show="editor" @success="onCreateTopic()" @close="editor=false"></Topic>

      <collects :show="collectShow" @close="collectShow=false">
        <div v-for="item in collectList" class="collect-container" @click="router.push(`/index/topic-details/${item.tid}`)">
          <el-tag style="margin:0 5px" size="small">{{store.getTypes(item.type)?.name}}</el-tag>
          <div class="collect-info">{{item.tittle}}</div>
        </div>
      </collects>
    </div>
  </div>

</template>

<style scoped>
.backTop{
  translate: 15px;
  position:  sticky;
  top :720px;
  text-align: center;
  border-radius: 50px;
  transition: 0.3s;
  padding: 10px 10px;
  &:hover{
    scale: 1.03;
    color: yellowgreen;
    cursor: pointer;
  }
}
:deep(.el-pagination){
  justify-content: center;
}
.collect-container{
  opacity: 0.8;
  display: flex;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  margin: 10px 0;
  padding: 5px 1px;
  transition: 0.3s;
  &:hover{
    scale: 1.015;
    color: cornflowerblue;
    cursor: pointer;
  }
  .collect-info{
    display:-webkit-box;
    -webkit-box-orient:vertical;
    -webkit-line-clamp:1;
    overflow:hidden;
    margin: 0 5px;
    font-size: 15px;
  }
text-overflow:ellipsis;
}
.collects{
  text-align: center;
  opacity: 0.8;
  transition: 0.3s;
  color: yellowgreen;
  &:hover{
    cursor: pointer;
    font-size: 17px;
  }
}
.top-list{
  display: flex;
  justify-content: space-between;
  margin: 5px 0;
  opacity: 0.8;
  transition: color 0.3s;
  &:hover{
    color: cornflowerblue;
    cursor: pointer;
  }
}
.type-selector{
  padding: 2px;
  box-sizing: border-box;
  font-size: 13px;
  border: solid 1px var(--el-border-color);
  border-radius: 3px;
transition: background-color 0.3s;
  &.active{
      border: solid 1px #409eff;
    background-color: #1ca5ce;
      padding: 3px 7px;
    font-weight: bolder;
  }
  &:hover{
    cursor: pointer;
    background-color: #c9c6c4;
  }
}
.topic-div{
  padding: 20px;
  transition: scale 0.3s;
  &:hover{
     scale: 1.015;
    cursor: pointer;
  }
  .topic-image{
    height: 100%;
    width: 100%;
    max-height: 230px;
    border-radius: 5px;
  }
  .topic-content{
    font-size:15px;
    color:grey;
    margin:5px 0;
    display:-webkit-box;
    -webkit-box-orient:vertical;
    -webkit-line-clamp:3;
    overflow:hidden;
    text-overflow:ellipsis;
  }
  .topic-tittle{
    font-weight: bolder;
  }
  .topic-type{
    padding:0 5px;
    font-size:15px;
    border: solid 1px var(--el-border-color);
    font-weight: lighter;
    color: grey;
    border-radius: 5px;
    margin-right: 5px;
  }
}
.info-text{
  display: flex;
  justify-content: space-between;
  margin: 5px 10px;
}
.link{
  border-radius: 5px;
  overflow: hidden;
}
.create-topic{
  background-color: #f5f5f5;
  color: grey;
  border-radius: 5px;
  height: 100%;font-size: 15px;
  padding: 10px;
  &:hover{
    cursor: pointer;
  }
}
.dark .create-topic{
  background-color: #464444;
}
</style>