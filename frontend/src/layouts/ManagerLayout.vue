<template>
  <div class="common-layout">
    <el-container>
      <el-header style="height: 90px;">
        <AdminHeader></AdminHeader>
      </el-header>
      <el-container>
        <el-aside class="vaeee-sidebar vaeee-sidebar--dark">
          <!--           <AdminLeftMenu></AdminLeftMenu> -->
          <AdminLeftMenu :menus="menus"></AdminLeftMenu>
        </el-aside>

        <el-container>
          <div class="vaeee-content__wrapper">
            <el-main class="vaeee-content">
              <!-- el卡片组件 -->
              <el-card :style="siteContent.siteContentHeight">
                <!-- 路由占位符,在主体部分显示不同页面及路由组件。 
              这是嵌套路由-->
                <router-view></router-view>
              </el-card>
            </el-main>
            <el-footer class="layout-footer">
              <AdminFooter></AdminFooter>
            </el-footer>
          </div>
        </el-container>
      </el-container>
    </el-container>
  </div>
</template>

<!-- setup语法糖,将vue组件引入为依赖 -->
<!-- lang告诉编译器，当前组件使用的是什么语法 -->
<script setup lang="ts">
//引入组件
import AdminHeader from '@/components/layouts/AdminHeader.vue';
/*  import AdminLeftMenu from '@/components/LeftMenu.vue'; */
import AdminFooter from '@/components/layouts/Footer.vue';
import AdminLeftMenu from '@/components/LeftMenu.vue';
import { Document } from '@element-plus/icons-vue'

//从 Vue 中引入创建响应式数据的 API
import { ref, reactive } from 'vue'

//定义内容区域常量 站点内容高度
//recative() 创建响应式数据对象,数据改变时，前端会监听接收数据并更新视图
const siteContent = reactive({ //定义响应式对象
  documentClientHeight: 0,
  siteContentHeight: {}
})
//定义菜单栏目数据
const menus = [
  {
    name: '首页',
    path: '/admin/index/welcome',
    icon: Document
  },
  {
    name: '用户管理',
    icon: Document,
    children: [
      {
        name: '用户信息',
        path: '/admin/index/u_list'
      },
      {
        name: '管理员信息',
        path: '/admin/index/a_list'
      }
    ]
  },
  {
    name: '交通管理',
    icon: Document,
    children: [
      {
        name: '航班信息',
        path: '/admin/index/f_list'
      },
      {
        name: '列车信息',
        path: '/admin/index/t_list'
      },
      {
        name: '巴士信息',
        path: '/admin/index/b_list'
      },
    ]
  },
  {
    name: '订单管理',
    icon: Document,
    path: '/admin/index/o_list'
  },
  {
    name: '文章管理',
    icon: Document,
    path: '/admin/index/art_list'
  },
]

//获取站点内容高度,后台系统通常希望 整个页面固定高度，不出现整个页面滚动条。所以需要计算高度。
function getSiteContentHeight() {  //定义计算高度的函数
  //用来获取 当前网页可视区域的高度
  siteContent.documentClientHeight = document.documentElement.clientHeight
  //let 声明一个可以改变值的变量
  let height = siteContent.documentClientHeight - 90 - 100
  siteContent.siteContentHeight = { minHeight: height + 'px' }

}

getSiteContentHeight();

//浏览器窗口变化时，会触发 resize 事件重新计算高度
window.onresize = function () {  //监听窗口大小改变
  getSiteContentHeight();
}

</script>


<style lang="less"></style>
