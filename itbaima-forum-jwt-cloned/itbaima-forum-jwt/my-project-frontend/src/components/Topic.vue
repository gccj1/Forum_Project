<script setup>
import {Delta, QuillEditor} from "@vueup/vue-quill";
 import {computed, reactive, ref} from 'vue';
 import {Check, Document} from "@element-plus/icons-vue";
 import '@vueup/vue-quill/dist/vue-quill.snow.css';
 import imageResize from "quill-image-resize-vue";
 import {ImageExtend, QuillWatch} from 'quill-image-extend-module';
 import axios from "axios";
 import {accessHeader, get, post} from "@/net";
 import {ElMessage} from "element-plus";
 import {useStore} from "@/store";
 const props= defineProps({
   show:Boolean,
   defaultType:{
     default:''
   },
   defaultContent:{
     type:String,
     default:''
   },
   defaultTittle:{
     type:String,
     default:''
   },
  submitButton:{
     type:String,
     default:'立即发帖'
  },
   submit: {
     default: (editor, success) => {
       post('/api/forum/top-create',{
         type:editor.type,
         tittle:editor.tittle,
         content:editor.content
       },()=>{
         ElMessage.success('帖子发布成功')
         success()
       },(meg)=>{
         ElMessage.warning(meg)
       })
     },
     type: Function
   }
 })
const editor=reactive({
  type:'',
  content:'',
  tittle:'',
  uploading:false,
})
 const store=useStore()
const emit=defineEmits(['close','success'])
 const editorRef=ref()


const cleanEditor=()=>{
   if(props.defaultContent) editor.content=new Delta(JSON.parse(props.defaultContent))
   else editorRef.value.setContents('','user')
  editor.type=props.defaultType
  editor.tittle=props.defaultTittle
 }
 function deltaToText(delta){
   if(!delta.ops) return ''
   let str=''
   for(let op of delta.ops){
     str+=op.insert
   }
   return str.replace(/\s/g,'')
 }
let textLen=computed(() =>deltaToText(editor.content).length)

 Quill.register('modules/imageResize',imageResize)
 Quill.register('modules/ImageExtend',ImageExtend)
 window.Quill = Quill
 const editorOption = {
   modules: {
     toolbar: {
       container: [
         "bold", "italic", "underline", "strike","clean",
         {color: []}, {'background': []},
         {size: ["small", false, "large", "huge"]},
         { header: [1, 2, 3, 4, 5, 6, false] },
         {list: "ordered"}, {list: "bullet"}, {align: []},
         "blockquote",  "link", "image",
         { indent: '-1' }, { indent: '+1' }
       ],
       handlers: {
         'image': function () {
           QuillWatch.emit(this.quill.id)
         }
       }
     },
     imageResize: {
       modules: [ 'Resize', 'DisplaySize' ]
     },
     ImageExtend: {
       action:  axios.defaults.baseURL + '/api/images/cache',
       name: 'file',
       size: 5,
       loading: true,
       accept: 'image/png, image/jpeg',
       response: (resp) => {
         if(resp.data) {
           return axios.defaults.baseURL + '/images' + resp.data
         } else {
           return null
         }
       },
       methods: 'POST',
       headers: xhr => {
         xhr.setRequestHeader('Authorization', accessHeader().Authorization);
       },
       start: () => editor.uploading = true,
       success: () => {
         ElMessage.success('图片上传成功!')
         editor.uploading = false
       },
       error: () => {
         ElMessage.warning('图片上传失败，请联系管理员!')
         editor.uploading = false
       }
     }
   }
 }

 const postTopic=()=>{
   let text=deltaToText(editor.content)
   if(text.length>2000) {
     ElMessage.warning('帖子内容不能超过2000字')
     return
   }
   if(text.length===0) {
     ElMessage.warning('帖子内容不能为空')
     return
   }
   if(!editor.tittle) {
     ElMessage.warning('帖子标题不能为空')
     return
   }

   if(!editor.type) {
     ElMessage.warning('请选择帖子类型')
     return
   }
   props.submit(editor,()=>emit('success'))
 }
</script>

<template>
<div>
  <el-drawer :model-value="show"
             @open="cleanEditor"
             direction="btt" size="650px" :close-on-click-modal="false" @close="emit('close')" >
    <template #header>
      <div>
        <div style="font-weight: bolder;">发表新的帖子</div>
        <div style="color: grey;font-size: 12px;">发表帖子请遵守相关法律规定。严禁谩骂、传播暴力等违法行为</div>
      </div>
    </template>
    <div style="display: flex">
      <div>
        <el-select placeholder="选择主题类型... " v-model="editor.type"  style="margin-right: 10px; width: 150px;" :disabled="!store.forum.types.length" >
          <el-option v-for="item in store.forum.types.filter(type=>type.id>0)" :value="item.id" :label="item.name"></el-option>
        </el-select>
      </div>
      <div style="flex: 1">
        <el-input :prefix-icon="Document" placeholder="请输入标题..." v-model="editor.tittle"></el-input>
      </div>
    </div>
    <div style="margin-top: 20px; "  v-loading="editor.uploading"
         element-loading-text="这种上传图片，请稍后...">
      <quill-editor v-model:content="editor.content" :content-type="'delta'"
                    :options="editorOption" ref="editorRef"
                    placeholder="emm...说点什么呢"></quill-editor>
    </div>
    <div style="display: flex; justify-content: space-between; margin-top: 10px;">
      <div>{{textLen}}/2000字</div>
      <div>
        <el-button type="success" plain :icon="Check" @click="postTopic">{{submitButton}}</el-button>
      </div>
    </div>
  </el-drawer>
</div>
</template>

<style scoped>
:deep(.el-drawer){
  margin: auto;
  width: 900px;
  border-radius: 8px;
}
:deep(.el-drawer__header){
  margin: 0;
}
:deep(.ql-toolbar){
  border-color: var(--el-border-color);
  border-radius: 8px 8px 0 0;
}
:deep(.ql-editor.ql-blank::before){
  color: var(--el-text-color-placeholder);
  font-style: normal;
}
:deep(.ql-editor){
  font-size: 15px;
  height: 400px;
}
:deep(.ql-container){
  border-color: var(--el-border-color);
  border-radius: 0 0 8px 8px;
}
</style>