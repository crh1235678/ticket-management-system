<template>
  <el-menu
    :default-active="activePath"
    mode="horizontal"
    class="front-menu"
    @select="handleSelect"
  >
    <el-menu-item
      v-for="item in menus"
      :key="item.path"
      :index="item.path"
    >
      {{ item.name }}
    </el-menu-item>
  </el-menu>
</template>

<script setup lang="ts">

import { useRouter, useRoute } from 'vue-router'
import { ref, watch } from 'vue'

interface MenuItem {
  name: string
  path: string
}

const props = defineProps<{
  menus: MenuItem[]
}>()

const router = useRouter()
const route = useRoute()

const activePath = ref(route.path)

watch(
  () => route.path,
  (val) => {
    activePath.value = val
  }
)

const handleSelect = (key: string) => {
  router.push(key)
}

</script>

<style scoped>
.front-menu{
  border-bottom:none;
}
:deep(.el-menu-item){
  font-size: 22px;
}
</style>