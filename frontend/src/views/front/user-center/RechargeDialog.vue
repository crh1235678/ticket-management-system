<template>
    <el-dialog v-model="visible" title="账户充值" width="40%">

        <el-form 
            :model="form" 
            :rules="rules" 
            ref="formRef" 
            label-width="90px"
            size="large"
        >
            <!-- 充值金额 -->
            <el-form-item label="充值金额" prop="amount" required>
                <el-input 
                    v-model="form.amount" 
                    placeholder="请输入充值金额"
                >
                    <template #prefix>￥</template>
                </el-input>
            </el-form-item>

            <!-- 支付方式 -->
            <el-form-item label="支付方式" prop="payType" required>
                <el-radio-group v-model="form.payType" class="pay-type-group">
                    <el-radio  label="alipay">支付宝</el-radio>
                    <el-radio  label="wechat">微信</el-radio>
                </el-radio-group>
            </el-form-item>
        </el-form>

        <!-- footer -->
        <template #footer>
            <div class="dialog-footer">
                <el-button size="large" @click="visible = false">取消</el-button>
                <el-button 
                    type="primary" 
                    size="large" 
                    :loading="btnLoading"
                    @click="onSubmit"
                >
                    确认充值
                </el-button>
            </div>
        </template>

    </el-dialog>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api/user-api'


// 暴露给父组件
defineExpose({
    showModel
})


// 控制弹窗
const visible = ref(false)
const btnLoading = ref(false)
const formRef = ref()
// 定义事件
const emit = defineEmits(['rechargeSuccess'])

// 表单数据
const form = reactive({
    amount: '',
    payType: ''
})

// 校验规则
const rules = reactive({
    amount: [
        { required: true, message: '请输入充值金额', trigger: 'blur' },
        {
            validator: (rule: any, value: string, callback: any) => {
                if (!value) return callback()
                const num = Number(value)
                if (isNaN(num) || num <= 0) {
                    callback(new Error('请输入正确金额'))
                } else {
                    callback()
                }
            },
            trigger: 'blur'
        }
    ],
    payType: [
        { required: true, message: '请选择支付方式', trigger: 'change' }
    ]
})

/**
 * 打开弹窗
 */
function showModel() {
    form.amount = ''
    form.payType = ''
    formRef.value?.clearValidate()
    visible.value = true
}

/**
 * 提交
 */
function onSubmit() {
    formRef.value.validate().then(async () => {
        try {
            btnLoading.value = true

            await userApi.recharge(form)

            console.log('充值参数：', form)

            ElMessage.success(`充值￥${form.amount}成功（模拟）`)

            visible.value = false
            // 通知父组件刷新数据
            emit('rechargeSuccess')
        } finally {
            btnLoading.value = false
        }
    })
}
</script>
<style scoped lang="less">
.dialog-footer {
    text-align: right;
}

/* 输入框 */
.el-input {
    width: 100%;
}

/* 间距更舒服 */
.el-form-item {
    margin-bottom: 30px;
}

/* 支付方式按钮美化 */
.pay-type-group {
    width: 100%;
    display: flex;

    .el-radio-button {
        flex: 1;
        text-align: center;
    }
}
</style>