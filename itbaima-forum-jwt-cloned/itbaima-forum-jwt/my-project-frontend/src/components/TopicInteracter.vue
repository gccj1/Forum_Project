<script setup>
defineProps({
  checkname:String,
  number:Number,
  check:Boolean,
  color:String
})
const emit=defineEmits(['check'])
</script>

<template>
<div class="topic-button" >
  <span class="name" :style="{'color' :  color}">
    {{check ? checkname : '' }}
  </span>
  <span class="icon" :style="{'color' : check ? color:'unset'}" @click="emit('check')">
    <slot></slot>
  </span>
  <span class="name">
    {{number}}
  </span>
</div>
</template>

<style scoped>
.topic-button{
  display: inline-block;
  height: 20px;
  margin: 20px;
  .name{
    opacity: 0.7;
    font-size: 13px;
    margin-right: 5px;
  }
  .icon{
    opacity: 0.7;
    font-size: 20px;
    transition: 0.3s;
    vertical-align: middle;
    margin-right: 5px;
    &:hover{
      font-size: 25px;
      cursor: pointer;
    }
  }

}
</style>