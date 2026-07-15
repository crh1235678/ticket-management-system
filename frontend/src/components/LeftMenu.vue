<template>
  <div class="vaeee-sidebar__inner">
    <el-menu
      :default-active="activePath"
      class="vaeee-sidebar__menu"
      background-color="#263238"
      text-color="#8a979e"
      active-text-color="#fff"
      router
    >
      <!-- 遍历菜单 -->
      <!-- 遍历menus以name为key 遍历菜单项 -->
      <template v-for="item in menus" :key="item.name">
        
        <!-- 一级菜单（无子菜单） -->
        <el-menu-item
          v-if="!item.children"
          :index="item.path"
        >
          <el-icon v-if="item.icon">
            <component :is="item.icon" />
          </el-icon>
          <span>{{ item.name }}</span>
        </el-menu-item>

        <!-- 二级菜单 -->
        <el-sub-menu v-else :index="item.name">
          <template #title>
            <el-icon v-if="item.icon">
              <component :is="item.icon" />
            </el-icon>
            <span>{{ item.name }}</span>
          </template>

          <el-menu-item
            v-for="child in item.children"
            :key="child.path"
            :index="child.path"
          >
            {{ child.name }}
          </el-menu-item>
        </el-sub-menu>

      </template>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import { ref, watch } from 'vue'
import { Document } from '@element-plus/icons-vue'

//类型声明
interface MenuItem {
  name: string
  path?: string
  icon?: any
  children?: MenuItem[]
}

// 定义组件属性，接收接收父组件传递的菜单数据(这里是菜单数据数组)
const props = defineProps<{
  menus: MenuItem[]
}>()

const route = useRoute()
const activePath = ref(route.path)

// 路由变化高亮
watch(
  () => route.path,
  (val) => {
    activePath.value = val
  }
)
</script>