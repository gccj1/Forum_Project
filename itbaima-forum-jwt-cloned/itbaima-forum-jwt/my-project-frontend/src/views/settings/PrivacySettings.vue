<script setup>
import Card from "@/components/Card.vue";
import {reactive, ref} from "vue";
import {Message, Setting, Lock, Refresh,Edit} from "@element-plus/icons-vue";
import {get, post} from "@/net";
import {ElMessage} from "element-plus";
import {useStore} from "@/store";
import router from "@/router";
const headers = reactive({
  icon:Setting,
  tittle: "个人隐私设置",
  desc:"您可以在这里修改想要显示的隐私设置",
})
const passwordForm= reactive({
  password: '',
  repeatPassword: '',
  email: '',
  code: '',
})
const privacyForm=reactive({
  phone:Boolean,
  gender:Boolean,
  email:Boolean,
  qq:Boolean,
  wx:Boolean,

})
get("api/user/privacy",(data)=>{
    privacyForm.phone=data.phone
    privacyForm.gender=data.gender
    privacyForm.email=data.email
    privacyForm.qq=data.qq
    privacyForm.wx=data.wx
  privacyLoading.value=false
})
  const savePrivacy=()=>{
  privacyLoading.value=true
  post('/api/user/save-privacy',privacyForm,(data)=>{
    ElMessage.success('个人隐私设置修改成功')
    privacyLoading.value=false
  },(meg)=>{
    ElMessage.error(meg)
    privacyLoading.value=false
  })

  }
const coldTime=ref(0)
const privacyLoading=ref(true)
const isEmailValid = ref(false)
const store = useStore()
const passwordRef=ref()
const confirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if(value!==passwordForm.password){
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}
const onValidate = (prop, isValid) => {
  if(prop === 'email')
    isEmailValid.value = isValid
}

const sendEmail = () => {
  coldTime.value = 60
  get(`/api/auth/ask-code?email=${passwordForm.email}&type=reset`, () => {
    ElMessage.success(`验证码已发送，请注意查收`)
    const handle = setInterval(() => {
      coldTime.value--
      if(coldTime.value === 0) {
        clearInterval(handle)
      }
    }, 1000)
  }, (message) => {
    coldTime.value = 0
    ElMessage.warning(message)
  })
}
const doReset = () => {
  passwordRef.value.validate((isValid) => {
    if(isValid) {
      post('/api/auth/reset-password', {
        email: passwordForm.email,
        code: passwordForm.code,
        password: passwordForm.password,
      }, () => {
        ElMessage.success('密码重置成功')
      },(meg)=>{
        ElMessage.warning(meg)
      })
    }
    else{
      ElMessage.warning('请正确填写表单')
    }
  })
}

const rules = reactive({
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur', 'change'] }
  ],
  repeatPassword: [
    {required: true, validator:confirmPassword,trigger:['blur','change']}
  ],
  email:[
    { required: true, message: '请输入邮件地址', trigger:[ 'blur'] },
    {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change']}
  ],
  code: [
    { required: true, message: '请输入获取的验证码', trigger: 'blur' },
    {min: 6, max: 6, message: '验证码的长度必须是6个字符', trigger: ['blur', 'change']}
  ],
})



</script>

<template>
<div style=" max-width: 650px; margin: 0 auto;">
  <div style="margin-bottom: 20px;">
    <card  :icon="headers.icon" :title="headers.tittle" :desc="headers.desc" v-loading="privacyLoading">
      <div style="display: flex; flex-direction: column; margin: 10px 0 10px 20px;">
        <el-checkbox v-model="privacyForm.gender" label="公开你的性别"></el-checkbox>
        <el-checkbox v-model="privacyForm.phone" label="公开你的手机号"></el-checkbox>
        <el-checkbox v-model="privacyForm.email" label="公开你的邮箱"></el-checkbox>
        <el-checkbox v-model="privacyForm.qq" label="公开你的QQ"></el-checkbox>
        <el-checkbox v-model="privacyForm.wx" label="公开你的WX"></el-checkbox>
        <div style="text-align: center; margin-top: 20px;">
          <el-button style="width: 300px;" :icon="Refresh" @click="savePrivacy" plain type="success">保存设置</el-button>
        </div>
      </div>
    </card>
  </div>

  <card :icon="Setting" title="重置密码" desc="在这里重置您的密码,请牢记您的密码">
    <el-form ref="passwordRef" :rules="rules" @validate="onValidate" :model="passwordForm" label-width="120px" status-icon>
<!--      <el-form-item label="旧密码" prop="oldPassword">-->
<!--        <el-input maxlength="20" type="password" v-model="passwordForm.oldPassword" :prefix-icon="Lock" placeholder="请输入账号的旧密码"></el-input>-->
<!--      </el-form-item>-->
      <el-form-item label="新密码" prop="password">
        <el-input maxlength="20" type="password" v-model="passwordForm.password" :prefix-icon="Lock" placeholder="请输入新密码"></el-input>
      </el-form-item>
      <el-form-item label="再次输入密码" prop="repeatPassword">
        <el-input maxlength="20" type="password" v-model="passwordForm.repeatPassword" :prefix-icon="Lock" placeholder="再次输入新密码"></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input maxlength="20" v-model="passwordForm.email" :prefix-icon="Lock" placeholder="请输入邮箱"></el-input>
      </el-form-item>
      <el-form-item prop="code" label="验证码" >
        <el-row style="width: 100%" :gutter="10">
          <el-col :span="10">
            <el-input :prefix-icon="Edit"  placeholder="请输入验证码" v-model="passwordForm.code"/>
          </el-col>
          <el-col :span="12" >
            <el-button style="width: 100%;" type="success" @click="sendEmail"
                       :disabled="!isEmailValid || coldTime > 0">
              {{coldTime > 0 ?  coldTime + ' 秒' : '获取验证码'}}
            </el-button>
          </el-col>
        </el-row>
      </el-form-item>
      <div style="text-align: center">
        <el-button style="width: 300px;" :icon="Refresh" @click="doReset" plain type="danger">重置密码</el-button>
      </div>
    </el-form>
  </card>
</div>
</template>

<style scoped>

</style>