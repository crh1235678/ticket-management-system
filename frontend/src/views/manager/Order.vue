<template>
  <div class="list-container">
    <!-- 查询 -->
    <el-form :inline="true">
      <el-form-item label="用户ID">
        <el-input v-model="queryForm.userId" placeholder="请输入用户ID" />
      </el-form-item>

      <el-form-item label="票类型">
        <el-select v-model="queryForm.transportType" placeholder="请选择">
          <el-option label="航班" value="FLIGHT" />
          <el-option label="火车" value="TRAIN" />
          <el-option label="汽车" value="BUS" />
        </el-select>
      </el-form-item>

      <el-form-item label="订单状态">
        <el-select v-model="queryForm.status" placeholder="请选择">
          <el-option label="未支付" value="UNPAID" />
          <el-option label="已支付" value="PAID" />
          <el-option label="已取消" value="CANCELLED" />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="success" plain @click="onSearch">查询</el-button>
        <el-button type="danger" plain @click="confirmDel()">批量删除</el-button>
      </el-form-item>
    </el-form>

    <el-divider />

    <!-- 表格 -->
    <el-table border v-loading="listLoading" :data="datalist" style="width: 100%"
      @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />

      <el-table-column prop="id" label="订单ID" align="center" />

      <el-table-column label="用户" align="center">
        <template #default="scope">
          {{ scope.row.username }}（ID: {{ scope.row.userId }}）
        </template>
      </el-table-column>

      <el-table-column prop="transportType" label="交通类型" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.transportType === 'FLIGHT'">航班</el-tag>
          <el-tag type="success" v-if="scope.row.transportType === 'TRAIN'">火车</el-tag>
          <el-tag type="warning" v-if="scope.row.transportType === 'BUS'">汽车</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="ticketId" label="票ID" align="center" />

      <el-table-column prop="totalPrice" label="总价" align="center" />

      <el-table-column prop="seatCount" label="数量" align="center" />

      <el-table-column prop="status" label="状态" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 'UNPAID'">未支付</el-tag>
          <el-tag type="success" v-if="scope.row.status === 'PAID'">已支付</el-tag>
          <el-tag type="danger" v-if="scope.row.status === 'CANCELLED'">已取消</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="createTime" label="创建时间" align="center">
        <template #default="scope">
          {{ formatDateTime(scope.row.createTime) }}
        </template>
      </el-table-column>

      <el-table-column prop="updateTime" label="更新时间" align="center">
        <template #default="scope">
          {{ formatDateTime(scope.row.updateTime) }}
        </template>
      </el-table-column>

      <el-table-column label="操作" width="150" align="center">
        <template #default="scope">
          <el-button type="text" @click="modify(scope.row)">修改</el-button>
          <el-button type="text" @click="confirmDel(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination background layout="prev, pager, next" :total="total" :page-size="queryForm.pageSize"
      style="float: right; margin: 10px" @current-change="handleCurrentChange" />

    <addOrUpdate 
    ref="operateRef" 
    title="订单修改" 
    :fields="fields" 
    :rules="rules" 
    :submitApi="submitApi"
    @refresh="getList">
    </addOrUpdate>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '@/api/order-api'
import Constants from '@/utils/constants'
import type { Field } from '@/types/form'
import addOrUpdate from '@/components/AddorUpdate.vue'

// 查询参数
const queryForm = reactive({
  userId: '',
  transportType: '',
  status: '',
  pageNum: 1,
  pageSize: Constants.PAGE_SIZE
})
// 定义修改或者添加订单的表单字段
const fields: Field[] = [
  // 这个票数应该要做验证不能大于总票数
  { label: '票数', prop: 'seatCount', type: 'input' },
  { label: '订单状态', prop: 'status', type: 'select', options: [
    { label: '未支付', value: 'UNPAID' },
    { label: '已支付', value: 'PAID' },
    { label: '已取消', value: 'CANCELLED' }
  ] },
]
const rules = {
  seatCount: [{ required: true, message: '请输入票数', trigger: 'blur' }],
  status: [{ required: true, message: '请选择订单状态', trigger: 'change' }],
}

const datalist = ref([])
const total = ref(0)
const listLoading = ref(false)
const multipleSelection = ref([])
const operateRef = ref()
// 定义提交订单的接口
const submitApi = (form: any) => {
  return orderApi.update(form)   // ⭐ 必须 return
}

// 时间格式化
function formatDateTime(value: string | Date) {
  if (!value) return ''
  const date = new Date(value)
  return date.toLocaleString()
}

// 获取数据
async function getList() {
  listLoading.value = true
  try {
    const res = await orderApi.getAllOrder(queryForm.pageNum, queryForm.pageSize, queryForm)
    datalist.value = res.data.records
    total.value = res.data.total
  } finally {
    listLoading.value = false
  }
}

// 查询
function onSearch() {
  queryForm.pageNum = 1
  getList()
}

// 分页
function handleCurrentChange(val: number) {
  queryForm.pageNum = val
  getList()
}

// 删除
function confirmDel(id?: number) {
    ElMessageBox.confirm(
    '是否确认删除？',
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      del(id);
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '用户取消删除',
      })
    })
}

async function del(id?: number) {
  listLoading.value = true
  try {
    const ids = id ? [id] : multipleSelection.value.map((item: any) => item.id)
    if (!ids.length) {
      ElMessage.warning('请选择要删除的航班')
      return
    }
    await orderApi.delete(ids)
    ElMessage.success('删除成功')
    getList()
  } finally {
    listLoading.value = false
  }
}

// 修改订单
function modify(row) {
  operateRef.value.show(row);
}

// 多选
function handleSelectionChange(val: any) {
  multipleSelection.value = val
}

onMounted(getList)
</script>