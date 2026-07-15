<template>
  <el-container>

    <!-- 头部 -->
    <el-header style="height:155px;background:#E8F5F2;padding:0">
      <FrontHeader :bgImage="bg"/>
    </el-header>

    <!-- 子页面 -->
    <el-main class="layout-main">
      <router-view />
    </el-main>

    <el-footer class="layout-footer">
      <Footer></Footer>
    </el-footer>

  </el-container>
</template>

<script setup lang="ts">
import FrontHeader from '@/components/layouts/FrontHeader.vue'
import Footer from '@/components/layouts/Footer.vue'
import { computed } from 'vue'
import { useRoute } from 'vue-router'
const route = useRoute()
// 使用路由来控制头部背景图片，是为了在不同的页面显示不同的背景图片的效果
const bg = computed(() => route.meta.headerBgImage || '')
</script>
