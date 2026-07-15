<template>
  <div class="list-container">
    <!-- 查询表单 -->
    <el-form :inline="true">
      <el-form-item label="车次号">
        <el-input v-model="queryForm.trainNumber" placeholder="请输入车次号" />
      </el-form-item>

      <el-form-item label="出发地">
        <el-input v-model="queryForm.origin" placeholder="请输入出发地" />
      </el-form-item>

      <el-form-item label="目的地">
        <el-input v-model="queryForm.destination" placeholder="请输入目的地" />
      </el-form-item>

      <el-form-item>
        <el-button type="success" plain @click="onSearch">查询</el-button>
        <el-button type="primary" plain @click="add">新增</el-button>
        <el-button type="danger" plain @click="confirmDel()">批量删除</el-button>
      </el-form-item>
    </el-form>

    <el-divider />

    <!-- 表格 -->
    <el-table border v-loading="listLoading" :data="datalist" style="width: 100%"
      :header-cell-style="{ background: '#f5f5f5' }" @selection-change="handleSelectionChange">

      <el-table-column type="selection" width="55" />
      <el-table-column prop="trainNumber" label="车次号" align="center" />
      <el-table-column prop="trainName" label="列车名称" align="center" />

      <el-table-column prop="origin" label="出发地" align="center" />
      <el-table-column prop="destination" label="目的地" align="center" />

      <el-table-column prop="departureTime" label="发车时间" align="center">
        <template #default="scope">
          {{ formatDateTime(scope.row.departureTime) }}
        </template>
      </el-table-column>

      <el-table-column prop="arrivalTime" label="到站时间" align="center">
        <template #default="scope">
          {{ formatDateTime(scope.row.arrivalTime) }}
        </template>
      </el-table-column>

      <el-table-column prop="price" label="票价" align="center" />
      <el-table-column prop="seatTotal" label="座位总量" align="center" />
      <el-table-column prop="seatRemaining" label="剩余票数" align="center" />

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
          <el-button type="text" @click="update(scope.row)">修改</el-button>
          <el-button type="text" @click="confirmDel(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination background layout="prev, pager, next"
      :total="total"
      style="float: right; margin: 10px 20px 20px 0"
      :page-size="queryForm.pageSize"
      @current-change="handleCurrentChange" />

    <!-- 弹窗 -->
    <addOrUpdate
      ref="operateRef"
      title="列车管理"
      :fields="fields"
      :rules="rules"
      :submitApi="submitApi"
      @refresh="getList" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import addOrUpdate from '@/components/AddorUpdate.vue'
import { trainApi } from '@/api/train-api'
import Constants from '@/utils/constants'
import type { Field } from '@/types/form'

// 查询参数
const queryForm = reactive({
  trainNumber: '',
  origin: '',
  destination: '',
  pageNum: Constants.PAGE_NUM,
  pageSize: Constants.PAGE_SIZE
})

// 表格
const datalist = ref([])
const listLoading = ref(false)
const total = ref(0)
const multipleSelection = ref([])

const operateRef = ref()

// 表单字段
const fields: Field[] = [
  { label: '车次号', prop: 'trainNumber', type: 'input' },
  { label: '列车名称', prop: 'trainName', type: 'input' },
  { label: '出发地', prop: 'origin', type: 'input' },
  { label: '目的地', prop: 'destination', type: 'input' },
  { label: '发车时间', prop: 'departureTime', type: 'datetime' },
  { label: '到站时间', prop: 'arrivalTime', type: 'datetime' },
  { label: '票价', prop: 'price', type: 'input' },
  { label: '座位总量', prop: 'seatTotal', type: 'input' },
  { label: '剩余座位数', prop: 'seatRemaining', type: 'input' }
]

// 校验
const rules = {
  trainNumber: [{ required: true, message: '请输入车次号', trigger: 'blur' }],
  trainName: [{ required: true, message: '请输入列车名称', trigger: 'blur' }],
  origin: [{ required: true, message: '请输入出发地', trigger: 'blur' }],
  destination: [{ required: true, message: '请输入目的地', trigger: 'blur' }],
  departureTime: [{ required: true, message: '请选择发车时间', trigger: 'blur' }],
  arrivalTime: [{ required: true, message: '请选择到站时间', trigger: 'blur' }],
  price: [{ required: true, message: '请输入票价', trigger: 'blur' }],
  ticketTotal: [{ required: true, message: '请输入总票数', trigger: 'blur' }],
  ticketRemaining: [{ required: true, message: '请输入剩余票数', trigger: 'blur' }]
}

// 提交
const submitApi = (form: any) => {
  return form.id ? trainApi.update(form) : trainApi.add(form)
}

// 时间格式化
function formatDateTime(value: any) {
  if (!value) return ''
  const date = new Date(value)
  return `${date.getFullYear()}-${(date.getMonth()+1).toString().padStart(2,'0')}-${date.getDate().toString().padStart(2,'0')} 
  ${date.getHours().toString().padStart(2,'0')}:${date.getMinutes().toString().padStart(2,'0')}`
}

// 查询
async function getList() {
  listLoading.value = true
  try {
    const res = await trainApi.getlist(queryForm.pageNum, queryForm.pageSize, queryForm)
    datalist.value = res.data.records
    total.value = res.data.total
  } finally {
    listLoading.value = false
  }
}

function onSearch() {
  queryForm.pageNum = 1
  getList()
}

function handleCurrentChange(val: number) {
  queryForm.pageNum = val
  getList()
}

function add() {
  operateRef.value.show()
}

function update(row: any) {
  operateRef.value.show(row)
}

function confirmDel(id?: number) {
  ElMessageBox.confirm('是否确认删除？', '提示')
    .then(() => del(id))
}

async function del(id?: number) {
  const ids = id ? [id] : multipleSelection.value.map((i: any) => i.id)
  if (!ids.length) return ElMessage.warning('请选择要删除的列车')

  await trainApi.delete(ids)
  ElMessage.success('删除成功')
  getList()
}

function handleSelectionChange(val: any) {
  multipleSelection.value = val
}

onMounted(getList)
</script>

<style scoped>
.list-container {
  padding: 20px;
}
</style>