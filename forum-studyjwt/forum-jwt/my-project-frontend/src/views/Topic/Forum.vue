<script setup>
import {get} from "@/net";
import {useStore} from "@/store";
const store=useStore()
get('/api/forum/type',(data)=>{
  const array=[]
  array.push({id:0,name:'全部',content:''})
  data.forEach(d=>array.push(d))
  store.forum.types=array
})
</script>

<template>
  <router-view v-slot="{ Component }">
    <transition name="el-fade-in-linear" mode="out-in">
      <keep-alive include="TopicIndex">
        <component :is="Component" style="height: 100%"/>
      </keep-alive>
    </transition>
  </router-view>
</template>

<style scoped>

</style>