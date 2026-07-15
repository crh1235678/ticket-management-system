<template>
    <div class="profile-page">

        <div class="user-card">
            <!-- 头像 -->
            <div class="avatar-area">
                <FileUpload :width="120" :height="120" :round="true" fit="fill" :defaultFileList="defaultFileList"
                    @change="uploadFileChange" />
            </div>

            <!-- 用户信息 -->
            <div class="user-info">
                <div class="username">用户名：{{ user.username }}</div>
                <div class="desc">UID：{{ 1000000000 + user.id }}</div>
                <!-- ⭐ 余额区域 -->
                <div class="balance-row">
                    <span class="balance-label">余额：</span>
                    <span class="balance-value">￥{{ user.account }}</span>

                    <el-button type="success" size="small" plain @click="openRecharge">
                        充值
                    </el-button>
                </div>
            </div>

            <!-- 右侧操作 -->
            <div class="user-actions">
                <el-button type="primary" plain @click="openChangePassword">
                    修改密码
                </el-button>
            </div>
        </div>

        <!-- 资料编辑 -->
        <el-card>

            <template #header>
                <div class="card-header">
                    <span>个人资料</span>
                </div>
            </template>

            <el-form :model="updateForm" label-width="80px" class="profile-form">
                <el-form-item label="姓名">
                    <el-input v-model="updateForm.name" />
                </el-form-item>

                <el-form-item label="邮箱">
                    <el-input v-model="updateForm.email" />
                </el-form-item>

                <el-form-item label="手机号">
                    <el-input v-model="updateForm.telephone" />
                </el-form-item>

                <el-form-item label="性别">
                    <el-radio-group v-model="updateForm.sex">
                        <el-radio label="男" />
                        <el-radio label="女" />
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="个人简介">
                    <el-input type="textarea" rows="3" v-model="updateForm.introduce" />
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" @click="saveUser">保存修改</el-button>
                </el-form-item>

            </el-form>

        </el-card>
        <ChangePassword ref="changePasswordRef" />
        <Recharge ref="rechargeRef" @rechargeSuccess="getUserInfo" />

    </div>
</template>

<script setup lang="ts">

import { reactive, onMounted, ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user-api'
import { ElMessage } from 'element-plus'
import FileUpload from '@/components/FileUpload.vue'
import { urls2FileUrl } from '@/utils/utils'
import CONSTANTS from '@/utils/constants'
import ChangePassword from '@/views/front/user-center/ChangePasswordDialog.vue'
import Recharge from '@/views/front/user-center/RechargeDialog.vue'



const userStore = useUserStore()
const defaultFileList = ref([])
//定义引用新增或修改组件的响应式变量
const changePasswordRef = ref();
const rechargeRef = ref();


// 用户对象
const user = reactive({
    id: '',
    username: '',
    name: '',
    headurl: '',
    email: '',
    telephone: '',
    sex: '',
    account: '',
    introduce: ''
})
// 提交表单对象
// 通过将展示数据和提交数据分离，实现前端数据解耦，只向后端传递允许修改的字段，减少敏感字段被误传或篡改的风险。同时，前后端DTO保持一致，数据结构清晰，可视化度更高。
const updateForm = reactive({
    name: '',
    email: '',
    telephone: '',
    sex: '',
    introduce: '',
    headurl: ''
})




/**
 * 查询用户信息
 */
async function getUserInfo() {
    try {

        const id = userStore.user.id
        const res = await userApi.getUserInfo()
        Object.assign(user, res.data)
        // 初始化表单数据
        Object.assign(updateForm, user)
        // 初始化头像回显
        defaultFileList.value = urls2FileUrl(user.headurl)

    } catch (error) {
    }

}

/**
 * 保存修改
 */
async function saveUser() {
    try {
        // 如果没有头像就使用默认头像
        if (!user.headurl) {
            user.headurl = CONSTANTS.DEFAULT_AVATAR
        }
        await userApi.update(updateForm)
        //console.log("旧的token:", userStore.token)

        // 更新 store 中的用户信息
        userStore.updateUserInfo(updateForm)

        //console.log("新的token:", userStore.token)

        ElMessage.success('修改成功')
        // 刷新用户信息
        getUserInfo()
    } catch (error) {

    }
}

// 将上传文件的name赋值给对象的headurl
function uploadFileChange(fileList: any) {
    if (fileList && fileList.length > 0) {
        user.headurl = fileList.map((o: { name: any; }) => o.name).toString();
    } else {
        user.headurl = '' as any;
        defaultFileList.value = [];
    }
}

/**
 * 跳转充值页面
 */
function openRecharge() {
    rechargeRef.value.showModel();
    // 刷新用户信息
    getUserInfo()

}

function openChangePassword() {
    changePasswordRef.value.showModel();

}

// 页面加载
onMounted(() => {
    getUserInfo()
})

</script>

<style scoped lang="less">
.user-card {
    display: flex;
    align-items: center;
    padding: 25px;
    background: white;
    border-radius: 10px;
    margin-bottom: 20px;
}

/* 用户信息 */
.user-info {
    margin-left: 20px;
    display: flex;
    flex-direction: column;
    gap: 8px;
}

/* ⭐ 余额一行 */
.balance-row {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-top: 5px;
}

/* 标签 */
.balance-label {
    color: #666;
    font-size: 20px;
}

/* 金额（重点🔥） */
.balance-value {
    font-size: 20px;
    font-weight: bold;
    color: #f56c6c; // 红色更像金额
}

/* 操作区 */
.user-actions {
    margin-left: auto;
    display: flex;
    align-items: center;

    .el-button {
        border-radius: 20px;
        padding: 8px 18px;
    }
}
</style>