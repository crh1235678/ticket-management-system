import 'vue-router'

//declare module 对 vue-router 模块进行类型扩展
declare module 'vue-router' {
  interface RouteMeta {
    headerBgImage?: string
    headerBgColor?: string
  }
}