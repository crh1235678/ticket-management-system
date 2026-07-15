<!-- 全站固定导航栏 -->
<template>
  <div class="front-header" :style="headerStyle">
    <div class="left">
      <img src="@/assets/images/login/logo.png" alt="">
      <div class="title">伴行</div>

      <div class="navigation">
        <el-menu class="nav-menu" ellipsis mode="horizontal" popper-offset="10"  router>
          <el-menu-item index="/user/home">首页</el-menu-item>
          <el-menu-item index="/user/order">出行预定</el-menu-item>
          <el-menu-item index="/user/article">文章目录</el-menu-item>
          <el-menu-item index="/user/center">个人中心</el-menu-item>
        </el-menu>
      </div>
    </div>


    <div class="right">
      <!-- 显示用户头像跟随状态信息实时更新 -->
      <img class="avatar" :src="getFileUrl(userStore.user.headurl)" alt="">
      <el-button type="primary" size="medium" @click="logout">退出登录</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ref, onMounted } from 'vue'
import { getFileUrl } from '@/utils/utils'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user-api'


const keyword = ref('')
const userStore = useUserStore()


// 定义组件的属性
interface Props {
  height?: string
  bgColor?: string
  bgImage?: string
}

const props = withDefaults(defineProps<Props>(), {
  height: '155px',
  bgColor: '#E8F5F2',
  bgImage: ''
})

// 计算动态样式
/**
 * 使用 computed 来生成 headerStyle，
 * 因为它依赖 props 动态变化。computed 会自动追踪依赖
 * 当 props 变化时重新计算样式对象，同时缓存结果，提高渲染性能
 * 保证 DOM 样式与数据一致。
 */
const headerStyle = computed(() => {
  // 创建一个空对象key-value都是String
  const style: Record<string, string> = {
    height: props.height
  }

  if (props.bgImage) {
    style.backgroundImage = `url(${props.bgImage})`
    style.backgroundSize = 'cover'
    style.backgroundPosition = 'center'
  } else {
    style.background = props.bgColor
  }

  return style
})

async function logout() {
    try {
        await userApi.logout();
        window.location.href = "/user/login";
    } catch (error) {
        console.log(error);
    }finally {
        // 登出成功后，清空本地存储,也就是清空登录信息状态
        userStore.logout();
    }
}


</script>


<style scoped lang="less">
.front-header {
  width: 100%;
  display: flex;
  align-items: center;
  // 两端贴边，中间平均分配间距
  justify-content: space-between;
  padding: 0 60px;
  box-sizing: border-box;

  /* 轻渐变背景 */
  background: linear-gradient(135deg, #e8f5f2 0%, #f4fbfa 100%);
  backdrop-filter: blur(10px);

  /* 阴影增强层次 */
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);

  transition: all 0.3s ease;

  .left {
    display: flex;
    align-items: center;
    flex: 1;

    img {
      height: 40px;
      width: 40px;
      border-radius: 50%;
      transition: transform 0.3s ease;
    }

    img:hover {
      transform: rotate(8deg) scale(1.05);
    }

    .title {
      color: #ffffff;
      cursor: pointer;
      margin-left: 12px;
      font-size: 22px;
      font-weight: 500;
      letter-spacing: 0;
      text-shadow: 0 2px 6px rgba(0, 0, 0, 0.25);
      transition: color 0.2s ease;
    }

    .title:hover {
      color: #fb7299; // B站粉
    }

    .navigation {
      margin-left: 40px;
      flex: 1;
    }

    .nav-menu {
      background: transparent;
      border-bottom: none;
    }

    /* 菜单 hover 优化 */
    :deep(.el-menu-item),
    :deep(.el-sub-menu__title) {
      color: #ffffff;
      font-size: 20px;
      font-weight: 500;
      background: transparent !important;
      transition: all 0.2s ease;
    }

    :deep(.el-menu-item:hover),
    :deep(.el-sub-menu__title:hover) {
      color: #fb7299;
      background: transparent !important;
    }
  }

  .center {
    flex: 1;
    display: flex;
    justify-content: center;

    .search-box {
      width: 420px;
    }

    .search-input {
      border-radius: 30px;
      overflow: hidden;
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
      transition: all 0.3s ease;
      height: 48px;
    }

    .search-input:hover {
      box-shadow: 0 6px 18px rgba(64, 158, 255, 0.25);
      transform: translateY(-2px);
    }
  }

  .right {
    display: flex;
    align-items: center;
    gap: 12px;

    .avatar {
      width: 42px;
      height: 42px;
      border-radius: 50%; // 圆形
      border: 3px solid #fff; // 白边
      object-fit: cover; // 图片不变形
      cursor: pointer;

      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
      transition: all 0.3s ease;
    }

    .avatar:hover {
      transform: scale(1.08);
    }

    button {
      border-radius: 20px;
      padding: 8px 20px;
      font-weight: 500;
      transition: all 0.3s ease;
    }

    /* 登录按钮 */
    button:first-child {
      background: transparent;
      color: #409eff;
      border: 1px solid #409eff;
    }

    button:first-child:hover {
      background: #409eff;
      color: white;
    }

    /* 注册按钮 */
    button:last-child {
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.25);
    }

    button:last-child:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 18px rgba(64, 158, 255, 0.35);
    }
  }
}
</style>