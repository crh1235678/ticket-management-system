import { get, post } from '@/utils/request'
import { pa } from 'element-plus/es/locales.mjs'
import { useUserStore } from '@/stores/user'

export const userApi = {

    // 当后端接口使用 @RequestParam 或 GET 请求时，参数需要拼接在 URL 后面；当后端使用 @RequestBody 接收数据时，前端才传 JSON 到请求体中。
    // 新增用户
    add: (params: any) => {
        return post('/user/add', params)
    },
    // 修改用户
    update: (params: any) => {
        return post('/user/update', params)
    },
    // 删除用户
    // 因为post在之前设计里必须要传data{}，但是删除只用id，所以这里传一个空对象
    delete: (ids: any) => {
        return post('/user/del?ids=' + ids, {})
    },
    // 查询用户列表
    queryPageList: (params: any, pageNum: any, pageSize: any) => {
        return get('/user/list?pageNum=' + pageNum + '&pageSize=' + pageSize, params)
    },

    // 用户登录
    login: (params: any) => {
        return post('/user/login', params)
    },

    // 用户登出
    logout: (params: any) => {
        return post('/user/logout', params)
    },

    // 用户注册
    register: (params: any) => {
        return post('/user/register', params)
    },

    // 修改密码
    changePassword: (params: any) => {
        return post('/user/changePassword', params)
    },

    // 查询用户信息
    getUserInfo: (params: any) => {
        return get('/user/me', params)
    },

    // 充值
    recharge: (params: any) => {
        return post('/user/recharge', params)
    },

}
