import { createApp } from 'vue'
import { createPinia } from 'pinia'

//引入element-plus,现成组件库
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
//引入element-plus图标库
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
//引入element-plus全局中文语言包
import zhCn from 'element-plus/es/locale/lang/zh-cn'
//引入全局样式,后续可以不用加
import '@/styles/styles.less'


import App from './App.vue'
import router from './router'

//创建了根组件的应用
const app = createApp(App)
//能够让页面显示element-plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
//注册pinia，将pinia实例注入到app应用中使得所有组件都可以访问到这个状态管理器上
app.use(createPinia())
//注册路由，告诉app应用如何使用路由
app.use(router)
//注册element-plus，安装到app应用中，并传入element-plus全局中文语言包
app.use(ElementPlus, {
  locale: zhCn,
})
//把根组件挂载到页面id为app的元素上，也就是指定挂载点为app  
app.mount('#app')
