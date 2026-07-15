<template>
  <div class="article-page">

    <!-- 左侧分类 -->
    <div class="article-left">
      <div class="category-title">📚 文章分类</div>

      <div
        class="category-item"
        v-for="item in categories"
        :key="item"
        :class="{ active: currentCategory === item }"
        @click="selectCategory(item)"
      >
        {{ item }}
      </div>
    </div>

    <!-- 右侧 -->
    <div class="article-right">

      <!-- 搜索区（升级🔥） -->
      <div class="article-top">
        <div class="search-box">
          <el-input
            v-model="keyword"
            placeholder="搜索旅游攻略（输入标题）"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-button type="primary" @click="handleSearch">
            🔍 搜索
          </el-button>
        </div>
      </div>

      <!-- 文章列表 -->
      <div class="article-list">
        <div
          class="article-card"
          v-for="item in list"
          :key="item.id"
          @click="goDetail(item)"
        >
          <img :src="getFileUrl(item.coverImg)" class="article-img" />

          <div class="article-content">
            <div>
              <div class="article-title">{{ item.title }}</div>
              <div class="article-desc">{{ item.description }}</div>
            </div>

            <div class="article-info">
              <span>👁 {{ item.viewCount }}</span>
              <span>👍 {{ item.likeCount }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
        />
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { articleApi }  from '@/api/article-api'
import type { ArticleCatalogVO } from '@/types/article'
import { getFileUrl } from '@/utils/utils'

type CategoryKey = '全部' | '国内游' | '国外游' | '城市' | '乡村'
const router = useRouter()
const pageNum = ref(1)
const pageSize = ref(10)
const list = ref<ArticleCatalogVO[]>([])
const total = ref(0)
const keyword = ref('')
const currentCategory = ref('全部')

const categories: CategoryKey[] = [
  '全部',
  '国内游',
  '国外游',
  '城市',
  '乡村'
]
// 前后端映射
const categoryMap: Record<string, any> = {
  '全部': null,
  '国内游': 'DOMESTIC',
  '国外游': 'OVERSEAS',
  '城市': 'CITY',
  '乡村': 'COUNTRYSIDE'
}
const handleSearch = () => {
  pageNum.value = 1 
  getList()
}

const goDetail = (item: any) => {
  router.push(`/user/article/${item.id}`)
}

const selectCategory = (item: string) => {
  currentCategory.value = item
  getList()
}

const getList = async () => {
    const res = await articleApi.getlist(pageNum.value, pageSize.value,{
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      title: keyword.value,
      articleType: categoryMap[currentCategory.value]
    }
  )
  console.log('查询结果：', res)
  list.value = res.data.records
  total.value = res.data.total
  console.log('查询：', keyword.value, currentCategory.value)
}

onMounted(() => {
  getList()
})
</script>

<style lang="less">
.article-page {
  display: flex;
  gap: 24px;
  padding: 30px;
  
  min-height: 100vh;
}

/* 左侧分类 */
.article-left {
  width: 220px;
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 6px 20px rgba(0,0,0,0.06);
  background: #f5f7fa;
}

.category-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 15px;
}

/* 分类项 */
.category-item {
  padding: 10px 14px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.25s;
  position: relative;
  
}

.category-item:hover {
  background: #f0f7ff;
}

.category-item.active {
  background: #409eff;
  color: #fff;
}

/* 右侧 */
.article-right {
  flex: 1;
}

/* 搜索区 */
.article-top {
  margin-bottom: 20px;
}

.search-box {
  display: flex;
  gap: 10px;
}

:deep(.el-input) {
  flex: 1;
}

:deep(.el-button) {
  border-radius: 8px;
  padding: 0 20px;
}

/* 列表 */
.article-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

/* 卡片 */
.article-card {
  display: flex;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 16px rgba(0,0,0,0.06);
}

.article-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 30px rgba(0,0,0,0.12);
}

/* 图片 */
.article-img {
  width: 240px;
  height: 150px;
  object-fit: cover;
  transition: 0.3s;
}

.article-card:hover .article-img {
  transform: scale(1.05);
}

/* 内容 */
.article-content {
  flex: 1;
  padding: 16px 18px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

/* 标题 */
.article-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 6px;
  color: #222;
}

/* 描述 */
.article-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

/* 信息 */
.article-info {
  font-size: 13px;
  color: #999;
  display: flex;
  gap: 18px;
}

/* 分页 */
.pagination {
  margin-top: 30px;
  text-align: center;
}
</style>