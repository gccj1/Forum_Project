<script setup>
import {Delta, QuillEditor} from "@vueup/vue-quill";
import {computed, reactive, ref} from 'vue';
import '@vueup/vue-quill/dist/vue-quill.snow.css';
import {post} from "@/net";
import router from "@/router";
import {ElMessage} from "element-plus";
const  props=defineProps({
  show:Boolean,
  tid:Number,
  quote:Object,
})
const emit=defineEmits(['close','comments'])
const editor = reactive({
  content:'',
})
function deltaToText(delta){
  if(!delta.ops) return ''
  let str=''
  for(let op of delta.ops){
    str+=op.insert
  }
  return str.replace(/\s/g,'')
}
let textLen=computed(() =>deltaToText(editor.content).length)
const submitComment = () => {
 let text=deltaToText(editor.content).length
  if(text > 1000) {
    ElMessage.error('评论字数不能超过1000')
    return ;
  }
  post('/api/forum/add-comment',{
    tid:props.tid,
    content: JSON.stringify(editor.content),
    quote:props.quote ? props.quote.cid : 0,
  },()=>{
    emit('close')
    emit('comments')
    ElMessage.success('评论发表成功')
  },(meg)=>{
    ElMessage.error(meg)
  })
}

// function deltaToSimpleText(delta) {
//   let str = ''
//   for (let op of JSON.parse(delta).ops) {
//     str += op.insert
//   }
//   if (str.length > 30){
//    str= str.substring(0, 30) + "..."
//   }
//   return str
// }
</script>

<template>
<div>
    <el-drawer :model-value="show" direction="btt"
               @close="emit('close')" size="400px" :close-on-click-modal="false"
                :title=" props.quote ? `回复 :  ${props.quote.username} ` : '发表评论' ">
              <div>
                <div>
                  <quill-editor v-model:content="editor.content" :content-type="'delta'"
                                placeholder="恶语伤人六月寒，良言一句三冬暖……"></quill-editor>
                </div>
                <div style="display: flex;justify-content: space-between; margin-top: 10px;">
                  <div>{{textLen}}/1000</div>
                  <el-button type="success" plain @click="submitComment">发表评论</el-button>
                </div>
              </div>
    </el-drawer>
</div>
</template>

<style scoped>
:deep(.el-drawer){
  margin: 10px auto;
  width: 900px;
  border-radius: 8px;
}
:deep(.el-drawer__header){
  margin: 0;
}
:deep(.el-drawer__body){
  padding: 10px;
}
:deep(.ql-toolbar){
  border-color: var(--el-border-color);
  border-radius: 8px 8px 0 0;
}
:deep(.ql-editor.ql-blank::before){
  color: var(--el-text-color-placeholder);
  font-style: normal;
  opacity: 0.7;
}
:deep(.ql-editor){
  font-size: 15px;
  height: 250px;
}
:deep(.ql-container){
  border-color: var(--el-border-color);
  border-radius: 0 0 8px 8px;
}
</style>