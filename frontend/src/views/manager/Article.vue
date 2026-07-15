<template>
  <div class="list-container">
    <!-- 查询表单 -->
    <el-form :inline="true" class="query-form">
      <el-form-item label="文章类型">
        <el-select v-model="queryForm.articleType" placeholder="请选择文章类型" clearable>
          <el-option label="国外游" value="OVERSEAS" />
          <el-option label="国内游" value="DOMESTIC" />
          <el-option label="城市" value="CITY" />
          <el-option label="乡村" value="COUNTRYSIDE" />
        </el-select>
      </el-form-item>

      <el-form-item label="标题">
        <el-input v-model="queryForm.title" placeholder="请输入文章标题" clearable />
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
      @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />

      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="coverImg" label="封面" width="120" align="center">
        <template #default="scope">
          <img :src="getFileUrl(scope.row.coverImg)" class="cover-img" />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />

      <el-table-column prop="articleType" label="类型" width="120" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.articleType === 'OVERSEAS'" type="primary">国外游</el-tag>
          <el-tag v-else-if="scope.row.articleType === 'DOMESTIC'" type="success">国内游</el-tag>
          <el-tag v-else-if="scope.row.articleType === 'CITY'" type="info">城市</el-tag>
          <el-tag v-else type="warning">乡村</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />

      <el-table-column label="内容摘要" min-width="200">
        <template #default="scope">
          <span class="content-preview">
            {{ formatContent(scope.row.content) }}
          </span>
        </template>
      </el-table-column>

      <el-table-column prop="viewCount" label="浏览" width="90" align="center" />
      <el-table-column prop="likeCount" label="点赞" width="90" align="center" />

      <el-table-column label="创建时间" width="170" align="center">
        <template #default="scope">{{ formatDateTime(scope.row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="更新时间" width="170" align="center">
        <template #default="scope">{{ formatDateTime(scope.row.updateTime) }}</template>
      </el-table-column>

      <el-table-column label="操作" width="220" align="center">
        <template #default="scope">
          <el-button type="text" @click="viewDetail(scope.row)">文章详情</el-button>
          <el-button type="text" @click="modify(scope.row)">修改</el-button>
          <el-button type="text" @click="confirmDel(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination background layout="prev, pager, next" :total="total" :page-size="queryForm.pageSize"
      style="float:right;margin-top:10px" @current-change="handleCurrentChange" />

    <!-- 添加/修改 -->
    <addOrUpdate ref="operateRef" title="文章管理" :fields="fields" :rules="rules" :submitApi="submitApi"
      @refresh="getList" />

    <!-- ✅ 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="文章详情" width="60%">
      <div class="detail-content">
        {{ currentArticle.content }}
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import addOrUpdate from '@/components/AddorUpdate.vue'
import { articleApi } from '@/api/article-api'
import Constants from '@/utils/constants'
import type { Field } from '@/types/form'
import { getFileUrl } from '@/utils/utils'

const queryForm = reactive({
  articleType: '',
  title: '',
  pageNum: 1,
  pageSize: Constants.PAGE_SIZE,
})

const datalist = ref([])
const total = ref(0)
const listLoading = ref(false)
const multipleSelection = ref([])
const operateRef = ref()

// ✅ 详情相关
const detailVisible = ref(false)
const currentArticle = ref<any>({})

// 表单字段
const fields: Field[] = [
  { label: '标题', prop: 'title', type: 'input' },
  { label: '描述', prop: 'description', type: 'input' },
  {
    label: '文章类型',
    prop: 'articleType',
    type: 'select',
    options: [
      { label: '国外游', value: 'OVERSEAS' },
      { label: '国内游', value: 'DOMESTIC' },
      { label: '城市', value: 'CITY' },
      { label: '乡村', value: 'COUNTRYSIDE' },
    ],
  },
  { label: '封面图片', prop: 'coverImg', type: 'upload' },
  { label: '内容', prop: 'content', type: 'textarea' },
]

// 校验
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  articleType: [{ required: true, message: '请选择类型', trigger: 'change' }],
  coverImg: [{ required: true, message: '请上传封面', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
}

// 提交
const submitApi = (form: any) => {
  return form.id ? articleApi.update(form) : articleApi.add(form)
}

// ✅ 内容截断
function formatContent(content: string) {
  if (!content) return ''
  return content.length > 60 ? content.slice(0, 60) + '...' : content
}

// 时间格式
function formatDateTime(value: any) {
  return value ? new Date(value).toLocaleString() : ''
}

// 获取数据
async function getList() {
  listLoading.value = true
  try {
    const res = await articleApi.getlist(queryForm.pageNum, queryForm.pageSize, queryForm)
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
    await articleApi.delete(ids)
    ElMessage.success('删除成功')
    getList()
  } finally {
    listLoading.value = false
  }
}

// 新增
function add() {
  operateRef.value.show()
}

// 修改
function modify(row: any) {
  operateRef.value.show(row)
}

// ✅ 查看详情
function viewDetail(row: any) {
  currentArticle.value = row
  detailVisible.value = true
}

// 多选
function handleSelectionChange(val: any) {
  multipleSelection.value = val
}

onMounted(getList)
</script>

<style scoped>
.list-container {
  padding: 20px;
}

/* 封面图 */
.cover-img {
  width: 80px;
  height: 50px;
  border-radius: 4px;
  object-fit: cover;
}

/* 内容摘要 */
.content-preview {
  display: inline-block;
  max-width: 220px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 详情内容 */
.detail-content {
  max-height: 500px;
  overflow-y: auto;
  white-space: pre-wrap;
  line-height: 1.6;
}
</style>