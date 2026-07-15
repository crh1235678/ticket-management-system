<template>
  <div class="order-manage-page">

    <!-- 🔍 筛选栏 -->
    <FilterBox class="filter-card" :model="searchForm" :configs="filterConfigs" @search="handleSearch"
      @reset="resetSearch" />

    <!-- 📦 主卡片 -->
    <el-card class="main-card">

      <template #header>
        <div class="card-header">
          💳 已支付订单
          <span class="sub-title">（支持退票 / 改签）</span>
        </div>
      </template>

      <el-table :data="orderList" stripe class="order-table" align="center">

        <!-- 交通号 -->
        <el-table-column prop="number" label="交通号" width="140" />

        <!-- 类型 -->
        <el-table-column label="类型" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getTagType(scope.row.transportType)">
              {{ formatType(scope.row.transportType) }}
            </el-tag>
          </template>
        </el-table-column>


        <!-- 路线 -->
        <el-table-column label="路线" align="center">
          <template #default="scope">
            <div class="route">
              <span>{{ scope.row.origin }}</span>
              <span class="arrow">→</span>
              <span>{{ scope.row.destination }}</span>
            </div>
          </template>
        </el-table-column>

        <!-- 时间 -->
        <el-table-column label="时间" width="220" align="center">
          <template #default="scope">
            <div class="time-box">
              <div>出发：{{ formatTime(scope.row.departureTime) }}</div>
              <div class="arrive">到达：{{ formatTime(scope.row.arrivalTime) }}</div>
            </div>
          </template>
        </el-table-column>

        <!-- 张数 -->
        <el-table-column prop="seatCount" label="张数" width="80" align="center" />

        <!-- 金额 -->
        <el-table-column label="金额" width="120" align="center">
          <template #default="scope">
            <span class="price">￥{{ scope.row.totalPrice }}</span>
          </template>
        </el-table-column>

        <!-- 操作 -->
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <el-button type="danger" size="small" @click="refund(scope.row)">
              退票
            </el-button>

            <el-button type="warning" size="small" @click="changeTicket(scope.row)">
              改签
            </el-button>
          </template>
        </el-table-column>

      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="orderList.length === 0" description="暂无已支付订单" />

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :total="total"
          layout="total, prev, pager, next" @current-change="getOrders" />
      </div>

    </el-card>
    <ChangeTicketDialog ref="changeDialogRef" @refresh="getOrders"/>


  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { orderApi } from '@/api/order-api'
import { ElMessage } from 'element-plus'
import type { OrderQuery, OrderUserVO } from '@/types/order'
import FilterBox from '@/components/FilterBox.vue'
import type { FilterConfig } from '@/types/filter'
import ChangeTicketDialog from '@/components/ChangeTicketDialog.vue'

const changeDialogRef = ref()
// 查询条件
const searchForm = ref<OrderQuery>({
  number: undefined,
  origin: undefined,
  destination: undefined,
  departureTime: undefined,
  arrivalTime: undefined,
  transportType: undefined,
})

// 数据
const orderList = ref<OrderUserVO[]>([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
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

// 获取已支付订单
const getOrders = async () => {

  const res = await orderApi.getList(pageNum.value, pageSize.value, {
    ...searchForm.value,
    // 这里只是前端控制输入，后端必须添加检验功能，防止用户恶意修改
    status: 'PAID'
  })
  orderList.value = res.data.records
  total.value = res.data.total
}

// 查询
const handleSearch = () => {
  pageNum.value = 1
  getOrders()
}

// 重置
const resetSearch = () => {
  searchForm.value = {
    number: undefined,
    origin: undefined,
    destination: undefined,
    departureTime: undefined,
    arrivalTime: undefined,
    transportType: undefined,
  }
  getOrders()
}

// 时间格式
const formatTime = (time: string) =>
  time?.replace('T', ' ').slice(0, 16)

// 类型格式化
const formatType = (type: string) => ({
  FLIGHT: '飞机',
  TRAIN: '火车',
  BUS: '巴士'
}[type] || type)

// tag颜色
const getTagType = (type: string) => ({
  FLIGHT: 'danger',
  TRAIN: 'success',
  BUS: 'warning'
}[type] || '')

// 退票
async function refund(row: OrderUserVO) {
  //console.log(row.id)
  await orderApi.refund(row.id)
  ElMessage.success(`退票成功：${row.number}`)
  getOrders()
}

// 改签
const changeTicket = (row: any) => {
  changeDialogRef.value.open(row)
}

function onPayFinish() {
  getOrders()
}

onMounted(getOrders)
</script>

<style scoped lang="less">
.order-manage-page {
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

    .sub-title {
      font-size: 13px;
      color: #999;
      margin-left: 10px;
    }
  }

  .route {
    font-weight: 500;
    text-align: center;

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