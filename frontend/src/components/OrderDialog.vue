<template>
    <el-dialog v-model="visible" :title="ticket?.number + ' - 购票'" width="450px" class="purchase-dialog">
        <div v-if="ticket" class="ticket-info">
            <div class="flight-row">
                <span class="label">航班号：</span>
                <span class="value">{{ ticket.number }}</span>
            </div>
            <div class="flight-row">
                <span class="label">出发：</span>
                <span class="value">{{ ticket.origin }} / {{ formatTime(ticket.departureTime) }}</span>
            </div>
            <div class="flight-row">
                <span class="label">到达：</span>
                <span class="value">{{ ticket.destination }} / {{ formatTime(ticket.arrivalTime) }}</span>
            </div>
            <div class="flight-row">
                <span class="label">价格：</span>
                <span class="value price">￥{{ ticket.price }}</span>
            </div>
            <div class="flight-row">
                <span class="label">剩余票数：</span>
                <span class="value remaining">{{ ticket.seatRemaining }}</span>
            </div>

            <!-- 购买数量 -->
            <div class="flight-row">
                <span class="label">购买数量：</span>
                <el-input-number v-model="form.count" :min="1" :max="ticket.seatRemaining" size="medium"
                    :controls-position="'right'" />
            </div>
        </div>

        <!-- footer -->
        <template #footer>
            <div class="dialog-footer">
                <el-button size="medium" @click="close">取消</el-button>
                <el-button type="primary" size="medium" :loading="btnLoading" @click="onSubmit">
                    确认购买
                </el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { Ticket } from '@/types/ticket'
import { orderApi } from '@/api/order-api'


defineExpose({ showModel })

const emits = defineEmits<{
    (e: 'confirm', count: number, price: number, orderId: number): void
}>()

const visible = ref(false)
const btnLoading = ref(false)

const props = defineProps<{
    ticket: Ticket | undefined
}>()

const form = reactive({
    count: 1
})

function showModel() {
    form.count = 1
    visible.value = true
}

function close() {
    form.count = 1
    visible.value = false
}

async function onSubmit() {
    if (!props.ticket) return
    try {
        btnLoading.value = true
        // 创建订单,但是并没有支付完成，所以我们需要给订单一个未支付的状态
        const response = await orderApi.create({
            transportId: props.ticket.transportId,
            transportType: props.ticket.transportType,
            seatCount: form.count
        })
        //console.log('创建订单响应:', response)

        // 这里直接拿 response.data 就是订单对象
        const orderData = response.data
        if (!orderData) {
            throw new Error('订单创建失败，没有返回数据')
        }

        //console.log("订单ID:", orderData.orderId)

        // 通知父组件
        emits('confirm', form.count, props.ticket.price, orderData.orderId)
        close()
    } finally {
        btnLoading.value = false
    }
}


const formatTime = (time: string) => time?.replace('T', ' ').slice(0, 16)
</script>

<style scoped lang="less">
.purchase-dialog {
    .el-dialog__body {
        padding: 20px 30px;
        font-family: "PingFang SC", "Helvetica", sans-serif;
        background-color: #fdfdfd;
    }

    .ticket-info {
        display: flex;
        flex-direction: column;
        gap: 12px;

        .flight-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 6px 0;

            .label {
                font-weight: 600;
                color: #555;
                width: 120px;
            }

            .value {
                font-weight: 500;
                color: #333;

                &.price {
                    color: #f56c6c;
                    font-weight: 700;
                    font-size: 16px;
                }

                &.remaining {
                    color: #67c23a;
                    font-weight: 600;
                }
            }
        }

        .el-input-number {
            width: 100px;
        }
    }

    .dialog-footer {
        text-align: right;
        padding: 10px 0;
        border-top: 1px solid #ebeef5;

        .el-button {
            min-width: 100px;
            margin-left: 10px;
        }
    }
}
</style>