<template>
  <div>
    <el-container>
      <el-header style="height: 155px;background: #E8F5F2;padding: 0;">
        <FrontHeader :bgImage="bg" />
      </el-header>
      <el-body>
        <div class="front-body">
          <div class="front-body-hero">
            <!-- hero-left不动 -->
            <div class="hero-left">
              <Carousel :images="homeImages" height="487.5px" />
            </div>
            <div class="hero-right">
              <el-row>
                <el-col :span="24" style="font-size: 30px;">
                  xxxxxxxxxx
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="8">xxxxxxxxxx</el-col>
                <el-col :span="8">xxxxxxxxxx</el-col>
                <el-col :span="8">xxxxxxxxxx</el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="8">xxxxxxxxxx</el-col>
                <el-col :span="8">xxxxxxxxxx</el-col>
                <el-col :span="8">xxxxxxxxxx</el-col>
              </el-row>
              <button style="cursor: pointer;">xxxxxxxxxx</button>

            </div>

          </div>
          <!-- min-height="300px 设置最下高度，后续添加内容可以给别的盒子 -->
          <div class="front-body-transport" style="min-height: 300px;">
            <div>xxxxxxxxxx</div>
            <div class="transport-wrapper">
              <div style="text-align: center;flex: 1;cursor: pointer;">
                <img src="@/assets/images/front/plane.png" alt="plane">
                <div>xxxxxxxxxx</div>
              </div>

              <div style="text-align: center;flex: 1;cursor: pointer;">
                <img src="@/assets/images/front/bus.png" alt="bus">
                <div>xxxxxxxxxx</div>
              </div>
              <div style="text-align: center;flex: 1;cursor: pointer;">
                <img src="@/assets/images/front/train.png" alt="train">
                <div>xxxxxxxxxx</div>
              </div>
            </div>

          </div>

        </div>
      </el-body>
    </el-container>
  </div>
</template>


<script setup lang="ts">

import FrontHeader from '@/components/layouts/FrontHeader.vue';
import Carousel from '@/components/Carousel.vue';
import bg from '@/assets/images/front/header-1.avif'
import carousel1 from '@/assets/images/front/carousel-1.png'
import carousel2 from '@/assets/images/front/carousel-2.png'
import carousel3 from '@/assets/images/front/carousel-3.png'
import carousel4 from '@/assets/images/front/carousel-4.png'

const homeImages = [
    { name: 'home-1', url: carousel1 },
    { name: 'home-2', url: carousel2 },
    { name: 'home-3', url: carousel3 },
    { name: 'home-4', url: carousel4 },
]

</script>


<style lang="less">
//引入全局样式
@import url('@/styles/styles.less');
//后续加入styles中，超出部分显示省略号
.overflowShow {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
</style>
