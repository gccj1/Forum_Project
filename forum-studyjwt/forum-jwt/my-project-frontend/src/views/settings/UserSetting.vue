<script setup>
import Card from "@/components/Card.vue";
import {Message, Refresh, Select, User, Lock, Upload} from "@element-plus/icons-vue";
import {useStore} from "@/store";
import {computed, reactive, ref} from "vue";
import {accessHeader, get, post} from "@/net";
import {ElMessage} from "element-plus";
import router from "@/router";
import axios from "axios";

get('api/user/details',(data)=>{
    baseForm.gender=data.gender
    baseForm.phone=data.phone
    baseForm.qq=data.qq
    baseForm.wx=data.wx
    baseForm.text=data.text
   desc.value=data.text
   baseFormLoading.value=false

},)
const store = useStore()
const baseForm=reactive({
  username:store.user.username,
  gender:0,
  phone:'',
  qq:'',
  wx:'',
  text:'',
})

const emailForm=reactive({
  email:store.user.email,
  code:'',
})
const isEmail=ref(true)
const headers = reactive({
  icon:Message,
  tittle: "电子邮件设置",
  desc:"您可以在这里修改默认绑定的电子邮件地址",
})
const baseRef = ref()
const emailRef = ref()
const desc=ref()

const isEmailValid = ref(false)

const coldTime = ref(0)
const baseFormLoading=ref(true)
const saveDetails= ()=>{
  baseRef.value.validate((isValid) => {
    if(isValid) {
      baseFormLoading.value=true
      post('/api/user/save-details', baseForm,() => {
        ElMessage.success('保存成功')
        store.user.username=baseForm.username
        desc.value=baseForm.text
        baseFormLoading.value=false
      })
    } else {
      ElMessage.warning('请正确填写表单内容！')
    }
  })
}
const onValidate = (prop, isValid) => {
  if(prop === 'email')
    isEmailValid.value = isValid
}
const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'))
  } else if(!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)){
    callback(new Error('用户名不能包含特殊字符，只能是中文/英文'))
  } else {
    callback()
  }
}
const sendEmail = () => {
  coldTime.value = 60
  get(`/api/auth/ask-code?email=${emailForm.email}&type=modify`, () => {
    ElMessage.success(`验证码已发送，请注意查收`)
    const handle = setInterval(() => {
      coldTime.value--
      if(coldTime.value === 0) {
        clearInterval(handle)
      }
    }, 1000)
  },  (message) => {
    ElMessage.warning(message)
    coldTime.value = 0
  })
}
const modifyEmail = () => {
  emailRef.value.validate((isValid) => {
    if(isValid) {
      post('/api/user/email-modify', emailForm, () => {
        ElMessage.success('邮箱修改成功')
        store.user.email=emailForm.email
        coldTime.value=0
        emailForm.code=''
      },(meg)=>{
        ElMessage.warning(meg)
      })
    } else {
      ElMessage.warning('请正确填写表单内容！')
    }
  })
}

const rules = {
  username: [
    { required:true, validator: validateUsername, trigger: ['blur', 'change'] },
    { min: 4, max: 10, message: '用户名的长度必须在4-10个字符之间', trigger: ['blur', 'change'] },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur', 'change'] }
  ],
  email: [
    { required: true, message: '请输入邮件地址', trigger: 'blur' },
    {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change']}
  ],
  code: [
    { required: true, message: '请输入获取的验证码', trigger: 'blur' },
    {min: 6, max: 6, message: '验证码的长度必须是6个字符', trigger: ['blur', 'change']}
  ]
}
const changePage = (page) => {
  isEmail.value=!isEmail.value
  headers.icon = isEmail.value ? Message : Lock
  headers.tittle= isEmail.value ? "电子邮件设置":"重置密码设置"
  headers.desc= isEmail.value ? "您可以在这里修改默认绑定的电子邮件地址":"在这里重置您的密码"
}
const  beforeUpload = (file) => {
  if(file.type!== 'image/jpeg' && file.type !== 'image/png'&& file.type !== 'image/gif'){
    ElMessage.error('请上传正确的图片格式')
    return false
  }
  else if(file.size / 1024 > 1024){
    ElMessage.error('图片大小不能超过1M')
    return false
  }
  return true
}
const handleAvatarSuccess = (res) => {
  ElMessage.success('头像上传成功')
  store.user.avatar = res.data
}
const registerTime = computed(() => new Date(store.user.registerTime).toLocaleString())
const dialogVisible = ref(false)
</script>

<template>
  <div style="display: flex; max-width: 950px;margin: 0 auto;">
    <div class="settings-left">
      <card :icon="User" v-loading="baseFormLoading" title="账号信息设置" desc="在这里编辑您的个人信息，您可以在隐私设置中选择是否展示这些信息">
        <el-form ref="baseRef" :rules="rules" label-position="top" :model="baseForm" status-icon style="margin: 0 10px 10px 10px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="baseForm.username"/>
          </el-form-item>
          <el-form-item label="性别" >
            <el-radio-group v-model="baseForm.gender">
              <el-radio :label="0">男</el-radio>
              <el-radio :label="1">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="手机号" >
            <el-input v-model="baseForm.phone" maxlength="11" minlength="11" show-word-limit/>
          </el-form-item>
          <el-form-item label="QQ号"  >
            <el-input v-model="baseForm.qq" maxlength="15"/>
          </el-form-item>
          <el-form-item label="微信号"  >
            <el-input v-model="baseForm.wx" maxlength="15"/>
          </el-form-item>
          <el-form-item label="个人简介" >
            <el-input type="textarea" :rows="6" v-model="baseForm.text" maxlength="300"/>
          </el-form-item>
          <div>
            <el-button :icon="Select" @click="saveDetails" type="success">保存用户信息</el-button>
          </div>
        </el-form>
      </card>
      <card style="margin-top: 10px" :is-email="isEmail"  :icon="headers.icon" :title="headers.tittle" :desc="headers.desc">
        <template #change> |
          <el-button  @click="changePage" plain   type="success"  >{{isEmail ? '重置密码?':'修改邮箱?'}}</el-button>
        </template>
        <el-form v-if="isEmail" ref="emailRef" @validate="onValidate" :model="emailForm" :rules="rules" label-position="top" style="margin: 0 10px 10px 10px">
          <el-form-item label="电子邮件" prop="email">
            <el-input v-model="emailForm.email"/>
          </el-form-item>
          <el-form-item prop="code">
            <el-row style="width: 100%" :gutter="10">
              <el-col :span="18">
                <el-input placeholder="请获取验证码" v-model="emailForm.code"/>
              </el-col>
              <el-col :span="6">
                <el-button type="success" @click="sendEmail"
                           :disabled="!isEmailValid || coldTime > 0">
                  {{coldTime > 0 ? '请稍后 ' + coldTime + ' 秒' : '获取验证码'}}
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <div>
            <el-button :icon="Refresh" @click="modifyEmail" type="success">更新电子邮件</el-button>
          </div>
        </el-form>
        <el-form v-else ref="baseRef" :rules="rules" label-position="top" :model="baseForm" status-icon style="margin: 0 10px 10px 10px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="baseForm.username"/>
          </el-form-item>
          <el-form-item label="性别" >
            <el-radio-group v-model="baseForm.gender">
              <el-radio :label="0">男</el-radio>
              <el-radio :label="1">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="手机号" >
            <el-input v-model="baseForm.phone" maxlength="11" minlength="11" show-word-limit/>
          </el-form-item>
          <el-form-item label="QQ号"  >
            <el-input v-model="baseForm.qq" maxlength="15"/>
          </el-form-item>
          <el-form-item label="微信号"  >
            <el-input v-model="baseForm.wx" maxlength="15"/>
          </el-form-item>
          <el-form-item label="个人简介" >
            <el-input type="textarea" :rows="6" v-model="baseForm.text" maxlength="300"/>
          </el-form-item>
          <div>
            <el-button :icon="Select" @click="saveDetails" type="success">保存用户信息</el-button>
          </div>
        </el-form>
      </card>
    </div>
    <div class="settings-right">
      <div style="position: sticky;top: 20px">
        <card>
          <div style="text-align: center;padding: 5px 15px 0 15px">
            <el-avatar class="image-viewer" :size="70" @click="dialogVisible=true" shape="square" :src="store.avatarUrl"/>
            <el-dialog v-model="dialogVisible" >
            <el-image-viewer
                :url-list="[store.avatarUrl]"
                @close="dialogVisible=false"></el-image-viewer>
            </el-dialog>
            <div style="margin:5px 0;">
              <el-upload
                  :action="axios.defaults.baseURL+'/api/images/avatar'"
                  :before-upload="beforeUpload"
                  :on-success="handleAvatarSuccess"
                  :headers="accessHeader()">
                <el-button  size="small" type="info" plain >修改头像</el-button>
              </el-upload>
            </div>
            <div style="font-weight: bold">你好, {{store.user.username}}</div>
          </div>
          <el-divider style="margin: 10px 0"/>
          <div style="font-size: 14px;color: grey;padding: 10px">
              <div>{{ desc || "这个用户很懒,还没有自我介绍~"}}</div>
          </div>
        </card>
        <card style="margin-top: 10px;font-size: 14px">
          <div>账号注册时间: {{registerTime}}</div>
          <div style="color: grey">欢迎加入我们的学习论坛！</div>
        </card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.image-viewer{
  &:hover{
    cursor: pointer;
  }
}
.settings-left {
  flex: 1;
  margin: 20px;
}

.settings-right {
  width: 300px;
  margin: 20px 30px 20px 0;
}
</style>
