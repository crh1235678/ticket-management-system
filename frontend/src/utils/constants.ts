//常量配置

//使用export default默认导出对象，方便给其他文件引入使用
export default {
    // token
    TOKEN: 'TOKEN',


    // 普通用户信息
    USER_INFO: 'USER_INFO',
    // 管理员信息
    ADMIN_INFO: 'ADMIN_INFO',

    // 普通用户头像
    USER_HEADURL: 'USER_HEADURL',
    // 管理员头像
    ADMIN_HEADURL: 'ADMIN_HEADURL',


    // 默认头像
    DEFAULT_AVATAR: 'upload/defaultavatar.png',
    
    //引用环境变量根据不同的启动方式（npm run dev和npm run build）获取不同的值（开发环境和生产环境里的api地址）
    BASE_URL: import.meta.env.VITE_API_URL,

    PAGE_SIZE: 10,

    PAGE_NUM: 1,

    PAGE_ADMIN_LOGIN: '/LOGIN'
}