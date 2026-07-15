import { get, post } from '@/utils/request'

export const adminApi = {

    // 当后端接口使用 @RequestParam 或 GET 请求时，参数需要拼接在 URL 后面；当后端使用 @RequestBody 接收数据时，前端才传 JSON 到请求体中。
    // 新增管理员
    add: (params: any) => {
        return post('/admin/add', params)
    },
    // 修改管理员
    update: (params: any) => {
        return post('/admin/update', params)
    },

    // 删除管理员
    // 因为post在之前设计里必须要传data{}，但是删除只用id，所以这里传一个空对象
    delete: (ids: any) => {
        return post('/admin/del?ids=' + ids, {})
    },

    // 查询管理员列表
    queryPageList: (params: any, pageNum: any, pageSize: any) => {
        return get('/admin/list?pageNum=' + pageNum + '&pageSize=' + pageSize, params)
    },

    // 管理员登录
    login: (params: any) => {
        return post('/admin/login', params)
    },

    // 管理员登出
    logout: (params: any) => {
        return post('/admin/logout', params)
    },
}
