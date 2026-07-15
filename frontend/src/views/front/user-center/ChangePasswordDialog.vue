<template>
    <el-dialog v-model="visible" title="修改登陆密码" width="40%">

        <el-form :rules="rules" :model="form" label-width="100px" ref="formRef" size="large">
            <el-form-item label="旧密码" prop="oldPassword" required>
                <el-input v-model="form.oldPassword" type="password" show-password placeholder="请输入旧密码" />
            </el-form-item>

            <el-form-item label="新密码" prop="newPassword" required>
                <el-input v-model="form.newPassword" type="password" show-password placeholder="请输入新密码" />
            </el-form-item>

            <el-form-item label="确认密码" prop="confirmPassword" required>
                <el-input v-model="form.confirmPassword" type="password" show-password placeholder="请确认新密码" />
            </el-form-item>
        </el-form>

        <template #footer>
            <div class="dialog-footer">
                <el-button size="large" @click="visible = false">取消</el-button>
                <el-button type="primary" size="large" @click="onsubmit" :loading="btnLoading">
                    确认修改
                </el-button>
            </div>
        </template>

    </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api/user-api'
import { useUserStore } from '@/stores/user'
import router from '@/router'

// 暴露方法给父组件
defineExpose({
    showModel
})

// 弹窗控制
const visible = ref(false)
const btnLoading = ref(false)
const formRef = ref()

// 表单数据
const form = reactive({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
})

/**
 * ✅ 自定义校验：确认密码
 */
const validateConfirm = (rule: any, value: string, callback: any) => {
    if (!value) {
        callback(new Error('请确认密码'))
    } else if (value !== form.newPassword) {
        callback(new Error('两次输入密码不一致'))
    } else {
        callback()
    }
}

// 校验规则
const rules = reactive({
    oldPassword: [
        { required: true, message: '请输入旧密码', trigger: 'blur' }
    ],
    newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, message: '密码至少6位', trigger: 'blur' }
    ],
    confirmPassword: [
        { validator: validateConfirm, trigger: 'blur' }
    ]
})

const userStore = useUserStore()

/**
 * 打开弹窗
 */
function showModel() {
    form.oldPassword = ''
    form.newPassword = ''
    form.confirmPassword = ''
    // 清空校验状态
    formRef.value?.clearValidate()
    visible.value = true
}

/**
 * 提交
 */
function onsubmit() {
    formRef.value.validate().then(async () => {
        try {
            btnLoading.value = true
            if (form.newPassword === form.oldPassword) {
                ElMessage.error('新密码不能与旧密码相同')
                return
            }

            await userApi.changePassword(form)

            ElMessage.success('密码修改成功，请重新登录')
            // 关闭弹窗
            visible.value = false
            // 退出登录,清空登陆信息并跳转登陆页面
            userStore.logout()
            router.push('/user/login')
        }
        finally {
            btnLoading.value = false
        }
    })
}
</script>

<style scoped lang="less">
.dialog-footer {
    text-align: right;
}

.el-input {
    width: 100%;
}

/* 让整体更紧凑一点 */
.el-form-item {
    margin-bottom: 40px;
}
</style>