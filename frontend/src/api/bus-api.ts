import { get, post } from '@/utils/request'

export const busApi = {
    
  // 查询列车列表，支持分页+条件查询。
  /** 这里有个问题前台和后台看到的巴士信息应该不一样（用户不能看到下架的巴士信息），
      后续要修改就是要做接口分离 
  */ 
  // 获取巴士
  getlist: (pageNum: number, pageSize: number, params?: any) => {
    return get('/bus/list?pageNum=' + pageNum + '&pageSize=' + pageSize, params)
  },

  // 新增巴士
  add: (params: any) => {
    return post('/bus/add', params)
  },

  // 修改巴士
  update: (params: any) => {
    return post('/bus/update', params)
  },

  // 删除巴士
  delete: (ids: any) => {
    return post('/bus/del?ids=' + ids, {})
  },
}