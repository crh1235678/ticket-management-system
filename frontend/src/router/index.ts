import { createRouter, createWebHistory } from 'vue-router'
import { useAdminStore } from '@/stores/admin'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import bg_index from '@/assets/images/front/header-1.avif'


// 创建路由器实例
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [


    // 根路径重定向到登录页面
    {
      path: '/',
      redirect: '/user/login',
    },
    // 用户登录页面
    {
      path: '/user/login',
      name: 'UserLogin',
      component: () => import('@/views/login/UserLogin.vue'),
    },
    // 用户注册页面
    {
      path: '/user/register',
      name: 'Register',
      component: () => import('@/views/Register.vue'),
    },
    // 前台页面
    {
      path: '/user',
      name: 'UserLayout',
      redirect: '/user/home',
      component: () => import('@/layouts/FrontLayout.vue'),
      meta: {
        headerBgImage: bg_index,
      },
      children: [
        {
          path: 'home',
          name: 'UserHome',
          component: () => import('@/views/front/Home.vue'),
        },
        {
          path: 'center',
          name: 'UserCenter',
          component: () => import('@/layouts/UserCenterLayout.vue'),
          redirect: '/user/center/profile',
          children: [
            {
              path: 'profile',
              name: 'UserCenterProfile',
              component: () => import('@/views/front/user-center/Profile.vue'),
            },
            {
              path: 'orderlist',
              name: 'UserCenterOrderList',
              component: () => import('@/views/front/user-center/OrderList.vue'),
            },
            {
              path: 'orderchange',
              name: 'UserCenterOrderChange',
              component: () => import('@/views/front/user-center/OrderChange.vue'),
            },
          ],
        },
        {
          path: 'order',
          name: 'UserOrder',
          component: () => import('@/layouts/OrderLayout.vue'),
          redirect: '/user/order/flightorder',
          children: [
            {
              path: 'flightorder',
              name: 'FlightOrder',
              component: () => import('@/views/front/order/FlightOrder.vue'),
            },
            {
              path: 'trainorder',
              name: 'TrainOrder',
              component: () => import('@/views/front/order/TrainOrder.vue'),
            },
            {
              path: 'busorder',
              name: 'BusOrder',
              component: () => import('@/views/front/order/BusOrder.vue'),
            },
          ],
        },
        {
          path: 'article',
          name: 'ArticleCatalog',
          component: () => import('@/views/front/article/ArticleCatalog.vue'),
        },
        {
          path: 'article/:id',
          name: 'ArticleDetail',
          component: () => import('@/views/front/article/ArticleDetail.vue'),
        }

      ],
    },
    // 管理员登录页面
    {
      path: '/admin/login',
      name: 'AdminLogin',
      component: () => import('@/views/login/AdminLogin.vue'),
    },
    // 后台管理页面
    {
      path: '/admin/index',
      name: 'Index',
      //表示当用户访问当前路由时，会自动重定向到/admin/index/welcome
      redirect: '/admin/index/welcome',
      component: () => import('@/layouts/ManagerLayout.vue'),
      //嵌套路由配置，嵌套在ManagerLayout.vue中
      children: [
        {
          // 当路径不写/时，router必须push/admin/Index/welcome 才能匹配到，就是需要写父级路径
          // WelcomeVue 将被渲染到 Index.vue 的 <router-view> 内部
          path: 'welcome',
          name: 'Welcome',
          component: () => import('@/views/manager/WelcomeVue.vue'),
        },
        {
          path: 'a_list',
          name: 'Admin',
          component: () => import('@/views/manager/Admin.vue'),
        },
        {
          path: 'u_list',
          name: 'User',
          component: () => import('@/views/manager/User.vue'),
        },
        {
          path: 'f_list',
          name: 'Flight',
          component: () => import('@/views/manager/Flight.vue'),
        },
        {
          path: 't_list',
          name: 'Train',
          component: () => import('@/views/manager/Train.vue'),
        },
        {
          path: 'b_list',
          name: 'Bus',
          component: () => import('@/views/manager/Bus.vue'),
        },
        {
          path: 'o_list',
          name: 'Order',
          component: () => import('@/views/manager/Order.vue'),
        },
        {
          path: 'art_list',
          name: 'Article',
          component: () => import('@/views/manager/Article.vue'),
        },
      ],
    },
  ],
})

//当一个导航触发时，全局前置守卫按照创建顺序调用。守卫是异步解析执行，此时导航在所有守卫 resolve 完之前一直处于等待中。
//如何去处理promise的resolve和reject是router.beforeEach的内部处理.
router.beforeEach(async (to, from) => {
  // 放行登录注册页
  if (
    to.path === '/user/login' ||
    to.path === '/user/register' ||
    to.path === '/admin/login'
  ) {
    return true
  }

  // 获取统一 token
  const token = useUserStore().getToken

  // 可选：提示用户未登录，但不阻止访问
  if (!token) {
    ElMessage.info('您未登录，访问可能受限')
  }

  return true
})

// 导出全局注册
export default router
