<template>
    <div class="container">
        <div class="container-content">
            <div class="container-content-bg">
                <div class="container-content-top">
                    <div>
                        <div style="text-align: center; padding-bottom: 25px;"><img alt="logo" class="logo"
                                src="@/assets/images/login/logo.png"></div>
                        <span class="-title">欢迎使用</span>
                    </div>
                </div>

                <div class="container-content-main">
                    <el-form :model="form" ref="formRef" :rules="rules" size="large">
                        <el-form-item prop="username" height="80px;">
                            <el-input :prefix-icon="User" v-model="form.username"
                                style="width: 368px;height: 40px;font-size: 16px;" placeholder="用户名" />
                        </el-form-item>
                        <el-form-item prop="userpassword" height="80px;">
                            <el-input :prefix-icon="Lock" v-model="form.userpassword"
                                style="width: 368px;height: 40px;font-size: 16px;" placeholder="密码" show-password
                                type="password" />
                        </el-form-item>
                        <el-form-item prop="captchaCode">
                            <div style="display: flex; justify-items: flex-start;">
                                <el-input v-model="form.captchaCode" style="width: 268px;height: 40px;font-size: 16px;"
                                    placeholder="验证码" />
                                <img :src="captchaImg">
                            </div>

                        </el-form-item>


                        <el-form-item>
                            <el-button style="width: 368px;height: 40px;font-size: 16px;" type="primary"
                                :loading="btnLoading" @click="onSubmit">登录</el-button>
                        </el-form-item>
                        <!-- 管理员登录界面跳转 -->
                        <el-form-item>
                            <div style="width:100%;font-size:14px;">
                                <span style="color:#409EFF;cursor:pointer;float:left;" @click="goAdminLogin">
                                    管理员登录
                                </span>
                                <span style="color:#409EFF;cursor:pointer;float:right" @click="goRegister">
                                    用户注册
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
import { commonApi } from '@/api/common-api'
import { ref, reactive, onMounted } from 'vue'
import { userApi } from '@/api/user-api'
import router from '@/router'
import { useUserStore } from '@/stores/user'

const captchaImg = ref('')
const form = reactive({
    username: undefined,
    userpassword: undefined,
    captchaId: undefined,
    captchaCode: undefined,
})
const formRef = ref()
const rules = reactive({
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
    ],
    userpassword: [
        { required: true, message: '请输入密码', trigger: 'blur' },
    ],
    captchaCode: [
        { required: true, message: '请输入验证码', trigger: 'blur' },
    ],
})
const btnLoading = ref(false)


async function getCaptcha() {
    try {
        let captchaResult = await commonApi.getCaptcha()
        captchaImg.value = captchaResult.data.captchaImage
        form.captchaId = captchaResult.data.captchaId
    } catch (error) {
    }
}

function goAdminLogin() {
    router.push('/admin/login')
}

async function goRegister() {
    router.push('/user/register')
}
function onSubmit() {
    //validate()方法返回一个Promise对象，当所有校验规则都通过时，Promise会被resolve，否则会被reject
    formRef.value.validate().then(async () => {
        try {
            btnLoading.value = true;
            let loginResult = await userApi.login(form);
            // 把登陆对象信息存入user状态库中
            useUserStore().setUserInfo(loginResult.data)
            //console.log("登录返回数据:", useUserStore().user.headurl)


            // 路由跳转
            router.push('/user')

        } catch (e: any) {
            if (e.data && e.data.code !== 200) {
                getCaptcha()
            }
        } finally {
            btnLoading.value = false;

        }
    })
    console.log("form.username" + form.username)
}

// 页面加载完后获取验证码
onMounted(getCaptcha)

</script>

<style lang="less" scoped>
body {
    margin: 0px;
}

.container {
    display: flex;
    flex-direction: column;
    height: 100vh;
    margin: 0px;
    background-image: url(@/assets/images/login/bg.jpg);
    background-repeat: no-repeat;
    background-size: 100% 100%;
    align-items: center;
    justify-content: center;

    &-content {
        width: 460px;
        margin: 0 auto;
        padding: 6px;
        background: hsla(0, 0%, 100%, .4);
        border-radius: 15px;

        &-bg {
            padding-top: 36px;
            padding-bottom: 36px;
            background-color: #fff;
            border-radius: 15px;
        }

        &-top {
            text-align: center;
            padding-bottom: 40px;
        }

        &-main {
            width: 368px;
            margin: 0 auto;
        }
    }
}


.logo {
    height: 60px;
    vertical-align: top;
    margin-right: 16px;
}

.title {
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