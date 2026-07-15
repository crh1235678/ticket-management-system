<template>
  <el-dialog v-model="visible" title="支付订单" width="400px" class="payment-dialog" :close-on-click-modal="false">
    <div class="info-box">
      <div class="row">
        <span class="label">应付金额：</span>
        <span class="value price">￥{{ amount }}</span>
      </div>
      <div class="row">
        <span class="label">账户余额：</span>
        <span class="value balance">￥{{ userBalance }}</span>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button size="medium" @click="cancel">取消支付</el-button>
        <el-button size="medium" type="warning" @click="laterPay">稍后支付</el-button>
        <el-button size="medium" type="primary" :loading="loading" @click="confirmPay">确认支付</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { userApi } from '@/api/user-api'
import { orderApi } from '@/api/order-api'
import { onMounted } from 'vue'
import { ElMessage } from 'element-plus'

defineExpose({ show })

const emits = defineEmits<{
  (e: 'success'): void
  (e: 'later'): void
  (e: 'close'): void
}>()

// 控制弹窗
const visible = ref(false)
const loading = ref(false)
const userBalance = ref(0)

// 父组件传入
const props = defineProps<{
  amount: number
  orderId: number
}>()

// 获取用户余额
async function getUserBalance() {
  try {
    const { data } = await userApi.getUserInfo()
    userBalance.value = data.account // 正确赋值
  } catch (err) {
    console.error('获取余额失败', err)
  }
}


// 打开弹窗
function show() {
  visible.value = true
  getUserBalance()
}

// 取消支付
async function cancel() {
  try {
    loading.value = true
    // 调用后端取消订单接口
    console.log(props.orderId)
    await orderApi.cancel(props.orderId)
    // 这里可以提示用户取消成功
    ElMessage.success('订单取消成功')
    // 关闭弹窗
    visible.value = false
  } catch (error) {
    ElMessage.error('取消订单失败')
  } finally {
    emits('close')
    loading.value = false
  }
}

// 稍后支付
function laterPay() {
  emits('later')
  visible.value = false
}
// 确认支付
async function confirmPay() {
  if (props.amount > userBalance.value) {
    ElMessage.error('余额不足')
    return
  }

  try {
    loading.value = true

    // 在这里调用支付接口
    await orderApi.pay(props.orderId)

    ElMessage.success('支付成功')

    emits('success')
    visible.value = false
  } catch (e) {
    ElMessage.error('支付失败')
    emits('close') 

  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getUserBalance()
})
</script>

<style scoped lang="less">
.payment-dialog {
  .info-box {
    display: flex;
    flex-direction: column;
    gap: 12px;
    margin-bottom: 20px;

    .row {
      display: flex;
      justify-content: space-between;
      font-size: 16px;

      .label {
        font-weight: 600;
        color: #555;
      }

      .value {
        font-weight: 600;

        &.price {
          color: #f56c6c;
        }

        &.balance {
          color: #67c23a;
        }
      }
    }
  }

  .dialog-footer {
    text-align: right;

    .el-button {
      min-width: 90px;
      margin-left: 10px;
    }
  }
}
</style>