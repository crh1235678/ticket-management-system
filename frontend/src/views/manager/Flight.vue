<template>
  <div class="list-container">
    <!-- 查询表单 -->
    <el-form :inline="true">
      <el-form-item label="航班号">
        <el-input v-model="queryForm.flightNumber" placeholder="请输入航班号" />
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

    <!-- 航班列表表格 -->
    <el-table border v-loading="listLoading" :data="datalist" style="width: 100%"
      :header-cell-style="{ background: '#f5f5f5' }" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="flightNumber" label="航班号" align="center" />
      <el-table-column prop="flightName" label="航班名称" align="center" />
      <el-table-column prop="logourl" label="Logo" align="center">
        <template #default="scope">
          <img :src="getFileUrl(scope.row.logourl)" alt="" style="width:50px;height:30px" />
        </template>
      </el-table-column>
      <el-table-column prop="origin" label="出发地" align="center" />
      <el-table-column prop="destination" label="目的地" align="center" />
      <el-table-column prop="departureTime" label="起飞时间" align="center">
        <!-- 为了操作数据格式化时间，所以这里需要<template #default="scope"> -->
        <template #default="scope">
          {{ formatDateTime(scope.row.departureTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="arrivalTime" label="到达时间" align="center">
        <template #default="scope">
          {{ formatDateTime(scope.row.arrivalTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="price" label="票价" align="center" />
      <el-table-column prop="seatTotal" label="座位总数" align="center" />
      <el-table-column prop="seatRemaining" label="剩余座位" align="center" />
      <el-table-column prop="createTime" label="创建时间" align="center">
        <template #default="scope">
          {{ formatDateTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" align="center" >
      <template #default="scope">
        {{ formatDateTime(scope.row.updateTime) }}
      </template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center" header-align="center">
        <template #default="scope">
          <el-button type="text" plain @click="update(scope.row)">修改</el-button>
          <el-button type="text" plain @click="confirmDel(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination background layout="prev, pager, next" :total="total" style="float: right; margin: 10px 20px 20px 0"
      :page-size="queryForm.pageSize" @current-change="handleCurrentChange" />

    <!-- 新增/修改弹窗 -->
    <addOrUpdate 
    ref="operateRef" 
    title="航班管理" 
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
import { flightApi } from '@/api/flight-api'
import Constants from '@/utils/constants'
import type { Field } from '@/types/form'
import { getFileUrl } from '@/utils/utils'

// 查询表单初始值
const queryFormState = {
  flightNumber: '',
  origin: '',
  destination: '',
  pageNum: Constants.PAGE_NUM,
  pageSize: Constants.PAGE_SIZE
}
const queryForm = reactive({ ...queryFormState })

// 表格数据
const datalist = ref([])
const listLoading = ref(false)
const total = ref(0)
const multipleSelection = ref([])

// 弹窗组件 ref
const operateRef = ref()

// 定义表单字段
const fields: Field[] = [
  { label: '航班号', prop: 'flightNumber', type: 'input' },
  { label: '航班名称', prop: 'flightName', type: 'input' },
  { label: '出发地', prop: 'origin', type: 'input' },
  { label: '目的地', prop: 'destination', type: 'input' },
  { label: '起飞时间', prop: 'departureTime', type: 'datetime' },
  { label: '到达时间', prop: 'arrivalTime', type: 'datetime' },
  { label: '票价', prop: 'price', type: 'input' },
  { label: '座位总数', prop: 'seatTotal', type: 'input' },
  { label: '剩余座位', prop: 'seatRemaining', type: 'input' },
  { label: 'Logo', prop: 'logourl', type: 'upload' },
]

// 表单验证规则
const rules = {
  flightNumber: [{ required: true, message: '请输入航班号', trigger: 'blur' }],
  flightName: [{ required: true, message: '请输入航班名称', trigger: 'blur' }],
  logourl: [{ required: true, message: '请上传Logo', trigger: 'change' }],
  origin: [{ required: true, message: '请输入出发地', trigger: 'blur' }],
  destination: [{ required: true, message: '请输入目的地', trigger: 'blur' }],
  departureTime: [{ required: true, message: '请选择起飞时间', trigger: 'blur' }],
  arrivalTime: [{ required: true, message: '请选择到达时间', trigger: 'blur' }],
  price: [{ required: true, message: '请输入票价', trigger: 'blur' }],
  seatTotal: [{ required: true, message: '请输入座位总数', trigger: 'blur' }],
  seatRemaining: [{ required: true, message: '请输入剩余座位', trigger: 'blur' }],
}
const submitApi = (form: any) => {
  if (form.id) {
    return flightApi.update(form)
  } else {
    return flightApi.add(form)
  }
}

// 格式化日期时间
function formatDateTime(value: string | Date) {
  if (!value) return ''
  const date = new Date(value)
  const y = date.getFullYear()
  const m = (date.getMonth() + 1).toString().padStart(2, '0')
  const d = date.getDate().toString().padStart(2, '0')
  const h = date.getHours().toString().padStart(2, '0')
  const min = date.getMinutes().toString().padStart(2, '0')
  return `${y}-${m}-${d} ${h}:${min}`
}

// 获取航班列表
async function getList() {
  try {
    listLoading.value = true
    const res = await flightApi.getlist(queryForm.pageNum, queryForm.pageSize, queryForm)
    datalist.value = res.data.records
    total.value = res.data.total
  } finally {
    listLoading.value = false
  }
}

// 查询按钮
function onSearch() {
  queryForm.pageNum = 1
  getList()
}

// 分页变化
function handleCurrentChange(val: number) {
  queryForm.pageNum = val
  getList()
}

// 新增航班
function add() {
  operateRef.value.show()
}

// 修改航班
function update(row: any) {
  operateRef.value.show(row)
}

// 批量航班
/* function confirmDel(id?: number) {
  ElMessageBox.confirm('是否确认删除？', '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => del(id))
    .catch(() => ElMessage({ type: 'info', message: '已取消' }))
} */
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
    await flightApi.delete(ids)
    ElMessage.success('删除成功')
    getList()
  } finally {
    listLoading.value = false
  }
}

// 选中行
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