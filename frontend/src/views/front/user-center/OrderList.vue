<template>
  <div class="user-orders-page">

    <!-- 🔍 筛选栏 -->
    <FilterBox 
    class="filter-card" 
    :model="query" 
    :configs="filterConfigs" 
    @search="getOrders"
    @reset="resetQuery" />

    <!-- 📦 主卡片 -->
    <el-card class="main-card">
      <template #header>
        <div class="card-header">✈️ 待支付订单</div>
      </template>

      <el-table :data="orderList" stripe class="order-table">

        <el-table-column prop="number" label="交通号" width="150" />

        <el-table-column label="类型" width="100">
          <template #default="scope">
            <el-tag :type="getTagType(scope.row.transportType)">
              {{ formatType(scope.row.transportType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="路线">
          <template #default="scope">
            <div class="route">
              <span>{{ scope.row.origin }}</span>
              <span class="arrow">→</span>
              <span>{{ scope.row.destination }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="时间" width="200">
          <template #default="scope">
            <div class="time-box">
              <div>{{ formatTime(scope.row.departureTime) }}</div>
              <div class="arrive">{{ formatTime(scope.row.arrivalTime) }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="seatCount" label="张数" width="80" />

        <el-table-column label="金额" width="120">
          <template #default="scope">
            <span class="price">￥{{ scope.row.totalPrice }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="240">
          <template #default="scope">
            <el-button type="primary" size="small" @click="payOrder(scope.row)">
              支付
            </el-button>
          </template>
        </el-table-column>

      </el-table>

      <el-empty v-if="orderList.length === 0" description="暂无待支付订单" />

      <div class="pagination">
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :total="total"
          layout="total, prev, pager, next" @current-change="getOrders" />
      </div>

    </el-card>

    <!-- 💰 支付弹窗 -->
    <PaymentDialog ref="paymentRef" :amount="totalPrice" :orderId="orderId" @success="onPayFinish" @close="onPayFinish"
      @later="onPayFinish" />

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { orderApi } from '@/api/order-api'
import PaymentDialog from '@/components/PaymentDialog.vue'
import type { OrderQuery } from '@/types/order'
import FilterBox from '@/components/FilterBox.vue'
import type { FilterConfig } from '@/types/filter'

const orderList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const query = ref<OrderQuery>({
  number: undefined,
  origin: undefined,
  destination: undefined,
  transportType: undefined,
  departureTime: undefined,
  arrivalTime: undefined,
})

const paymentRef = ref()
const totalPrice = ref(0)
const orderId = ref(0)

// 定义筛选框格式
const filterConfigs = <FilterConfig[]>[
  {
    label: '交通号',
    prop: 'number',
    type: 'input',
    placeholder: '请输入交通号'
  },
  {
    label: '出发地',
    prop: 'origin',
    type: 'input'
  },
  {
    label: '目的地',
    prop: 'destination',
    type: 'input'
  },
  {
    label: '出发日期',
    prop: 'departureTime',
    type: 'date'
  },
  {
    label: '到达日期',
    prop: 'arrivalTime',
    type: 'date'
  },
  {
    label: '类型',
    prop: 'transportType',
    type: 'select',
    options: [
      { label: '飞机', value: 'FLIGHT' },
      { label: '火车', value: 'TRAIN' },
      { label: '巴士', value: 'BUS' }
    ]
  }
]

const getOrders = async () => {
  const res = await orderApi.getList(pageNum.value, pageSize.value, {
    ...query.value,
    // 这里只是前端控制输入，后端必须添加检验功能，防止用户恶意修改
    status: 'UNPAID'
  })
  orderList.value = res.data.records
  total.value = res.data.total
}

const resetQuery = () => {
  query.value = {
    number: undefined,
    origin: undefined,
    destination: undefined,
    transportType: undefined,
    departureTime: undefined,
    arrivalTime: undefined,
  }
  getOrders()
}

// 时间格式转换
const formatTime = (time: string) => time?.replace('T', ' ').slice(0, 16)

const formatType = (type: string) => ({
  FLIGHT: '飞机',
  TRAIN: '火车',
  BUS: '巴士'
}[type] || type)

const getTagType = (type: string) => ({
  FLIGHT: 'danger',
  TRAIN: 'success',
  BUS: 'warning'
}[type] || '')

const payOrder = (row: any) => {
  totalPrice.value = row.totalPrice
  orderId.value = row.id
  paymentRef.value.show()
}

const onPayFinish = () => getOrders()

onMounted(getOrders)
</script>

<style scoped lang="less">
.user-orders-page {
  padding: 20px;
  background: #f5f7fa;

  .filter-card {
    margin-bottom: 15px;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }

  .main-card {
    border-radius: 16px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  }

  .card-header {
    font-size: 20px;
    font-weight: bold;
  }

  .route {
    font-weight: 500;

    .arrow {
      margin: 0 8px;
      color: #999;
    }
  }

  .time-box {
    font-size: 13px;

    .arrive {
      color: #999;
      margin-top: 4px;
    }
  }

  .price {
    color: #f56c6c;
    font-size: 16px;
    font-weight: bold;
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>