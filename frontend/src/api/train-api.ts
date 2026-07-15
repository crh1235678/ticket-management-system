import { get, post } from '@/utils/request'

export const trainApi = {
    
  // 查询列车列表，支持分页+条件查询。
  /** 这里有个问题前台和后台看到的航班信息应该不一样（用户不能看到下架的航班信息），
      后续要修改就是要做接口分离 
  */ 
  // 获取列车
  getlist: (pageNum: number, pageSize: number, params?: any) => {
    return get('/train/list?pageNum=' + pageNum + '&pageSize=' + pageSize, params)
  },

  // 新增列车
  add: (params: any) => {
    return post('/train/add', params)
  },

  // 修改列车
  update: (params: any) => {
    return post('/train/update', params)
  },

  // 删除列车
  delete: (ids: any) => {
    return post('/train/del?ids=' + ids, {})
  },
}