<template>
    <el-dialog v-model="visible" width="720px" class="change-dialog">

        <!-- 🔷 标题 -->
        <template #header>
            <div class="dialog-title">
                🚄 改签车票
            </div>
        </template>

        <!-- 🔹 原订单卡片 -->
        <div class="ticket-card old">
            <div class="top">
                <span class="number">{{ form.number }}</span>
                <el-tag type="info">原订单</el-tag>
            </div>

            <div class="route">
                <span class="city">{{ form.origin }}</span>
                <span class="arrow">→</span>
                <span class="city">{{ form.destination }}</span>
            </div>

            <div class="time">
                {{ formatTime(form.departureTime) }} - {{ formatTime(form.arrivalTime) }}
            </div>

            <div class="bottom">
                <span>🎫 {{ form.seatCount }} 张</span>
                <span class="price">￥{{ form.totalPrice }}</span>
            </div>
        </div>

        <!-- 🔹 分割 -->
        <div class="divider">可改签车次</div>

        <!-- 🔹 新票列表（卡片式🔥） -->
        <div class="ticket-list">
            <div v-for="item in ticketList" :key="item.id" class="ticket-card selectable"
                :class="{ active: selectedTicket?.id === item.id }" @click="selectRow(item)">
                <div class="top">
                    <span class="number">{{ item.number }}</span>
                    <el-tag type="success">可选</el-tag>
                </div>

                <div class="route">
                    <span class="city">{{ item.origin }}</span>
                    <span class="arrow">→</span>
                    <span class="city">{{ item.destination }}</span>
                </div>

                <div class="time">
                    {{ formatTime(item.departureTime) }}
                </div>

                <div class="bottom">
                    <span>余票：{{ item.seatRemaining }}</span>

                    <span class="diff" :class="{
                        up: item.diffPrice > 0,
                        down: item.diffPrice < 0
                    }">
                        {{ item.diffPrice > 0 ? '补' : '退' }}
                        ￥{{ Math.abs(item.diffPrice) }}
                    </span>
                </div>
            </div>
        </div>

        <!-- 🔹 底部 -->
        <template #footer>
            <div class="footer">
                <div v-if="selectedTicket" class="selected-info">
                    已选：{{ selectedTicket.number }}
                </div>

                <div>
                    <el-button @click="visible = false">取消</el-button>
                    <el-button type="primary" @click="confirmChange">
                        确认改签
                    </el-button>
                </div>
            </div>
        </template>

    </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { orderApi } from '@/api/order-api'
import { ElMessage } from 'element-plus'
import type{ ChangeNewTicket } from '@/types/order'


const emit = defineEmits(['refresh'])

const visible = ref(false)

// 🔹 接收父组件数据
const form = ref<any>({})

// 🔹 可选票
const ticketList = ref<any[]>([])

// 🔹 当前选中
const selectedTicket = ref<any>(null)

// 时间格式转换
const formatDate = (time: string) => time?.slice(0, 10)

const pageNum = ref(1)
const pageSize = ref(10)

// 打开弹窗（暴露给父组件）
const open = async (row: any) => {
    form.value = row
    visible.value = true

    // ✅ 调后端查可改签票
    const res = await orderApi.getOptionalTickets(pageNum.value, pageSize.value, {
        id: row.id,
        transportType: row.transportType,
        origin: row.origin,
        destination: row.destination,
        departureTime: formatDate(row.departureTime),  // ✅ 改这里
        arrivalTime: formatDate(row.arrivalTime),
    })
    ticketList.value = res.data.records
}

// 选中
const selectRow = (row: any) => {
    selectedTicket.value = row
}

// 确认改签
const confirmChange = async () => {
    if (!selectedTicket.value) {
        return ElMessage.warning('请选择车次')
    }
    console.log(selectedTicket.value)

    await orderApi.changeConfirm(<ChangeNewTicket>{
        oldId: form.value.id,
        transportId: selectedTicket.value.transportId,
        transportType: selectedTicket.value.transportType,
        seatCount: selectedTicket.value.seatCount
    })

    emit('refresh')
    ElMessage.success('改签成功')
    visible.value = false
}

// 时间格式
const formatTime = (time: string) =>
    time?.replace('T', ' ').slice(0, 16)

// 暴露方法
defineExpose({
    open
})
</script>

<style scoped>
.change-dialog {
    border-radius: 16px;
}

/* 标题 */
.dialog-title {
    font-size: 18px;
    font-weight: bold;
}

/* 卡片通用 */
.ticket-card {
    border-radius: 12px;
    padding: 14px;
    margin-bottom: 12px;
    background: #fff;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
    transition: all 0.2s;
}

/* 原订单 */
.ticket-card.old {
    background: linear-gradient(135deg, #eef5ff, #f7fbff);
}

/* 可选卡片 */
.ticket-card.selectable {
    cursor: pointer;
}

.ticket-card.selectable:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

/* 选中状态 */
.ticket-card.active {
    border: 2px solid #409eff;
}

/* 顶部 */
.top {
    display: flex;
    justify-content: space-between;
    margin-bottom: 6px;
}

.number {
    font-weight: bold;
}

/* 路线 */
.route {
    font-size: 18px;
    font-weight: 600;
    text-align: center;
}

.city {
    margin: 0 6px;
}

.arrow {
    color: #999;
}

/* 时间 */
.time {
    text-align: center;
    font-size: 13px;
    color: #666;
    margin-top: 4px;
}

/* 底部 */
.bottom {
    display: flex;
    justify-content: space-between;
    margin-top: 8px;
    font-size: 13px;
}

/* 价格 */
.price {
    color: #f56c6c;
    font-weight: bold;
}

/* 差价 */
.diff.up {
    color: #f56c6c;
}

.diff.down {
    color: #67c23a;
}

/* 分割线 */
.divider {
    margin: 15px 0;
    font-weight: bold;
    color: #666;
}

/* 底部 */
.footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.selected-info {
    color: #409eff;
}
</style>