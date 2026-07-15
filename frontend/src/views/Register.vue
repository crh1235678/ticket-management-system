<template>
  <div class="container">
    <div class="container-content">
      <div class="container-content-bg">
        <div class="container-content-top">
          <div>
            <div style="text-align: center; padding-bottom: 25px;">
              <img alt="logo" class="logo" src="@/assets/images/login/logo.png">
            </div>
            <span class="title">用户注册</span>
          </div>
        </div>

        <div class="container-content-main">
          <el-form :model="form" ref="formRef" :rules="rules" size="large">

            <!-- 用户名 -->
            <el-form-item prop="username">
              <el-input
                :prefix-icon="User"
                v-model="form.username"
                style="width: 368px;height: 40px;font-size: 16px;"
                placeholder="用户名" />
            </el-form-item>

            <!-- 密码 -->
            <el-form-item prop="userpassword">
              <el-input
                :prefix-icon="Lock"
                v-model="form.userpassword"
                style="width: 368px;height: 40px;font-size: 16px;"
                placeholder="密码"
                show-password
                type="password" />
            </el-form-item>

            <!-- 确认密码 -->
            <el-form-item prop="confirmPassword">
              <el-input
                :prefix-icon="Lock"
                v-model="form.confirmPassword"
                style="width: 368px;height: 40px;font-size: 16px;"
                placeholder="确认密码"
                show-password
                type="password" />
            </el-form-item>

            <!-- 验证码 -->
            <el-form-item prop="captchaCode">
              <div style="display: flex;">
                <el-input
                  v-model="form.captchaCode"
                  style="width: 268px;height: 40px;font-size: 16px;"
                  placeholder="验证码" />
                <img
                  :src="captchaImg"
                  style="margin-left:10px;cursor:pointer;height:40px;"
                  @click="getCaptcha" />
              </div>
            </el-form-item>

            <!-- 注册按钮 -->
            <el-form-item>
              <el-button
                style="width: 368px;height: 40px;font-size: 16px;"
                type="primary"
                :loading="btnLoading"
                @click="onSubmit">
                注册
              </el-button>
            </el-form-item>

            <!-- 底部跳转 -->
            <el-form-item>
              <div style="width:100%;font-size:14px;text-align:right;">
                <span
                  style="color:#409EFF;cursor:pointer;"
                  @click="goLogin">
                  已有账号？去登录
                </span>
              </div>
            </el-form-item>

          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { User, Lock } from '@element-plus/icons-vue'
import { ref, reactive, onMounted } from 'vue'
import router from '@/router'
import { commonApi } from '@/api/common-api'
import { userApi } from '@/api/user-api'
import { ElMessage } from 'element-plus'

const captchaImg = ref('')
const btnLoading = ref(false)
const formRef = ref()

const form = reactive({
  username: '',
  userpassword: '',
  confirmPassword: '',
  captchaId: '',
  captchaCode: '',
})

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value !== form.userpassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  userpassword: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  captchaCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
})

async function getCaptcha() {
  try {
    const result = await commonApi.getCaptcha()
    captchaImg.value = result.data.captchaImage
    form.captchaId = result.data.captchaId
  } catch (e) {}
}

function goLogin() {
  router.push('/user/login')
}

function onSubmit() {
  formRef.value.validate().then(async () => {
    try {
      btnLoading.value = true

      await userApi.register(form)

      ElMessage.success('注册成功，请登录')
      router.push('/user/login')

    } catch (e: any) {
      getCaptcha()
    } finally {
      btnLoading.value = false
    }
  })
}

onMounted(getCaptcha)
</script>

<style lang="less" scoped>
body{
    margin: 0px;
}

.container{
    display: flex;
    flex-direction: column;
    height: 100vh;
    margin:0px;
    background-image: url(@/assets/images/login/bg.jpg);
    background-repeat: no-repeat;
    background-size: 100% 100%;
    align-items: center;
    justify-content: center;
    &-content{
        width:460px;
        margin: 0 auto;
        padding: 6px;
        background: hsla(0, 0%, 100%, .4);
        border-radius: 15px;
        &-bg{
            padding-top: 36px;
            padding-bottom: 36px;
            background-color: #fff;
            border-radius: 15px;
        }      
        &-top{
            text-align: center;
            padding-bottom: 40px;
        }
        &-main{
            width: 368px;
            margin: 0 auto;
        }
    }
}


.logo{
    height: 60px;
    vertical-align: top;
    margin-right: 16px;
}

.title{
    font-size: 26px;
    color: rgba(0, 0, 0, .85);
    font-family: Myriad Pro, Helvetica Neue, Arial, Helvetica, sans-serif;
    font-weight: 600;
    position: relative;
    top: 2px;
}

input:-internal-autofill-selected {
    background-color: transparent !important;
    background-image: none !important;
    color: rgb(255, 255, 255) !important;
}

input:-webkit-autofill,
input:-webkit-autofill:hover,
input:-webkit-autofill:focus,
input:-webkit-autofill:active {
    transition-delay: 50000s;
    transition: background-color 50000s ease-out;
    -webkit-transition-delay: 50000s;
    -webkit-transition: background-color 50000s ease-out;
}
</style>