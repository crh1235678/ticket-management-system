<template>
  <!-- 外层包裹一层，统一圆角 -->
  <div class="carousel-wrapper" :style="{ height }">
    <el-carousel :height="height">
      <el-carousel-item
        v-for="item in images"
        :key="item.name"
      >
        <img
          :src="item.url"
          :alt="item.name"
          class="carousel-img"
        />
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup lang="ts">
interface CarouselItem {
  name: string
  url: string
}

defineProps<{
  images: CarouselItem[]
  height?: string
}>()
</script>

<style scoped lang="less">
/* ⭐核心：统一圆角 */
.carousel-wrapper {
  width: 100%;
  border-radius: 10px;
  overflow: hidden; /* ⭐关键裁剪 */
}

/* ⭐强制子组件继承圆角（Element UI内部） */
.carousel-wrapper :deep(.el-carousel),
.carousel-wrapper :deep(.el-carousel__container) {
  border-radius: 10px;
}

/* 图片填充 */
.carousel-img {
  width: 100%;
  height: 100%;
  object-fit: cover; /* ⭐建议改成 cover，更饱满 */
}
</style>