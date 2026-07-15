<template>
  <div class="user-order-layout">

    <!-- 顶部菜单 -->
    <div class="user-order-menu">
      <Menu :menus="menus" />
    </div>

    <div class="user-order-main">
        <!-- 子页面 -->
        <router-view />
    </div>

  </div>
</template>

<script setup lang="ts">

import Menu from '@/components/TopMenu.vue'

const menus = [
  { name: '机票查询', path: '/user/order/flightorder' },
  { name: '车票查询', path: '/user/order/trainorder' },
  { name: '巴士查询', path: '/user/order/busorder' },
]

</script>

<style scoped lang="less">

.user-order-layout {
  width: 100%;
  margin: 20px auto;
}

.user-order-menu {
  margin-bottom: 20px;
}

.user-order-main {
  min-height: 400px;
}

</style>