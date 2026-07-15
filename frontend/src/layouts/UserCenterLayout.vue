<template>
  <div class="user-center-layout">

    <!-- 顶部菜单 -->
    <div class="user-center-menu">
      <Menu :menus="menus" />
    </div>

    <div class="user-center-main">
        <!-- 子页面 -->
        <router-view />
    </div>

  </div>
</template>

<script setup lang="ts">

import Menu from '@/components/TopMenu.vue'

const menus = [
  { name: '个人资料', path: '/user/center/profile' },
  { name: '改签退票', path: '/user/center/orderchange' },
  { name: '订单中心', path: '/user/center/orderlist' },
]

</script>

<style scoped lang="less">

.user-center-layout {
  width: 1350px;
  margin: 20px auto;
}

.user-center-menu {
  margin-bottom: 20px;
}

.user-center-main {
  min-height: 400px;
}

</style>