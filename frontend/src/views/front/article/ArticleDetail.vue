<template>
  <div class="article-detail">

    <!-- 主体卡片 -->
    <div class="detail-container">

      <!-- 标题 -->
      <h1 class="title">{{ detail?.title }}</h1>

      <!-- 信息栏 -->
      <div class="meta">
        <span class="tag">{{ typeLabel }}</span>
        <span>👁 {{ detail?.viewCount || 0 }}</span>
        <span>👍 {{ detail?.likeCount || 0 }}</span>
      </div>


      <!-- 正文 -->
      <div class="content" v-html="detail?.content"></div>

      <!-- 点赞区 -->
      <div class="action">
        <el-button type="primary" @click="handleLike">👍 点赞</el-button>
      </div>

    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { articleApi } from '@/api/article-api'
import type { ArticleDetailVO } from '@/types/article'
import { ElMessage } from 'element-plus'

const route = useRoute()
const detail = ref<ArticleDetailVO>()

const typeMap: Record<string, string> = {
  OVERSEAS: '国外游',
  DOMESTIC: '国内游',
  CITY: '城市',
  COUNTRYSIDE: '乡村'
}

const typeLabel = computed(() => {
  const type = detail.value?.articleType || ''
  return typeMap[type] || '未知'
})

const getDetail = async () => {
  const id = Number(route.params.id)
  try {
    const res = await articleApi.getDetail(id)
    detail.value = res as unknown as ArticleDetailVO
  } catch (err) {
    console.error('获取文章详情失败', err)
  }
}

const handleLike = async () => {
  if (!detail.value?.id) return
  try {
    console.log('查询文章详情：', detail.value.id)
    await articleApi.like(detail.value.id)
    detail.value.likeCount = (detail.value.likeCount || 0) + 1
    // 成功提示
    ElMessage.success('点赞成功 👍')
    // 滚动到顶部
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    })
  } catch (err) {
    console.error('点赞失败', err)
  }
}

onMounted(() => {
  getDetail()
})
</script>

<style lang="less">
.article-detail {
  display: flex;
  justify-content: center;
  padding: 60px 15px;
  background: #f2f3f5;
}

/* 主体容器（核心🔥） */
.detail-container {
  width: 760px;
  max-width: 95%;
  background: #fff;
  border-radius: 18px;
  padding: 40px 50px;
  box-shadow: 0 8px 30px rgba(0,0,0,0.06);

  /* ⭐ 防塌陷 + 自适应高度 */
  min-height: calc(100vh - 200px);

  /* ⭐ 关键布局（让点赞区在底部） */
  display: flex;
  flex-direction: column;
}

/* 标题 */
.title {
  font-size: 32px;
  font-weight: 700;
  line-height: 1.4;
  margin-bottom: 20px;
  color: #1a1a1a;
  text-align: center;
}

/* 信息栏 */
.meta {
  display: flex;
  justify-content: center;
  gap: 20px;
  font-size: 13px;
  color: #999;
  margin-bottom: 30px;
}

/* 标签 */
.tag {
  background: #e8f3ff;
  color: #409eff;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
}

/* 分割线 */
.meta::after {
  content: '';
  display: block;
  width: 60px;
  height: 2px;
  background: #409eff;
  margin: 15px auto 0;
  border-radius: 2px;
}

/* 正文（核心阅读体验🔥） */
.content {
  flex: 1; /* ⭐ 撑开内容，让底部对齐 */
  line-height: 2;
  font-size: 17px;
  color: #333;
  letter-spacing: 0.5px;
  white-space: pre-line;
}

/* 段落 */
.content p {
  margin: 18px 0;
  text-indent: 2em;
}

/* 图片 */
.content img {
  display: block;
  max-width: 100%;
  margin: 25px auto;
  border-radius: 10px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
}

/* 标题层级 */
.content h1,
.content h2,
.content h3 {
  margin: 25px 0 10px;
  font-weight: 600;
  color: #222;
}

.content h2 {
  font-size: 22px;
}

.content h3 {
  font-size: 18px;
}

/* 点赞区 */
.action {
  margin-top: 50px;
  padding-top: 25px;
  border-top: 1px solid #eee;
  text-align: center;
}

/* 按钮优化 */
:deep(.el-button) {
  font-size: 16px;
  padding: 10px 26px;
  border-radius: 25px;
  transition: all 0.2s;
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
}

/* 响应式 */
@media (max-width: 768px) {
  .detail-container {
    padding: 25px 20px;
  }

  .title {
    font-size: 26px;
  }

  .content {
    font-size: 15px;
  }
}
</style>