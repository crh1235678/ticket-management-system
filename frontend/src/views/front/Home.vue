<template>
  <div class="front-body">

    <div class="front-body-hero">

      <!-- 左侧轮播 -->
      <div class="hero-left">
        <Carousel :images="homeImages" height="487.5px" />
      </div>

      <!-- 右侧 -->
      <div class="hero-right">

        <!-- 标题 + 更多 -->
        <el-row class="hero-header">
          <el-col :span="12" class="hero-title">
            热门目的地推荐
          </el-col>
          <el-col :span="12" class="hero-more">
            <span @click="goMore">查看更多 ></span>
          </el-col>
        </el-row>

        <!-- 六宫格 -->
        <el-row :gutter="16" class="view-grid">
          <el-col
            v-for="item in viewList"
            :key="item.id"
            :span="8"
            class="view-col"
          >
            <div class="view-card" @click="goArticle(item)">
              <img :src="item.img" class="view-img" />
              <div class="view-title overflowShow">
                {{ item.title }}
              </div>
            </div>
          </el-col>
        </el-row>

      </div>

    </div>

    <!-- 下方交通模块 -->
    <div class="front-body-transport">

      <div class="transport-title">出行方式</div>

      <div class="transport-wrapper">

        <div class="transport-item" @click="$router.push('/user/order/flightorder')">
          <img src="@/assets/images/front/plane.png">
          <div>飞机</div>
        </div>

        <div class="transport-item" @click="$router.push('/user/order/busorder')">
          <img src="@/assets/images/front/bus.png">
          <div>汽车</div>
        </div>

        <div class="transport-item" @click="$router.push('/user/order/trainorder')">
          <img src="@/assets/images/front/train.png">
          <div>火车</div>
        </div>

      </div>

    </div>

  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import Carousel from '@/components/Carousel.vue'

import carousel1 from '@/assets/images/front/carousel-1.png'
import carousel2 from '@/assets/images/front/carousel-2.png'
import carousel3 from '@/assets/images/front/carousel-3.png'
import carousel4 from '@/assets/images/front/carousel-4.png'

import zhejiangView from '@/assets/images/front/zhejiang_view.png'
import yunanView from '@/assets/images/front/yunnan_view.png'
import jiangxiView from '@/assets/images/front/jiangxi_view.png'
import japanView from '@/assets/images/front/japan_view.png'
import americanView from '@/assets/images/front/american_view.png'
import parisView from '@/assets/images/front/bali_view.png'

const router = useRouter()

const homeImages = [
  { name: 'home-1', url: carousel1 },
  { name: 'home-2', url: carousel2 },
  { name: 'home-3', url: carousel3 },
  { name: 'home-4', url: carousel4 }
]

const viewList = [
  { id: 3, title: '浙江 · 山水风光', img: zhejiangView },
  { id: 5, title: '云南 · 自然秘境', img: yunanView },
  { id: 6, title: '江西 · 文化古城', img: jiangxiView },
  { id: 7, title: '日本 · 东京之旅', img: japanView },
  { id: 8, title: '美国 · 城市探索', img: americanView },
  { id: 9, title: '法国 · 巴黎浪漫', img: parisView },
]

const goArticle = (item: any) => {
  router.push(`/user/article/${item.id}`)
}

const goMore = () => {
  router.push('/user/article')
}
</script>

<style lang="less">

.front-body {
  padding: 20px;
}

/* hero */
.front-body-hero {
  display: flex;
  gap: 20px;
  align-items: stretch;
}

.hero-left {
  flex: 1;
}

.hero-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 487.5px;
}

/* 标题 */
.hero-header {
  margin-bottom: 10px;
}

.hero-title {
  font-size: 22px;
  font-weight: 600;
}

.hero-more {
  text-align: right;
  font-size: 14px;
  color: #409EFF;
  cursor: pointer;
}

.hero-more:hover {
  text-decoration: underline;
}

/* 六宫格（关键优化） */
.view-grid {
  flex: 1;
}

/* 每个格子固定高度 */
.view-col {
  height: 218px;  
  margin-bottom: 10px;
}

/* 卡片 */
.view-card {
  height: 100%;
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  display: flex;
  flex-direction: column;
}

.view-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 18px rgba(0,0,0,0.15);
  
}

/* 图片（变大更饱满） */
.view-img {
  width: 100%;
  height: 150px;  /* ⭐变大填充空间 */
  object-fit: cover;
}

/* 标题 */
.view-title {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}

/* ===== 交通模块 ===== */

.front-body-transport {
  margin-top: 30px;
  padding: 20px;
  background: #f8f8f8;
  border-radius: 10px;
}

.transport-title {
  font-size: 20px;
  margin-bottom: 20px;
  font-weight: 600;
  text-align: center;
}

.transport-wrapper {
  display: flex;
  gap: 20px;
}

.transport-item {
  flex: 1;
  text-align: center;
  cursor: pointer;
  transition: 0.3s;
  padding: 20px 0;
  background: #fff;
  border-radius: 10px;
}

.transport-item img {
  width: 60px;
  margin-bottom: 10px;
}

.transport-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

/* 省略 */
.overflowShow {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

</style>