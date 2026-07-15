import { get, post } from '@/utils/request'

export const articleApi = {
  // 获取文章列表
  getlist: (pageNum: number, pageSize: number, params?: any) => {
    return get('/article/list?pageNum=' + pageNum + '&pageSize=' + pageSize, params)
  },

  // 获取详情
  getDetail: (id: number, params?: any) => {
    return get(`/article/${id}`, params)
  },

  // 创建文章
  add: (params: any) => {
    return post('/article/add', params)
  },

  // 删除文章
  delete: (ids: any) => {
    return post('/article/del?ids=' + ids, {})
  },

  // 修改文章
  update: (params: any) => {
    return post('/article/update', params)
  },

  like: (id: number) => {
    return post('/article/like?id=' + id, {})
  },
}