<template>
  <div class="train-container">

    <!-- 查询区域 -->
    <el-form :inline="true" :model="searchForm" class="search-box">
      <el-form-item label="出发地">
        <el-input v-model="searchForm.origin" placeholder="如：北京" />
      </el-form-item>

      <el-form-item label="目的地">
        <el-input v-model="searchForm.destination" placeholder="如：上海" />
      </el-form-item>

      <el-form-item label="日期">
        <el-date-picker
          v-model="searchForm.date"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="选择出发日期" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="getList">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 列车表 -->
    <el-table :data="trainList" border width="100%">

      <!-- 列车信息 -->
      <el-table-column label="列车">
        <template #default="scope">
          <div class="train-info">
            <div>
              <div>{{ scope.row.trainName }}</div>
              <div class="sub">{{ scope.row.trainNumber }}</div>
            </div>
          </div>
        </template>
      </el-table-column>

      <!-- 路线 -->
      <el-table-column label="路线信息">
        <template #default="scope">
          <div class="route-box">
            <div class="city">
              <div class="name">{{ scope.row.origin }}</div>
              <div class="time">{{ formatTime(scope.row.departureTime) }}</div>
            </div>
            <div class="arrow">→</div>
            <div class="city">
              <div class="name">{{ scope.row.destination }}</div>
              <div class="time">{{ formatTime(scope.row.arrivalTime) }}</div>
            </div>
          </div>
        </template>
      </el-table-column>

      <!-- 价格 -->
      <el-table-column label="价格">
        <template #default="scope">
          <span class="price">￥{{ scope.row.price }}</span>
        </template>
      </el-table-column>

      <!-- 余票 -->
      <el-table-column label="余票">
        <template #default="scope">
          <span :class="scope.row.seatRemaining === 0 ? 'sold-out' : ''">
            {{ scope.row.seatRemaining === 0 ? '已售罄' : scope.row.seatRemaining + '张' }}
          </span>
        </template>
      </el-table-column>

      <!-- 操作 -->
      <el-table-column label="操作" width="100">
        <template #default="scope">
          <el-button type="success" @click="book(scope.row)">购票</el-button>
        </template>
      </el-table-column>

    </el-table>

    <!-- 弹窗 -->
    <PurchaseDialog
      :ticket="selectedTicket"
      ref="purchaseDialogRef"
      @confirm="handleConfirmPurchase" />

    <PaymentDialog
      ref="paymentRef"
      :amount="singlePrice * quantity"
      :orderId="orderId"
      @success="onPayFinish"
      @close="onPayFinish"
      @later="onPayFinish" />

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { trainApi } from '@/api/train-api'
import constants from '@/utils/constants'
import type { Ticket } from '@/types/ticket'
import type { TrainUserVO } from '@/types/train'
import PurchaseDialog from '@/components/OrderDialog.vue'
import PaymentDialog from '@/components/PaymentDialog.vue'

const trainList = ref<TrainUserVO[]>([])
const selectedTicket = ref<Ticket>()
const purchaseDialogRef = ref()
const paymentRef = ref<typeof PaymentDialog>()
const quantity = ref(0)
const singlePrice = ref(0)
const orderId = ref(0)

// 查询条件
const searchForm = ref({
  origin: '',
  destination: '',
  date: ''
})

// 重置
const reset = () => {
  searchForm.value = { origin: '', destination: '', date: '' }
  getList()
}

// 时间格式
const formatTime = (time: string) => time?.replace('T', ' ').slice(0, 16)

// 购票
const book = (row: TrainUserVO) => {
  selectedTicket.value = {
    ...row,
    transportType: 'TRAIN',
    name: row.trainName,
    number: row.trainNumber,
    transportId: row.id
  }
  purchaseDialogRef.value.showModel()
}

// 确认购票
const handleConfirmPurchase = (count: number, price: number, OrderId: number) => {
  quantity.value = count
  singlePrice.value = price
  orderId.value = OrderId
  paymentRef.value?.show(orderId.value)
}

// 支付完成
const onPayFinish = () => {
  getList()
}

// 查询
async function getList() {
  const params: any = {
    origin: searchForm.value.origin,
    destination: searchForm.value.destination
  }

  if (searchForm.value.date) {
    params.departureStart = searchForm.value.date + 'T00:00:00'
    params.departureEnd = searchForm.value.date + 'T23:59:59'
  }

  const res = await trainApi.getlist(constants.PAGE_NUM, constants.PAGE_SIZE, params)
  trainList.value = res.data.records
}

onMounted(getList)
</script>

<style scoped lang="less">
.train-container {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}


.route-box {
    display: flex;
    align-items: center;
    justify-content: center;
}

.city {
    text-align: center;
    width: 80px;
}

.arrow {
    margin: 0 15px;
    font-size: 20px;
    color: #409EFF;
}

.flight-container {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
}

.search-box {
    margin-bottom: 20px;
}

.flight-info {
    display: flex;
    align-items: center;
}

.sub {
    font-size: 12px;
    color: #999;
}

.time {
    font-size: 12px;
    color: #666;
}

.price {
    color: red;
    font-weight: bold;
}

.sold-out {
    color: #ccc;
}

:deep(.search-box) {
    font-size: 18px;
}

:deep(.search-box .el-form-item__label) {
    font-size: 18px;
}

:deep(.search-box .el-input__inner) {
    font-size: 18px;
}

:deep(.search-box .el-input__wrapper) {
    font-size: 18px;
}

:deep(.search-box .el-button) {
    font-size: 18px;
}

//===================== 表格整体 =====================================================================================


::v-deep(.el-table__empty-text) {
  display: none !important;
}

:deep(.el-table) {
    font-size: 20px;
}

:deep(.el-table th) {
    font-size: 20px;
    font-weight: 600;
    color: #333;
    background: #f5f7fa;
    text-align: center;
}

:deep(.el-table td) {
    padding: 16px 0;
}

/* 行高更舒服 */
:deep(.el-table__row) {
    height: 70px;
}

/* hover效果 */
:deep(.el-table__body tr:hover) {
    background-color: #f0f9ff;
}

/* ===== 航班信息 ===== */
.flight-info {
    display: flex;
    align-items: center;
}

.logo {
    width: 75px;
    height: 75px;
    margin-right: 15px;
    border-radius: 50%;
}

.sub {
    font-size: 13px;
    color: #999;
}

.route-box {
    display: flex;
    align-items: center;
    justify-content: center;
}

.city {
    text-align: center;
    width: 90px;
}

.name {
    font-size: 20px;
    color: #666;
}

/* 时间突出 */
.time {
    font-size: 16px;
    font-weight: bold;
    color: #000;
}

/* 箭头更高级 */
.arrow {
    margin: 0 20px;
    font-size: 22px;
    color: #409EFF;
}

/* ===== 价格（重点🔥） ===== */
.price {
    color: #ff4d4f;
    font-size: 20px;
    font-weight: bold;
}

/* ===== 余票 ===== */
.sold-out {
    color: #ccc;
    font-size: 14px;
}

/* ===== 按钮 ===== */
:deep(.el-button) {
    font-size: 14px;
    padding: 8px 16px;
    border-radius: 6px;
}

</style>





