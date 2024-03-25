<script setup>
import {get, logout} from '@/net'
import router from "@/router";
import {useStore} from "@/store";
import {reactive, ref} from "vue";
import {
  Back,
  Bell,
  ChatDotSquare, Check, Collection, DataLine,
  Document, Files, House,
  Location, Lock, Message, Monitor, Moon, MoonNight,
  Notification, Operation,
  Position,
  School, Search, Sunny,
  Umbrella, User
} from "@element-plus/icons-vue";
import {useDark} from "@vueuse/core";
import {ElMessage} from "element-plus";
import LightCard from "@/components/LightCard.vue";

const store = useStore()
const loading = ref(true)

const searchInput = reactive({
  type: '1',
  text: ''
})

get('/api/user/info', (data) => {
  store.user = data
  loading.value = false
})
useDark({
  selector:'html',
  valueDark: 'dark',
  valueLight: 'light',
  attribute:'class'
})
const isDark = useDark()
let loading1= ref(false)
const beforeChange = () => {
  loading1.value = true
  return new Promise((resolve) => {
    setTimeout(() => {
      loading1.value = false
      ElMessage.success('主题已切换')
      return resolve(true)
    }, 500)
  })
}

function userLogout() {
  logout(() => router.push("/"))
}
const  notification=ref([])
const loadNotification = () => {
  get('/api/notification/list', (data) => {
    notification.value = data
  })
}
loadNotification()
const confirmNotification = (id,url) => {
  get(`/api/notification/delete?id=${id}`, ()=>{
    loadNotification()
    window.open(url)
  })
}
const deleteAllNotification = () => {
  get('/api/notification/delete-all', () => {
    notification.value = []
    loadNotification()
  }

)}
</script>

<template>
  <div class="main-content" v-loading="loading" element-loading-text="正在进入，请稍后...">
    <el-container style="height: 100%" v-if="!loading">
      <el-header class="main-content-header">
        <div @click="router.push('/')"  class="logo-container">
          <el-image class="logo" src="https://element-plus.org/images/element-plus-logo.svg"/>
        </div>
        <div style="flex: 1;padding: 0 20px;text-align: center">
          <el-input v-model="searchInput.text" style="width: 100%;max-width: 500px" placeholder="搜索论坛相关内容...">
            <template #prefix>
              <el-icon><Search/></el-icon>
            </template>
            <template #append>
              <el-select style="width: 120px" v-model="searchInput.type">
                <el-option value="1" label="帖子广场"/>
                <el-option value="2" label="校园活动"/>
                <el-option value="3" label="表白墙"/>
                <el-option value="4" label="教务通知"/>
              </el-select>
            </template>
          </el-input>
        </div >
          <div style="margin: 0 20px;min-width: 100px;">
            <el-popover trigger="click" :width="350"  >
              <template #reference>
                <el-badge :value="notification.length" :max="10" :hidden="!notification.length">
                  <div class="bell-notification">
                    <el-icon  :size="25"><Bell/></el-icon>
                    <div style="font-size: 13px;">消息</div>
                  </div>
                </el-badge>
              </template>
              <el-empty :image-size="80" description="暂时没有新消息" v-if="!notification.length"></el-empty>
              <el-scrollbar v-else>
                <light-card  v-for="item in notification" @click="confirmNotification(item.id,item.url)" class="notification-item">
                  <div>
                    <el-tag size="small" style="margin: 5px 10px;" type="success">消息</el-tag>
                    <span>{{item.content}}</span>
                  </div>
                </light-card>
                <el-divider style="margin: 3px 0;"></el-divider>
              </el-scrollbar>
              <div style="text-align: center;margin-top:5px;">
                <el-button style="width: 100%;" :icon="Check"
                           type="info" @click="deleteAllNotification" plain>清除全部未读消息</el-button>
              </div>
            </el-popover>
          </div>

        <div class="user-info">
          <div class="profile">
            <div>{{store.user.username}}</div>
            <div>IP属地 : 湖南</div>
          </div>
          <el-dropdown>
            <el-avatar :src="store.avatarUrl"/>
            <template #dropdown>
              <el-dropdown-item>
                <el-icon><Operation/></el-icon>
                个人设置
              </el-dropdown-item>
              <el-dropdown-item>
                <el-icon><Message/></el-icon>
                消息列表
              </el-dropdown-item>
              <el-dropdown-item>
                  <el-switch :before-change="beforeChange" size="large"
                             v-model="isDark" :loading="loading1"
                             :active-action-icon="Moon" :inactive-action-icon="Sunny"
                             style="--el-switch-on-color: #464444; --el-switch-off-color: #d9d4d4; " >
                  </el-switch>
              </el-dropdown-item>
              <el-dropdown-item @click="userLogout" divided>
                <el-icon><Back/></el-icon>
                退出登录
              </el-dropdown-item>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-container>
        <el-aside width="230px">
          <el-scrollbar style="height: calc(100vh - 55px)">
            <el-menu
                router
                :default-active="$route.path"
                :default-openeds="['1','2','3']"
                style="min-height: calc(100vh - 55px)">
              <el-sub-menu index="1">
                <template #title>
                  <el-icon><Location/></el-icon>
                  <span><b>校园论坛</b></span>
                </template>
                <el-menu-item index="/index">
                  <template #title>
                    <el-icon><ChatDotSquare/></el-icon>
                    帖子广场
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Bell/></el-icon>
                    失物招领
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Notification/></el-icon>
                    校园活动
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Umbrella/></el-icon>
                    表白墙
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><School/></el-icon>
                    海文考研
                    <el-tag style="margin-left: 10px" size="small">合作机构</el-tag>
                  </template>
                </el-menu-item>
              </el-sub-menu>
<!--              <el-sub-menu index="2">-->
<!--                <template #title>-->
<!--                  <el-icon><Position/></el-icon>-->
<!--                  <span><b>探索与发现</b></span>-->
<!--                </template>-->
<!--                <el-menu-item>-->
<!--                  <template #title>-->
<!--                    <el-icon><Document/></el-icon>-->
<!--                    成绩查询-->
<!--                  </template>-->
<!--                </el-menu-item>-->
<!--                <el-menu-item>-->
<!--                  <template #title>-->
<!--                    <el-icon><Files/></el-icon>-->
<!--                    班级课程表-->
<!--                  </template>-->
<!--                </el-menu-item>-->
<!--                <el-menu-item>-->
<!--                  <template #title>-->
<!--                    <el-icon><Monitor/></el-icon>-->
<!--                    教务通知-->
<!--                  </template>-->
<!--                </el-menu-item>-->
<!--                <el-menu-item>-->
<!--                  <template #title>-->
<!--                    <el-icon><Collection/></el-icon>-->
<!--                    在线图书馆-->
<!--                  </template>-->
<!--                </el-menu-item>-->
<!--                <el-menu-item>-->
<!--                  <template #title>-->
<!--                    <el-icon><DataLine/></el-icon>-->
<!--                    预约教室-->
<!--                  </template>-->
<!--                </el-menu-item>-->
<!--              </el-sub-menu>-->
              <el-sub-menu index="3">
                <template #title>
                  <el-icon><Operation/></el-icon>
                  <span><b>个人设置</b></span>
                </template>
                <el-menu-item index="/index/user-info">
                  <template #title>
                    <el-icon><House/></el-icon>
                    用户中心
                  </template>
                </el-menu-item>
                <el-menu-item index="/index/user-setting">
                  <template #title>
                    <el-icon><User/></el-icon>
                    个人信息设置
                  </template>
                </el-menu-item>
                <el-menu-item  index="/index/privacy-setting">
                  <template #title>
                    <el-icon><Lock/></el-icon>
                    账号安全设置
                  </template>
                </el-menu-item>
              </el-sub-menu>
            </el-menu>
          </el-scrollbar>
        </el-aside>
        <el-main class="main-content-page">
          <el-scrollbar style="height: calc(100vh - 55px)">
            <router-view v-slot="{ Component }">
              <transition name="el-zoom-in-center" mode="out-in">
                <component :is="Component" style="height: 100%"/>
              </transition>
            </router-view>
          </el-scrollbar>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style lang="less" scoped>
.notification-item{
  margin: 3px;
  transition: all 0.3s;
  opacity: 0.7;
  &:hover{
    scale: 1.015;
    cursor: pointer;
  }
}
.bell-notification{
  color: gray;
  transition: all 0.3s;
  line-height: 12px;
  &:hover{
    scale: 1.1;
    color: yellowgreen;
    cursor: pointer;
  }
}
.main-content-page {
  padding: 0;
  background-color: #f7f8fa;
}
.logo-container{
  :hover{
    cursor: pointer;
  }
}
.dark .main-content-page {
  background-color: #212225;
}

.main-content {
  height: 100vh;
  width: 100vw;
}

.main-content-header {
  border-bottom: solid 1px var(--el-border-color);
  height: 55px;
  display: flex;
  align-items: center;
  box-sizing: border-box;

  .logo{
    height: 32px;
  }

  .user-info {
    display: flex;
    justify-content: flex-end;
    align-items: center;

    .el-avatar:hover {
      cursor: pointer;
    }

    .profile {
      text-align: right;
      margin-right: 20px;

      :first-child {
        font-size: 18px;
        font-weight: bold;
        line-height: 20px;
      }

      :last-child {
        font-size: 10px;
        color: grey;
      }
    }
  }
}
</style>
